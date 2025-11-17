package com.arka.MSAuthentication.domain.model.Exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe
 */
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s con ID %d no encontrado", resource, id));
    }
}

