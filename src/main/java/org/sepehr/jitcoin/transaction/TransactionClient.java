package org.sepehr.jitcoin.transaction;

import org.sepehr.jitcoin.timestampserver.TimestampServer;
import org.sepehr.jitcoin.verification.MerkleTree;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface TransactionClient {

    Transaction createTransaction(PublicKey senderPublic,
                                  PrivateKey senderPrivate,
                                  long amount,
                                  PublicKey receiverPublic,
                                  List<Utxo> inputs);

    boolean verifyTransaction(Transaction transaction, MerkleTree.TransactionProof transactionProof);

    List<Utxo> getAccountInputs(TimestampServer server, PublicKey owner);
}
