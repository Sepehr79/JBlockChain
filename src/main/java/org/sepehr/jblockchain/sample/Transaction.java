package org.sepehr.jblockchain.sample;

import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.block.BlockBody;

import java.nio.charset.StandardCharsets;

public class Transaction implements BlockBody {

    private final String sender;
    private final String receiver;
    private final String amount;

    public Transaction(String sender, String receiver, String amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String hash() {
        return Hashing.sha256()
                .hashString(
                        sender +
                                receiver +
                                amount,
                        StandardCharsets.UTF_8
                )
                .toString();
    }
}
