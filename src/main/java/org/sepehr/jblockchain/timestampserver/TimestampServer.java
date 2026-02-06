package org.sepehr.jblockchain.timestampserver;

import org.sepehr.jblockchain.transaction.Transaction;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface TimestampServer {
    Transaction createTransaction(PublicKey senderPublic,
                              PrivateKey senderPrivate,
                              long amount,
                              PublicKey receiverPublic);

    boolean acceptBlock(Block block);

    byte[] getHash();

    Block mineCurrentBlock(long timeout);

    boolean appendTransaction(Transaction transaction);
}
