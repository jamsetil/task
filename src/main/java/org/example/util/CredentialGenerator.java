package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CredentialGenerator {

    private final Map<String, AtomicInteger> usernameCounter = new HashMap<>();

    @Value("${username-generator.char}")
    private String chars;

    public String generateUsername(String firstName, String lastName) {

        String base = firstName + "." + lastName;

        int count = usernameCounter
                .getOrDefault(base, new AtomicInteger(0))
                .incrementAndGet();

        usernameCounter.putIfAbsent(base, new AtomicInteger(count));

        String result = (count == 1) ? base : base + count;

        log.debug("Generated username: base={}, count={}, result={}", base, count, result);

        return result;
    }

    public void removeUsername(String username) {

        usernameCounter.remove(username);

        log.info("Removed username tracking for: {}", username);
    }

    public String generatePassword() {

        SecureRandom random = new SecureRandom();

        String password = random.ints(10, 0, chars.length())
                .mapToObj(i -> String.valueOf(chars.charAt(i)))
                .collect(Collectors.joining());

        log.debug("Generated password of length={}", password.length());

        return password;
    }
}