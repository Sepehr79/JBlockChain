package com.sepehr.jblockchain.factory;

import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.RecoveryCodeFactoryImp;

import java.util.Arrays;

class RecoveryCodeFactoryTest {

    @Test
    void readWordsTest() {
        RecoveryCodeFactoryImp recoveryCodeFactoryImp = new RecoveryCodeFactoryImp();
        String[] words = recoveryCodeFactoryImp.generateRandomRecoveryCodes();

    }

}
