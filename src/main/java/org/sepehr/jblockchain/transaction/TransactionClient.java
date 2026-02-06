package org.sepehr.jblockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public interface TransactionClient {

    Transaction createTransaction(PublicKey senderPublic,
                                  PrivateKey senderPrivate,
                                  long amount,
                                  PublicKey receiverPublic,
                                  List<Utxo> inputs);

    boolean verifyTransaction(Transaction transaction, List<Utxo> inputs);
}
