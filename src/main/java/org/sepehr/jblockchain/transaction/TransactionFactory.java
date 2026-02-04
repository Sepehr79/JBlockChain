package org.sepehr.jblockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface TransactionFactory {

    Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate);

}
