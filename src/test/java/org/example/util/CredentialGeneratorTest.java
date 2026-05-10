package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CredentialGeneratorTest {

    private CredentialGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CredentialGenerator();
    }

    @Test
    void shouldGenerateUsernameWithoutSuffixForFirstUser() {
        String result = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );


        assertEquals("Ilyas.Azizzade", result);
    }

    @Test
    void shouldGenerateUsernameWithSuffixWhenDuplicateExists() {

        String first = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );

        String second = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );

        String third = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );

        assertEquals("Ilyas.Azizzade", first);
        assertEquals("Ilyas.Azizzade2", second);
        assertEquals("Ilyas.Azizzade3", third);
    }

    @Test
    void shouldGenerateSeparateCountersForDifferentUsers() {
        String firstUser = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );

        String secondUser = generator.generateUsername(
                "Jane",
                "Azizzade"
        );

        String thirdUser = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );
        assertEquals("Ilyas.Azizzade", firstUser);
        assertEquals("Jane.Azizzade", secondUser);
        assertEquals("Ilyas.Azizzade2", thirdUser);
    }

    @Test
    void shouldHandleEmptyFirstName() {

        String result = generator.generateUsername(
                "",
                "Azizzade"
        );
        assertEquals(".Azizzade", result);
    }

    @Test
    void shouldHandleEmptyLastName() {

        String result = generator.generateUsername(
                "Ilyas",
                ""
        );
        assertEquals("Ilyas.", result);
    }

    @Test
    void shouldRemoveUsernameCounter() {

        generator.generateUsername("Ilyas", "Azizzade");
        generator.generateUsername("Ilyas", "Azizzade");

        generator.removeUsername("Ilyas.Azizzade");

        String result = generator.generateUsername(
                "Ilyas",
                "Azizzade"
        );
        assertEquals("Ilyas.Azizzade", result);
    }


    @Test
    void shouldGeneratePasswordWithLengthTen() throws Exception {
        CredentialGenerator generator = new CredentialGenerator();

        Field charsField = CredentialGenerator.class
                .getDeclaredField("chars");

        charsField.setAccessible(true);

        charsField.set(
                generator,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789"
        );

        String password = generator.generatePassword();

        assertNotNull(password);

        assertEquals(10, password.length());
    }

}