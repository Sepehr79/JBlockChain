package org.sepehr.jblockchain.transaction;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;


@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction implements Serializable {

    private final PublicKey sender;

    List<Utxo> inputs;

    @Setter
    private Utxo out0;

    @Setter
    private Utxo out1;

    @Setter
    private long amount;

    @Setter
    private byte[] transactionSignature;

    @Setter
    @EqualsAndHashCode.Include
    private byte[] hash;

    public Transaction(PublicKey senderPublic, List<Utxo> inputs, Utxo out0, Utxo out1) {
        this.sender = senderPublic;
        this.inputs = inputs;
        this.out0 = out0;
        this.out1 = out1;
    }

}
