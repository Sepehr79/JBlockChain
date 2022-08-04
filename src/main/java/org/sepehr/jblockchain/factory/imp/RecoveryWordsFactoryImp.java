package org.sepehr.jblockchain.factory.imp;

import org.sepehr.jblockchain.ApplicationProperties;
import org.sepehr.jblockchain.factory.RecoveryCodeFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.stream.IntStream;

public class RecoveryWordsFactoryImp implements RecoveryCodeFactory {

    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();
    private static final ApplicationProperties PROPERTIES = ApplicationProperties.getInstance();
    private static String[] words;

    static {
        try {
            words = new String(Objects.requireNonNull(RecoveryWordsFactoryImp.class.getResourceAsStream("/words.csv")).readAllBytes()).split("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] generateRandomRecoveryCodes() {
        return IntStream.range(0, Integer.parseInt(PROPERTIES.getProperty("random.words.size")))
                .mapToObj(value -> words[RANDOM_GENERATOR.nextInt(words.length)])
                .toArray(String[]::new);
    }
}
