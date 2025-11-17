package com.arka.MSAuthentication.infrastructure.config;

import com.arka.MSAuthentication.domain.model.gateway.UserGateway;
import com.arka.MSAuthentication.domain.usecase.CreateUserUseCase;
import com.arka.MSAuthentication.application.service.BusinessValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {
    @Bean
    public BusinessValidationService businessValidationService(UserGateway userGateway) {
        return new BusinessValidationService(userGateway);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder,
                                               BusinessValidationService businessValidationService) {
        return new CreateUserUseCase(userGateway , passwordEncoder, businessValidationService);
    }
}
