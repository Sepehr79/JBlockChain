package org.sepehr.jblockchain.factory;

import java.security.KeyPair;

public class SimpleAccountFactory implements AccountFactory {

    private final KeyFactory keyFactory;

    public SimpleAccountFactory(final KeyFactory keyFactory) {
        this.keyFactory = keyFactory;
    }

    public Account buildAccount() {
        final KeyPair keyPair = keyFactory.generateKeyPair();
        return new Account(
                keyPair.getPrivate(),
                keyPair.getPublic()
        );
    }

    public Account baseAccount() {
        final KeyPair keyPair = ((SimpleKeyFactory) keyFactory).generateBaseKeyPair();
        return new Account(keyPair.getPrivate(), keyPair.getPublic());
    }


}
