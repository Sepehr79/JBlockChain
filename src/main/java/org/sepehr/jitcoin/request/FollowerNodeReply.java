package org.sepehr.jitcoin.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sepehr.jitcoin.timestampserver.Block;
import org.sepehr.jitcoin.transaction.Utxo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class FollowerNodeReply implements Serializable {
    private final List<Block> blocks;
    private final Set<Utxo> openUtxos;
    private final int miningDifficulty;
}
