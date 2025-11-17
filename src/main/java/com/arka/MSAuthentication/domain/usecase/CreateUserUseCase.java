package com.arka.MSAuthentication.domain.usecase;

import com.arka.MSAuthentication.application.dto.ConsultUserDto;
import com.arka.MSAuthentication.application.dto.UpdateInfoUserDto;
import com.arka.MSAuthentication.domain.model.User;
import com.arka.MSAuthentication.domain.model.gateway.UserGateway;
import com.arka.MSAuthentication.application.service.BusinessValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateUserUseCase {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final BusinessValidationService businessValidationService;

    public CreateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder,
                             BusinessValidationService businessValidationService) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.businessValidationService = businessValidationService;
    }

    public User create(User user, String finalRoleName) {
        // Normalizar email
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        // Validaciones de negocio (no de campos)
        businessValidationService.ensureEmailAvailableForCreate(user.getEmail());

        // Hash de la contraseña
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        // Construcción del usuario
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setDirection(user.getDirection());
        newUser.setPhone(user.getPhone());
        newUser.setRole(finalRoleName);
        newUser.setIsDeleted(false);

        // Persistencia
        return userGateway.SaveUser(newUser);
    }

    public Long idUSer(String email){
        return userGateway.idUser(email);
    }

    public ConsultUserDto getConsultUser(Long id){
        User user = userGateway.BuscarUser(id);
        ConsultUserDto dto = new ConsultUserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDireccion(user.getDirection());
        dto.setTelefono(user.getPhone());
        return dto;
    }

    public User updateInfoUser(Long id, UpdateInfoUserDto updateInfoUserDto){
        User user = userGateway.BuscarUser(id);

        if(updateInfoUserDto.getNombre() != null && !updateInfoUserDto.getNombre().isEmpty()){
            user.setName(updateInfoUserDto.getNombre());
        }
        if(updateInfoUserDto.getDireccion() != null && !updateInfoUserDto.getDireccion().isEmpty()){
            user.setDirection(updateInfoUserDto.getDireccion());
        }
        if(updateInfoUserDto.getTelefono() != null && !updateInfoUserDto.getTelefono().isEmpty()){
            user.setPhone(updateInfoUserDto.getTelefono());
        }
        if(updateInfoUserDto.getPassword() != null && !updateInfoUserDto.getPassword().isEmpty()){
            String hashedPassword = passwordEncoder.encode(updateInfoUserDto.getPassword());
            user.setPassword(hashedPassword);
        }
        if(updateInfoUserDto.getEmail() != null && !updateInfoUserDto.getEmail().isEmpty()){
            String normalizedNewEmail = updateInfoUserDto.getEmail().trim().toLowerCase();
            businessValidationService.ensureEmailAvailableForUpdate(user.getEmail(), normalizedNewEmail);
            user.setEmail(normalizedNewEmail);
        }

        return userGateway.SaveUser(user);
    }
}
