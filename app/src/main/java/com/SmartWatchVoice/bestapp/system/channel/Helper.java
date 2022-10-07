package com.SmartWatchVoice.bestapp.system.channel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Helper {
    public static final String makeCodeChallenge(String code) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(code.getBytes(StandardCharsets.UTF_8));
            byte[] encodedBase64 = Base64.getUrlEncoder().withoutPadding().encode(encodedHash);
            return new String(encodedBase64, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String makePartBoundary(){
        return UUID.randomUUID().toString();
    }
}
