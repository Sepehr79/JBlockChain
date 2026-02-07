package org.sepehr.jblockchain.verification;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.timestampserver.Block;
import org.sepehr.jblockchain.transaction.Transaction;
import org.sepehr.jblockchain.transaction.Utxo;

import java.util.List;

public class HashManager {

    public static byte[] hash(String value) {
        return Hashing.sha256().hashBytes(value.getBytes()).asBytes();
    }

    public static byte[] hashTransaction(Transaction transaction, List<Utxo> inputs) {
        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putLong(transaction.getAmount());
        hasher.putBytes(transaction.getSender().getEncoded());
        hasher.putBytes(transaction.getReceiver().getEncoded());
        inputs.forEach(utxo -> hasher.putBytes(utxo.getTxid()));
        return hasher.hash().asBytes();
    }

    public static byte[] hashBlock(Block block) {
        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putBytes(block.getRootHash().getBytes());
        hasher.putLong(block.getNonce());
        hasher.putInt(block.getIdx());
        hasher.putBytes(block.getPrevHash());
        return hasher.hash().asBytes();
    }

}
