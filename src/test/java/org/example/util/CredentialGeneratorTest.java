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
    void generateUsername_thirdDuplicateAppendsCounter() {
        generator.generateUsername("Jane", "Doe");
        generator.generateUsername("Jane", "Doe");
        assertEquals("jane.doe3", generator.generateUsername("Jane", "Doe"));
    }

    @Test
    void generatePassword_returnsConfiguredLength() {
        assertEquals(10, generator.generatePassword().length());
    }

    @Test
    void removeUsername_clearsCounterTracking() {
        generator.generateUsername("John", "Smith");
        generator.removeUsername("john.smith");
        assertEquals("john.smith", generator.generateUsername("John", "Smith"));
    }

    @Test
    void setCharsFromProperties_usesConfiguredAlphabet() {
        generator.setCharsFromProperties("ABC");
        String password = generator.generatePassword();
        assertTrue(password.chars().allMatch(c -> c == 'A' || c == 'B' || c == 'C'));
    }

    @Test
    void setCharsFromProperties_blank_keepsDefault() {
        generator.setCharsFromProperties("   ");
        assertEquals(10, generator.generatePassword().length());
    }

    @Test
    void setCharsFromProperties_null_keepsDefault() {
        generator.setCharsFromProperties(null);
        assertEquals(10, generator.generatePassword().length());
    }
}
