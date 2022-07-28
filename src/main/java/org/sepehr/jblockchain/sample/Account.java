package org.sepehr.jblockchain.sample;

import org.sepehr.jblockchain.block.BlockBody;

import java.math.BigDecimal;

public class Account implements BlockBody {

    private final String privateKey;

    private final String publicKey;

    private final long createdTimestamp;

    private final String[] recoveryCodes;

    private final BigDecimal amount;

    public Account(final String privateKey,
                   final String publicKey,
                   final long createdTimestamp,
                   final String[] recoveryCodes,
                   final BigDecimal amount) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.createdTimestamp = createdTimestamp;
        this.recoveryCodes = recoveryCodes;
        this.amount = amount;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String[] getRecoveryCodes() {
        return recoveryCodes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "publicKey='" + publicKey + '\'' +
                '}';
    }
}
