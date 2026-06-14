package org.example.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionLoggingInterceptorTest {

    @Test
    void sanitizeQueryString_redactsSensitiveParams() {
        String sanitized = TransactionLoggingInterceptor.sanitizeQueryString(
                "username=john&password=secret&oldPassword=old1&newPassword=new1&token=abc");

        assertEquals("username=john&password=***&oldPassword=***&newPassword=***&token=***", sanitized);
    }

    @Test
    void sanitizeQueryString_nullOrBlank_returnsUnchanged() {
        assertNull(TransactionLoggingInterceptor.sanitizeQueryString(null));
        assertEquals("", TransactionLoggingInterceptor.sanitizeQueryString(""));
    }
}
