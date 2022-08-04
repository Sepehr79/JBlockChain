package org.sepehr.jblockchain.sample;

import org.apache.commons.codec.binary.Hex;
import org.sepehr.jblockchain.block.BlockBody;

import java.security.PublicKey;

public class Transaction implements BlockBody {

    private final PublicKey sender;
    private final PublicKey receiver;
    private final String amount;
    private byte[] transactionSignature;

    public Transaction(PublicKey sender, PublicKey receiver, String amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public void setTransactionSignature(byte[] transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public String getSenderAddress() {
        return Hex.encodeHexString(sender.getEncoded());
    }

    public String getReceiverAddress() {
        return Hex.encodeHexString(receiver.getEncoded());
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "sender='" + getSenderAddress() + '\'' +
                ", receiver='" + getReceiverAddress() + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    public byte[] getTransactionSignature() {
        return transactionSignature;
    }
}
