package org.sepehr.jitcoin.transaction;

import java.security.PublicKey;
import java.util.List;

public class TransactionInputFactory {

    public List<Utxo> createInput(PublicKey publicKey, int amount) {
        byte[] hash = new byte[32];
        Utxo utxo = new Utxo(publicKey, amount, 0);
        utxo.setTxid(hash);
        return List.of(utxo);
    }
}
