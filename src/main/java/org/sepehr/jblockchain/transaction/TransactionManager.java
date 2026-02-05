package org.sepehr.jblockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface TransactionManager {

    Transaction createTransaction(PublicKey senderPublic, PrivateKey senderPrivate, byte[] prevHash);

    boolean verifyTransaction(Transaction transaction);
}
