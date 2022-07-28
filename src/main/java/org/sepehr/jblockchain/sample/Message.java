package org.sepehr.jblockchain.sample;

import com.google.common.hash.Hashing;
import org.sepehr.jblockchain.block.BlockBody;

import java.nio.charset.StandardCharsets;

public class Message implements BlockBody {

    private final String sender;
    private final String receiver;
    private final String text;

    public Message(String sender, String receiver, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
