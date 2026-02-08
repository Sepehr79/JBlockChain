package org.sepehr.jblockchain.transaction;

import jdk.jfr.Enabled;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.PublicKey;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Utxo {

    private final PublicKey receiver;
    private final long value;

    @EqualsAndHashCode.Include
    private byte[] txid;

    @EqualsAndHashCode.Include
    private final int vout;

    private boolean confirmed = false;
    private int blockHeight;

}
