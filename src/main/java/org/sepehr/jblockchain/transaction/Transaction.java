package org.sepehr.jblockchain.transaction;


import lombok.Data;

import java.security.PublicKey;

@Data
public class Transaction {

    public Transaction(PublicKey sender) {
        this.sender = sender;
    }

    private final PublicKey sender;
    private byte[] hash;

    private byte[] transactionSignature;

    @Override
    public String toString() {
        return "Transaction{" +
                "sender=" + sender +
                ", hash='" + hash + '\'' +
                '}';
    }
}
