package org.sepehr.jblockchain.transaction;


import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;


@Getter
public class Transaction {

    private final PublicKey sender;

    private final PublicKey receiver;

    private final long amount;

    @Setter
    private Utxo out0;

    @Setter
    private Utxo out1;

    @Setter
    private byte[] transactionSignature;

    @Setter
    private byte[] hash;

    public Transaction(PublicKey senderPublic, long amount, PublicKey receiverPublic) {
        this.sender = senderPublic;
        this.amount = amount;
        this.receiver = receiverPublic;
    }

}
