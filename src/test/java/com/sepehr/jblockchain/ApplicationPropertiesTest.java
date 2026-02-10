package com.sepehr.jblockchain;

import org.junit.jupiter.api.Test;
import org.sepehr.jitcoin.ApplicationProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationPropertiesTest {

    @Test
    void readPropertiesTest() {
        final String propertyName = "application.properties.status";
        final ApplicationProperties properties = ApplicationProperties.getInstance();
        assertEquals("alive", properties.getProperty(propertyName));
    }

}
