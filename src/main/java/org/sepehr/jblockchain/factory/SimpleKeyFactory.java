package org.sepehr.jblockchain.factory;

import java.security.*;
import java.util.Random;

public class SimpleKeyFactory implements KeyFactory {

    private static final Random RANDOM = new Random();

    @Override
    public KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed(RANDOM.nextInt());
            keyGen.initialize(1024, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }
}
