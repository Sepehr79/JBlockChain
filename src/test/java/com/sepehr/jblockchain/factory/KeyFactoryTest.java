package com.sepehr.jblockchain.factory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyFactoryTest {

    @Test
    void sameKayPairGenerationTest() throws DecoderException {
        final String[] seeds = {"Hello"};
        final long timestamp = 1001;

        KeyFactoryImp keyFactoryImp = new KeyFactoryImp();
        KeyPair keyPair1 = keyFactoryImp.generateKeyPair(timestamp, seeds);
        KeyPair keyPair2 = keyFactoryImp.generateKeyPair(timestamp, seeds);

        assertArrayEquals(keyPair1.getPrivate().getEncoded(), keyPair2.getPrivate().getEncoded());
        String hexCode = Hex.encodeHexString(keyPair1.getPrivate().getEncoded());
        assertEquals(
                "3082014b0201003082012c06072a8648ce3804013082011f02818100fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c70215009760508f15230bccb292b982a2eb840bf0581cf502818100f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a041602142d3b90e617b1a359d502f7a2b3c8930af469f123",
                hexCode
        );
        byte[] bytes = Hex.decodeHex(hexCode);
        assertArrayEquals(bytes, keyPair2.getPrivate().getEncoded());
    }

}
