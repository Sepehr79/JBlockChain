package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.security.PublicKey;
import java.util.List;

public interface TimestampServer {

    boolean acceptBlock(Block block);

    byte[] getHash();

    boolean mineCurrentBlock(long timeout);

    boolean appendTransaction(Transaction transaction);

    List<Utxo> getTransactionInputs(PublicKey senderPublic);

    int getCurrentBlockIdx();
}
