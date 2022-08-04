package org.sepehr.jblockchain.factory;

import java.security.KeyPair;

public interface KeyFactory {

    KeyPair generateKeyPair(long timestamp, String[] seeds);

}
