package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.KeyFactory;

import java.security.*;

public class KeyFactoryImp implements KeyFactory {

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
