package org.sepehr.jitcoin.timestampserver;

import org.sepehr.jitcoin.transaction.Transaction;
import org.sepehr.jitcoin.transaction.Utxo;
import org.sepehr.jitcoin.verification.MerkleTree;

import java.security.PublicKey;
import java.util.List;

public interface TimestampServer {

    boolean acceptBlock(Block block);

    byte[] getHash();

    boolean mineCurrentBlock(long timeout);

    boolean appendTransaction(Transaction transaction);

    List<Utxo> getTransactionInputs(PublicKey senderPublic);

    int getCurrentBlockIdx();

    MerkleTree.TransactionProof getProof(Transaction transaction);
}
