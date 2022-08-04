package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.AccountFactory;
import org.sepehr.jblockchain.factory.KeyFactory;
import org.sepehr.jblockchain.factory.RecoveryCodeFactory;
import org.sepehr.jblockchain.sample.Account;

import java.security.KeyPair;

public class AccountFactoryImp implements AccountFactory {

    private final KeyFactory keyFactory;

    private final RecoveryCodeFactory recoveryCodeFactory;

    public AccountFactoryImp(final KeyFactory keyFactory,
                             final RecoveryCodeFactory recoveryCodeFactory) {
        this.keyFactory = keyFactory;
        this.recoveryCodeFactory = recoveryCodeFactory;
    }

    public Account buildAccount() {
        final long createdTimestamp  = System.currentTimeMillis();
        final String[] recoveryCodes = recoveryCodeFactory.generateRandomRecoveryCodes();
        final KeyPair keyPair = keyFactory.generateKeyPair(createdTimestamp, recoveryCodes);
        return new Account(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                createdTimestamp,
                recoveryCodes
        );
    }


}
