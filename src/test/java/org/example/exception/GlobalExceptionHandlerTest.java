package org.example.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_returnsFieldErrors() {
        var target = new Object();
        var bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "firstName", "must not be blank"));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("firstName"));
    }

    @Test
    void handleLockedException_returnsUnauthorized() {
        var response = handler.handleLockedException(
                new org.springframework.security.authentication.LockedException("Account is locked. Try again in 5 minutes."));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Account is locked. Try again in 5 minutes.", response.getBody().get("message"));
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorized() {
        var response = handler.handleUnauthorizedException(new UnauthorizedException("Access denied"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Access denied", response.getBody().get("message"));
    }

    @Test
    void handleUnauthorizedException_nullMessage_usesDefault() {
        var response = handler.handleUnauthorizedException(new UnauthorizedException(null));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody().get("message"));
    }

    @Test
    void handleBadRequest_returnsBadRequest() {
        var response = handler.handleBadRequest(new IllegalStateException("user already registered as trainer"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("user already registered as trainer", response.getBody().get("message"));
    }

    @Test
    void handleAllExceptions_nullMessage_usesDefault() {
        var response = handler.handleAllExceptions(new RuntimeException());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().get("message"));
    }

    @Test
    void handleAuthenticationException_returnsUnauthorized() {
        var response = handler.handleAuthenticationException(new AuthenticationException("Invalid credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void handleResourceNotFoundException_returnsNotFound() {
        var response = handler.handleResourceNotFoundException(new ResourceNotFoundException("Not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().get("message"));
    }

    @Test
    void handleAllExceptions_returnsInternalServerError() {
        var response = handler.handleAllExceptions(new RuntimeException("Something failed"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something failed", response.getBody().get("message"));
    }
}
