package org.sepehr.jitcoin.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sepehr.jitcoin.transaction.Utxo;

import java.io.Serializable;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TransactionInputReply implements Serializable {
    final List<Utxo> inputs;
}
