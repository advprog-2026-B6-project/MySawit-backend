package id.ac.ui.cs.advprog.mysawit.kebun.controller;

import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunConflictException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunNotFoundException;
import id.ac.ui.cs.advprog.mysawit.kebun.exception.KebunValidationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = {
        KebunSawitController.class,
        KebunAssignmentController.class
})
public class KebunExceptionHandler {

    @ExceptionHandler(KebunNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(KebunNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(exception.getMessage()));
    }

    @ExceptionHandler(KebunConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(KebunConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error(exception.getMessage()));
    }

    @ExceptionHandler(KebunValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(KebunValidationException exception) {
        return ResponseEntity.badRequest().body(error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Request tidak valid");
        return ResponseEntity.badRequest().body(error(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(error("Request tidak valid"));
    }

    private Map<String, String> error(String message) {
        return Map.of("error", message);
    }
}
