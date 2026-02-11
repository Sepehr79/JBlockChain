package org.sepehr.jitcoin.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.security.PublicKey;

@RequiredArgsConstructor
@Getter
public class TransactionInputRequest implements Serializable {
    private final PublicKey account;
}
