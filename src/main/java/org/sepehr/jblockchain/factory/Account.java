package org.sepehr.jblockchain.factory;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Account {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    public Account(final PrivateKey privateKey,
                   final PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "Account{" +
                "publicKey='" + publicKey + '\'' +
                '}';
    }
}
