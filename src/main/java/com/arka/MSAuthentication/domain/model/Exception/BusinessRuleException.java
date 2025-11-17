package com.arka.MSAuthentication.domain.model.Exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 */
public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

