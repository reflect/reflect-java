package io.reflect.token;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class TokenGenerator {
    public static String generate(String secretKey, Parameter[] params) throws Exception {
        String[] str = new String[params.length];

        for (int i = 0; i < params.length; i++) {
            str[i] = params[i].toJsonString();
        }

        Arrays.sort(str);
        String fullString = String.format("V2\n%s", String.join("\n", str));

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(key);
            return String.format("=2=%s", Base64.getEncoder().encodeToString(mac.doFinal(fullString.getBytes())));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new Exception(e);
        }
    }
}