package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialGeneratorTest {

    private CredentialGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CredentialGenerator();
    }

    @Test
    void generateUsername_firstUse_returnsBaseName() {
        assertEquals("john.smith", generator.generateUsername("John", "Smith"));
    }

    @Test
    void generateUsername_duplicateAppendsCounter() {
        generator.generateUsername("John", "Smith");
        assertEquals("john.smith2", generator.generateUsername("John", "Smith"));
    }

    @Test
    void generatePassword_returnsConfiguredLength() {
        assertEquals(10, generator.generatePassword().length());
    }
}
