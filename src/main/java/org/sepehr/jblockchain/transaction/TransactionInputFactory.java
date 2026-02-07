package org.sepehr.jblockchain.transaction;

import java.security.PublicKey;
import java.util.List;

public class TransactionInputFactory {

    public List<Utxo> createInput(PublicKey publicKey, int amount) {
        return List.of(new Utxo(publicKey, amount, "".getBytes(), 0));
    }
}
