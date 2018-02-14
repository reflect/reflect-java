package io.reflect.token;

import java.nio.ByteBuffer;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

final class SecretKeyUtils {
    static SecretKey secretKeyFromUUID(String uuid) {
        try {
            UUID secretKeyUUID = UUID.fromString(uuid);
            ByteBuffer secretKeyBytes = ByteBuffer.wrap(new byte[16]);
            secretKeyBytes.putLong(secretKeyUUID.getMostSignificantBits());
            secretKeyBytes.putLong(secretKeyUUID.getLeastSignificantBits());

            return new SecretKeySpec(secretKeyBytes.array(), "AES");
        } catch (IllegalArgumentException iae) {
            throw new InvalidSecretKeyException();
        }
    }
}
