package org.sepehr.jblockchain.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.PublicKey;

@RequiredArgsConstructor
@Getter
public class Utxo {

    private final PublicKey receiver;
    private final long value;
    private final byte[] txid;
    private final int vout;

    @Setter
    private boolean spent = false;
    @Setter
    private boolean confirmed = false;
    @Setter
    private int blockHeight;

}
