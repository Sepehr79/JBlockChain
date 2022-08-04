package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.KeyFactory;

import java.security.*;

public class KeyFactoryImp implements KeyFactory {

    @Override
    public KeyPair generateKeyPair(long timestamp, String[] seeds) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            secureRandom.setSeed((timestamp + String.join("", seeds)).getBytes());
            keyGen.initialize(1024, secureRandom);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException noSuchAlgorithmException) {
            return null;
        }
    }
}
