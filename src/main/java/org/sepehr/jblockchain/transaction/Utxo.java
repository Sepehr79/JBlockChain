package org.sepehr.jblockchain.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.PublicKey;

@RequiredArgsConstructor
@Getter
@Setter
public class Utxo {

    private final PublicKey receiver;
    private final long value;
    private final byte[] txid;
    private final int vout;

    private boolean spent = false;
    private boolean confirmed = false;
    private int blockHeight;

}
