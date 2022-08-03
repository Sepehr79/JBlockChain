package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.KeyFactory;

import java.security.*;

public class KeyFactoryImp implements KeyFactory {

    @Override
    public KeyPair generateKeyPair(String... seed) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed(String.join("", seed).getBytes());
            keyGen.initialize(2048, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }
}
