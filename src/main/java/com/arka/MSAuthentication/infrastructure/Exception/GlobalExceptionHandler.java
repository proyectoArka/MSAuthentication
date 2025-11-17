package com.arka.MSAuthentication.infrastructure.Exception;

import com.arka.MSAuthentication.domain.model.Exception.BusinessRuleException;
import com.arka.MSAuthentication.domain.model.Exception.DomainException;
import com.arka.MSAuthentication.domain.model.Exception.ResourceNotFoundException;
import com.arka.MSAuthentication.domain.model.Exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== EXCEPCIONES DE DOMINIO ====================

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<SimpleErrorDto> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        log.warn("Validation error: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<SimpleErrorDto> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<SimpleErrorDto> handleBusinessRuleException(
            BusinessRuleException ex, HttpServletRequest request) {
        log.warn("Business rule violation: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<SimpleErrorDto> handleDomainException(
            DomainException ex, HttpServletRequest request) {
        log.warn("Domain exception: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ==================== EXCEPCIONES DE SPRING VALIDATION ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SimpleErrorDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String joined = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        String message = joined.isBlank()
                ? "Uno o más campos tienen errores de validación"
                : "Errores de validación: " + joined;
        log.warn("Validation failed at {} -> {}", request.getRequestURI(), message);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    // ==================== EXCEPCIONES DE SEGURIDAD ====================

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<SimpleErrorDto> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        log.warn("Authentication failed: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<SimpleErrorDto> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso");
    }

    // ==================== EXCEPCIONES DE TOKENS ====================

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<SimpleErrorDto> handleTokenRefreshException(
            TokenRefreshException ex, HttpServletRequest request) {
        log.warn("Token refresh error: {} at {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // ==================== EXCEPCIONES DE HTTP ====================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SimpleErrorDto> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request at {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "El formato del JSON es inválido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<SimpleErrorDto> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
        log.warn("Type mismatch: {} at {}", message, request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<SimpleErrorDto> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("Endpoint not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return build(HttpStatus.NOT_FOUND, "El endpoint solicitado no existe");
    }

    // ==================== EXCEPCIÓN GENÉRICA (FALLBACK) ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleErrorDto> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado. Por favor, contacte al administrador.");
    }

    private ResponseEntity<SimpleErrorDto> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(SimpleErrorDto.builder()
                        .status(status.value())
                        .message(message)
                        .build());
    }
}
