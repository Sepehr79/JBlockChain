package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.AccountFactory;
import org.sepehr.jblockchain.factory.KeyFactory;
import org.sepehr.jblockchain.factory.RecoveryCodeFactory;
import org.sepehr.jblockchain.sample.Account;

import java.security.KeyPair;

public class AccountFactoryImp implements AccountFactory {

    private final KeyFactory keyFactory;

    public AccountFactoryImp(final KeyFactory keyFactory) {
        this.keyFactory = keyFactory;
    }

    public Account buildAccount() {
        final KeyPair keyPair = keyFactory.generateKeyPair();
        return new Account(
                keyPair.getPrivate(),
                keyPair.getPublic()
        );
    }


}
