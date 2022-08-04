package org.sepehr.jblockchain.sample;

import org.apache.commons.codec.binary.Hex;
import org.sepehr.jblockchain.block.BlockBody;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Account implements BlockBody {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private final long createdTimestamp;

    private final String[] recoveryCodes;

    private final BigDecimal amount;

    public Account(final PrivateKey privateKey,
                   final PublicKey publicKey,
                   final long createdTimestamp,
                   final String[] recoveryCodes) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.createdTimestamp = createdTimestamp;
        this.recoveryCodes = recoveryCodes;
        this.amount = new BigDecimal("0");
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
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
                ", amount=" + amount +
                '}';
    }
}
