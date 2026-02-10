package org.sepehr.jblockchain.account;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DataEncoder {

    private DataEncoder() {}

    private static final DataEncoder DATA_ENCODER = new DataEncoder();

    public String encodeData(byte[] hash) {
        return Base64.getEncoder().encodeToString(hash);
    }

    public byte[] decodeHash(String hash) {
        return Base64.getDecoder().decode(hash);
    }

    public PublicKey decodePublicKey(String pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] pubKeyByte = Base64.getDecoder().decode(pubKey);
        KeyFactory factory = KeyFactory.getInstance("DSA");
        return factory.generatePublic(new X509EncodedKeySpec(pubKeyByte));
    }

    public PrivateKey decodePrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyByte = Base64.getDecoder().decode(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
    }

    public static DataEncoder getInstance() {
        return DATA_ENCODER;
    }

}
