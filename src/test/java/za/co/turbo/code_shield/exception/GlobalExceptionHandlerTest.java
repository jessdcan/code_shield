package za.co.turbo.code_shield.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("unit")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void handleEntityNotFoundException_ReturnsNotFoundResponse() {
        // Arrange
        EntityNotFoundException ex = new EntityNotFoundException("Task", 999L);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleEntityNotFoundException(ex, webRequest);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("Task with id 999 not found", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleValidationExceptions_ReturnsBadRequestResponse() {
        // Arrange
        MethodArgumentNotValidException ex = createValidationException();
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertNotNull(errors);
        assertEquals("Title is required", errors.get("title"));
        assertEquals("Due date is required", errors.get("dueDate"));
    }

    @Test
    void handleAllUncaughtException_ReturnsInternalServerErrorResponse() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleAllUncaughtException(ex, webRequest);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(500, body.get("status"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleEntityNotFoundException_WithDifferentEntity_ReturnsCorrectMessage() {
        // Arrange
        EntityNotFoundException ex = new EntityNotFoundException("User", 123L);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleEntityNotFoundException(ex, webRequest);
        
        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("User with id 123 not found", body.get("message"));
    }

    @Test
    void handleValidationExceptions_WithMultipleErrors_ReturnsAllErrors() {
        // Arrange
        MethodArgumentNotValidException ex = createValidationExceptionWithMultipleErrors();
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(ex);
        
        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        
        assertEquals(3, errors.size());
        assertEquals("Title is required", errors.get("title"));
        assertEquals("Due date is required", errors.get("dueDate"));
        assertEquals("Status is required", errors.get("status"));
    }

    private MethodArgumentNotValidException createValidationException() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "task");
        bindingResult.addError(new FieldError("task", "title", "Title is required"));
        bindingResult.addError(new FieldError("task", "dueDate", "Due date is required"));
        
        return new MethodArgumentNotValidException(null, bindingResult);
    }

    private MethodArgumentNotValidException createValidationExceptionWithMultipleErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "task");
        bindingResult.addError(new FieldError("task", "title", "Title is required"));
        bindingResult.addError(new FieldError("task", "dueDate", "Due date is required"));
        bindingResult.addError(new FieldError("task", "status", "Status is required"));
        
        return new MethodArgumentNotValidException(null, bindingResult);
    }
} 