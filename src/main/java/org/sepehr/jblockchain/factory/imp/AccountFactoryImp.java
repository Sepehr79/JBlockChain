package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.factory.AccountFactory;
import org.sepehr.jblockchain.factory.KeyFactory;
import org.sepehr.jblockchain.factory.RecoveryCodeFactory;
import org.sepehr.jblockchain.sample.Account;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.stream.Stream;

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
        final String[] seeds = Stream.concat(Arrays.stream(new String[] {String.valueOf(createdTimestamp)}), Arrays.stream(recoveryCodes)).toArray(String[]::new);
        final KeyPair keyPair = keyFactory.generateKeyPair(seeds);
        return new Account(
                new BigInteger(1, keyPair.getPrivate().getEncoded()).toString(16), // Convert byte array to hexadecimal string
                new BigInteger(1, keyPair.getPublic().getEncoded()).toString(16),
                createdTimestamp,
                recoveryCodes
        );
    }


}
