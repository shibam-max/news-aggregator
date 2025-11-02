package com.newsaggregator.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler globalExceptionHandler;
    
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }
    
    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "keyword", "Keyword is required"));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationExceptions(exception);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertNotNull(body.get("timestamp"));
        
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertEquals("Keyword is required", errors.get("keyword"));
    }
    
    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Given
        Exception exception = new RuntimeException("Test exception");
        
        // When
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(exception);
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }
}
