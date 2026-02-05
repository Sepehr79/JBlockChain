package org.sepehr.jblockchain.transaction;


import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;
import java.util.List;


@AllArgsConstructor
@Getter
public class Transaction {

    private final PublicKey sender;

    private PublicKey receiver;

    @Setter
    private Utxo out0;

    @Setter
    private Utxo out1;

    @Setter
    private byte[] transactionSignature;

    @Setter
    private byte[] hash;

    @Setter
    private List<Utxo> inputs;

    private long amount;

    public Transaction(PublicKey senderPublic, long amount, PublicKey receiverPublic) {
        this.sender = senderPublic;
        this.amount = amount;
        this.receiver = receiverPublic;
    }

}
