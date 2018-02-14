package io.reflect.token;

import static org.junit.Assert.*;
import org.junit.Test;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

public class TokenBuilderTest {
    private static class KeyPair {
        private final String accessKey = UUID.randomUUID().toString();
        private final String secretKey = UUID.randomUUID().toString();
    }

    @Test
    public void simple() throws TokenEncryptionException, ParseException, JOSEException, KeyLengthException {
        KeyPair kp = new KeyPair();

        String token = new TokenBuilder(kp.accessKey)
                .build(kp.secretKey);

        EncryptedJWT jwt = EncryptedJWT.parse(token);
        assertEquals(kp.accessKey, jwt.getHeader().getKeyID());

        jwt.decrypt(new DirectDecrypter(SecretKeyUtils.secretKeyFromUUID(kp.secretKey)));
    }

    @Test
    public void expiration() throws TokenEncryptionException, ParseException, JOSEException, KeyLengthException {
        KeyPair kp = new KeyPair();

        Date expiration = Date.from(LocalDateTime.now().plus(15, ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC));

        String token = new TokenBuilder(kp.accessKey)
                .expiration(expiration)
                .build(kp.secretKey);

        EncryptedJWT jwt = EncryptedJWT.parse(token);
        assertEquals(kp.accessKey, jwt.getHeader().getKeyID());

        jwt.decrypt(new DirectDecrypter(SecretKeyUtils.secretKeyFromUUID(kp.secretKey)));

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        assertTrue("token is not yet valid", claims.getNotBeforeTime().before(new Date()));
        assertEquals(
                expiration.toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli(),
                claims.getExpirationTime().getTime());
    }

    @Test
    public void claims() throws TokenEncryptionException, ParseException, JOSEException, KeyLengthException {
        KeyPair kp = new KeyPair();

        Parameter parameter = new Parameter("user-id", Parameter.Op.EQUALS, "1234");

        String token = new TokenBuilder(kp.accessKey)
                .addViewIdentifier("SecUr3View1D")
                .setAttribute("user-id", 1234)
                .setAttribute("user-name", "Billy Bob")
                .addParameter(parameter)
                .build(kp.secretKey);

        EncryptedJWT jwt = EncryptedJWT.parse(token);
        assertEquals(kp.accessKey, jwt.getHeader().getKeyID());

        jwt.decrypt(new DirectDecrypter(SecretKeyUtils.secretKeyFromUUID(kp.secretKey)));

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        assertArrayEquals(new String[]{"SecUr3View1D"}, claims.getStringArrayClaim(TokenBuilder.VIEW_IDENTIFIERS_CLAIM_NAME));

        List<?> parameters = (List<?>)claims.getClaim(TokenBuilder.PARAMETERS_CLAIM_NAME);
        assertTrue("parameters claim does not have exactly one entry", parameters.size() == 1);
        JSONObject serializedParameter = (JSONObject)JSONValue.parse(JSONValue.toJSONString(parameter));
        assertEquals(serializedParameter, parameters.iterator().next());

        JSONObject attributes = claims.getJSONObjectClaim(TokenBuilder.ATTRIBUTES_CLAIM_NAME);
        assertTrue("attributes claim does not have exactly two entries", attributes.size() == 2);
        assertEquals(1234L, attributes.get("user-id"));
        assertEquals("Billy Bob", attributes.get("user-name"));
    }
}
