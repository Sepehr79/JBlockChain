package org.sepehr.jitcoin.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class FollowerNodeRequest implements Serializable {
    private final String address;
}
