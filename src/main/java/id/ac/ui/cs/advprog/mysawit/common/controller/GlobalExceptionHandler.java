package id.ac.ui.cs.advprog.mysawit.common.controller;

import id.ac.ui.cs.advprog.mysawit.pengiriman.dto.ApiResponse;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanAuthorizationException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanNotFoundException;
import id.ac.ui.cs.advprog.mysawit.pengiriman.service.exception.PengirimanStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(PengirimanNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handlePengirimanNotFound(PengirimanNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(PengirimanAuthorizationException.class)
    public ResponseEntity<ApiResponse<Object>> handlePengirimanForbidden(PengirimanAuthorizationException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(PengirimanStateException.class)
    public ResponseEntity<ApiResponse<Object>> handlePengirimanStateConflict(PengirimanStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(exception.getMessage()));
    }
}
