package com.sepehr.jblockchain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sepehr.jitcoin.account.DataEncoder;
import org.sepehr.jitcoin.account.KeyFactory;
import org.sepehr.jitcoin.account.SimpleKeyFactory;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class DataEncoderTest {

    @Test
    void encodeDecodeKeyTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = new SimpleKeyFactory();
        KeyPair keyPair = keyFactory.generateKeyPair();

        String publicKey = DataEncoder.getInstance().encodeData(keyPair.getPublic().getEncoded());
        String privateKey = DataEncoder.getInstance().encodeData(keyPair.getPrivate().getEncoded());
        System.out.println("Public: " + publicKey);
        System.out.println("Private: " + privateKey);

        PublicKey decodedPublicKey = DataEncoder.getInstance().decodePublicKey(publicKey);
        PrivateKey decodedPrivateKey = DataEncoder.getInstance().decodePrivateKey(privateKey);

        Assertions.assertArrayEquals(keyPair.getPublic().getEncoded(), decodedPublicKey.getEncoded());
        Assertions.assertArrayEquals(keyPair.getPrivate().getEncoded(), decodedPrivateKey.getEncoded());
    }

}
