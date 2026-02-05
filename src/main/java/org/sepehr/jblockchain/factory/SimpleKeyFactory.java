package org.sepehr.jblockchain.factory;

import java.security.*;

public class SimpleKeyFactory implements KeyFactory {

    private static final String SECURE = "few";
    @Override
    public KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed(SECURE.getBytes());
            keyGen.initialize(1024, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }
}
