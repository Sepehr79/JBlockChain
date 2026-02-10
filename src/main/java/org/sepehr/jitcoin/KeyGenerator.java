package org.sepehr.jitcoin;

import org.sepehr.jitcoin.account.DataEncoder;
import org.sepehr.jitcoin.account.KeyFactory;
import org.sepehr.jitcoin.account.SimpleKeyFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;

public class KeyGenerator {
    public static void main(String[] args) throws IOException {
        KeyFactory keyFactory = new SimpleKeyFactory();
        KeyPair keyPair = keyFactory.generateKeyPair();
        Files.writeString(Path.of("private.key"),
                DataEncoder.getInstance().encodeData(keyPair.getPrivate().getEncoded()));
        Files.writeString(Path.of("public.key"),
                DataEncoder.getInstance().encodeData(keyPair.getPublic().getEncoded()));
    }
}
