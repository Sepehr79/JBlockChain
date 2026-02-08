package org.sepehr.jblockchain.verification;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.util.List;

public class HashManager {

    private static HashManager HASH_MANAGER;
    private HashManager() { }

    public byte[] hash(String value) {
        return Hashing.sha256().hashBytes(value.getBytes()).asBytes();
    }

    public byte[] hash(byte[] value) {
        return Hashing.sha256().hashBytes(value).asBytes();
    }

    public byte[] hashTransaction(Transaction transaction) {
        Hasher hasher = Hashing.sha256().newHasher();

        hasher.putBytes(transaction.getSender().getEncoded());

        for (Utxo input : transaction.getInputs()) {
            hasher.putBytes(input.getTxid());
            hasher.putInt(input.getVout());
        }

        if (transaction.getOut0() != null) {
            hasher.putLong(transaction.getOut0().getValue());
            hasher.putBytes(transaction.getOut0().getReceiver().getEncoded());
        }

        if (transaction.getOut1() != null) {
            hasher.putLong(transaction.getOut1().getValue());
            hasher.putBytes(transaction.getOut1().getReceiver().getEncoded());
        }

        return hasher.hash().asBytes();
    }

    public byte[] hashBlock(Block block) {
        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putBytes(block.getTransactionRootHash());
        hasher.putLong(block.getNonce());
        hasher.putInt(block.getIdx());
        hasher.putBytes(block.getPrevHash());
        hasher.putLong(block.getTimestamp());
        return hasher.hash().asBytes();
    }

    public static HashManager getInstance() {
        if (HASH_MANAGER == null)
            HASH_MANAGER = new HashManager();
        return HASH_MANAGER;
    }

}
