package org.sepehr.jitcoin.account;

import java.security.*;
import java.util.Random;

public class SimpleKeyFactory implements KeyFactory {

    private static final Random RANDOM = new Random();

    @Override
    public KeyPair generateKeyPair() {
        return generateKeyPair(RANDOM.nextInt());
    }

    public KeyPair generateKeyPair(int random) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed(random);
            keyGen.initialize(1024, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }

    public KeyPair generateBaseKeyPair() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed(42);
            keyGen.initialize(1024, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }
}
