package org.sepehr.jblockchain.transaction;


import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import lombok.Data;

import java.security.PublicKey;

@Data
public class Transaction {

    public Transaction(PublicKey sender, byte[] prevHash) {
        this.sender = sender;
        this.prevHash = prevHash;
    }

    private final PublicKey sender;
    private byte[] prevHash;

    private byte[] transactionSignature;

    public byte[] getHash() {
        return Hashing.sha256().hashBytes(Bytes.concat(
                prevHash,
                sender.getEncoded())
        ).asBytes();
    }
}
