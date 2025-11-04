package com.arka.MSAuthentication.infrastructure.config;

import com.arka.MSAuthentication.domain.model.gateway.UserGateway;
import com.arka.MSAuthentication.domain.usecase.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(userGateway , passwordEncoder);
    }
}
