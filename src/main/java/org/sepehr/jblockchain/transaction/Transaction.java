package org.sepehr.jblockchain.transaction;


import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.security.PublicKey;

@Data
public class Transaction {

    public Transaction(PublicKey sender, byte[] prevHash) {
        this.sender = sender;
        byte[] hash = Hashing.sha256().hashBytes(Bytes.concat(
                prevHash, sender.getEncoded())
        ).asBytes();
        setHash(hash);
    }

    private final PublicKey sender;

    @Setter(AccessLevel.PRIVATE)
    private byte[] hash;

    private byte[] transactionSignature;
}
