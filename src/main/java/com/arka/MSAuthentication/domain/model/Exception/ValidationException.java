package com.arka.MSAuthentication.domain.model.Exception;

/**
 * Excepción lanzada cuando hay errores de validación de reglas de negocio
 */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

