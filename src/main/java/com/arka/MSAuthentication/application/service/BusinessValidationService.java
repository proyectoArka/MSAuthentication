package com.arka.MSAuthentication.application.service;

import com.arka.MSAuthentication.domain.model.Exception.BusinessRuleException;
import com.arka.MSAuthentication.domain.model.gateway.UserGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BusinessValidationService {

    private final UserGateway userGateway;

    /** Verifica que el email no esté registrado para creación */
    public void ensureEmailAvailableForCreate(String email) {
        if (email != null && userGateway.existsByEmail(email)) {
            throw new BusinessRuleException("El email ya está registrado.");
        }
    }

    /** Verifica que el nuevo email esté disponible para actualización */
    public void ensureEmailAvailableForUpdate(String currentEmail, String newEmail) {
        if (newEmail != null && !newEmail.equals(currentEmail) && userGateway.existsByEmail(newEmail)) {
            throw new BusinessRuleException("El email ya está registrado.");
        }
    }
}

