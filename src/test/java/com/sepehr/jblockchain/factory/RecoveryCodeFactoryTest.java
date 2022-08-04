package com.sepehr.jblockchain.factory;

import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.ApplicationProperties;
import org.sepehr.jblockchain.factory.imp.RecoveryWordsFactoryImp;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecoveryCodeFactoryTest {

    private static final Logger LOGGER = Logger.getLogger(RecoveryCodeFactoryTest.class.getSimpleName());

    @Test
    void readWordsTest() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        RecoveryWordsFactoryImp recoveryCodeFactoryImp = new RecoveryWordsFactoryImp();
        String[] words = recoveryCodeFactoryImp.generateRandomRecoveryCodes();
        assertEquals(Integer.parseInt(applicationProperties.getProperty("random.words.size")), words.length);
        Arrays.stream(words).forEach(LOGGER::info);
    }

}
