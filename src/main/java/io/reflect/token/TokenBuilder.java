package io.reflect.token;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

/**
 * Builder for encrypted tokens.
 *
 * Provides a way to create secure, reusable tokens that enable particular
 * functionality in the Reflect API while disallowing tampering.
 */
public class TokenBuilder {
    static final String VIEW_IDENTIFIERS_CLAIM_NAME = "http://reflect.io/s/v3/vid";
    static final String PARAMETERS_CLAIM_NAME = "http://reflect.io/s/v3/p";
    static final String ATTRIBUTES_CLAIM_NAME = "http://reflect.io/s/v3/a";

    private final String accessKey;
    private Date expiration = null;

    private final List<String> viewIdentifiers = new LinkedList<>();
    private final List<Parameter> parameters = new LinkedList<>();
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * Constructs a new token builder with the given access key, which must
     * not be null.
     *
     * @param accessKey the access key that identifies the project of this
     *                  token
     */
    public TokenBuilder(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Sets the expiration for the constructed token to the given time.
     *
     * After this time, the token will no longer be valid. All requests made
     * using an expired token will fail.
     *
     * @param when the time at which the token will expire
     * @return this token builder
     */
    public TokenBuilder expiration(Date when) {
        this.expiration = when;
        return this;
    }

    /**
     * Adds the given view identifier to the list of view identifiers permitted
     * by this token.
     *
     * If no view identifiers are added to this builder, all views in the given
     * access key's project will be able to be loaded. Otherwise, only those
     * added will be able to be loaded.
     *
     * @param id the view identifier to restrict to
     * @return this token builder
     */
    public TokenBuilder addViewIdentifier(String id) {
        this.viewIdentifiers.add(id);
        return this;
    }

    /**
     * Adds a data-filtering parameter to this token.
     *
     * @param parameter the parameter to add
     * @return this token builder
     */
    public TokenBuilder addParameter(Parameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    /**
     * Sets the given attribute in this token.
     *
     * @param name the name of the attribute
     * @param value the attribute's value, which must be serializable to JSON
     * @return this token builder
     */
    public TokenBuilder setAttribute(String name, Object value) {
        this.attributes.put(name, value);
        return this;
    }

    /**
     * Builds a final copy of the token using the given secret key.
     *
     * @param secretKey the secret key that corresponds to this builder's
     *                  access key
     * @return the encrypted token
     * @throws TokenEncryptionException if the encryption fails
     */
    public String build(String secretKey) throws TokenEncryptionException {
        SecretKey secret = SecretKeyUtils.secretKeyFromUUID(secretKey);

        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM)
                .compressionAlgorithm(CompressionAlgorithm.DEF)
                .keyID(this.accessKey)
                .build();

        Date now = new Date();

        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                .issueTime(now)
                .notBeforeTime(now);

        if (!viewIdentifiers.isEmpty()) {
            claimsSetBuilder.claim(VIEW_IDENTIFIERS_CLAIM_NAME, viewIdentifiers);
        }

        if (!parameters.isEmpty()) {
            claimsSetBuilder.claim(PARAMETERS_CLAIM_NAME, parameters);
        }

        if (!attributes.isEmpty()) {
            claimsSetBuilder.claim(ATTRIBUTES_CLAIM_NAME, attributes);
        }

        if (this.expiration != null) {
            claimsSetBuilder.expirationTime(this.expiration);
        }

        EncryptedJWT jwt = new EncryptedJWT(header, claimsSetBuilder.build());

        try {
            jwt.encrypt(new DirectEncrypter(secret));
            return jwt.serialize();
        } catch (KeyLengthException kle) {
            // This should never happen, because our UUID parsing guards
            // against it.
            throw new RuntimeException(kle);
        } catch (JOSEException je) {
            throw new TokenEncryptionException(je);
        }
    }
}
