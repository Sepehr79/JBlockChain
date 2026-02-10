package org.sepehr.jblockchain;

import org.sepehr.jblockchain.account.DataEncoder;
import org.sepehr.jblockchain.account.KeyFactory;
import org.sepehr.jblockchain.account.SimpleKeyFactory;

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
