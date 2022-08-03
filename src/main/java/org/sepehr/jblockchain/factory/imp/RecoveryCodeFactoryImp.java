package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.ApplicationProperties;
import org.sepehr.jblockchain.factory.RecoveryCodeFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class RecoveryCodeFactoryImp implements RecoveryCodeFactory {

    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();
    private static final ApplicationProperties PROPERTIES = ApplicationProperties.getInstance();
    private static String[] words;

    static {
        try {
            words = new String(Objects.requireNonNull(RecoveryCodeFactoryImp.class.getResourceAsStream("/Words.csv")).readAllBytes()).split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] generateRandomRecoveryCodes() {
        final List<String> wordsList = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(PROPERTIES.getProperty("words.size")); i++) {
            wordsList.add(RecoveryCodeFactoryImp.words[RANDOM_GENERATOR.nextInt(RecoveryCodeFactoryImp.words.length)]);
        }
        return wordsList.toArray(new String[0]);
    }
}
