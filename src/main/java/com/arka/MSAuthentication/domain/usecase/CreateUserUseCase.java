package com.arka.MSAuthentication.domain.usecase;

import com.arka.MSAuthentication.application.dto.ConsultUserDto;
import com.arka.MSAuthentication.application.dto.UpdateInfoUserDto;
import com.arka.MSAuthentication.domain.model.Exception.Exception;
import com.arka.MSAuthentication.domain.model.User;
import com.arka.MSAuthentication.domain.model.gateway.UserGateway;

import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateUserUseCase {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User user, String finalRoleName) {

        if(user.getName() == null || user.getName().isEmpty()){
            throw new Exception("El nombre no puede estar vacío.");
        }

        if(user.getEmail() == null || user.getEmail().isEmpty()){
            throw new Exception("El email no puede estar vacío.");
        }
        if(user.getDirection() == null || user.getDirection().isEmpty()){
            throw new Exception("La dirección no puede estar vacía.");
        }

        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new Exception("El teléfono no puede estar vacío.");
        }

        // 1. Validar email existente
        if (userGateway.existsByEmail(user.getEmail())) {
            throw new Exception("El email ya está registrado.");
        }

        if(user.getPassword().length() < 6){
            throw new Exception("La contraseña debe tener al menos 6 caracteres.");
        }

        // 2. Hashing de la contraseña
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        // 3. Crear el usuario
        User newUser = new User();
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(hashedPassword);
            newUser.setDirection(user.getDirection());
            newUser.setPhone(user.getPhone());
            newUser.setRole(finalRoleName); //Asigna el rol FINAL determinado por el Controller
            newUser.setIsDeleted(false);

        // 4. Persistencia (a través del puerto)
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
        User userUpdate = user;

        if(updateInfoUserDto.getNombre() != null && !updateInfoUserDto.getNombre().isEmpty()){
            userUpdate.setName(updateInfoUserDto.getNombre());
        }
        if(updateInfoUserDto.getDireccion() != null && !updateInfoUserDto.getDireccion().isEmpty()){
            userUpdate.setDirection(updateInfoUserDto.getDireccion());
        }
        if(updateInfoUserDto.getTelefono() != null && !updateInfoUserDto.getTelefono().isEmpty()){
            userUpdate.setPhone(updateInfoUserDto.getTelefono());
        }
        if(updateInfoUserDto.getPassword() != null && !updateInfoUserDto.getPassword().isEmpty()){
            if(updateInfoUserDto.getPassword().length() < 6){
                throw new Exception("La contraseña debe tener al menos 6 caracteres.");
            }
            String hashedPassword = passwordEncoder.encode(updateInfoUserDto.getPassword());
            userUpdate.setPassword(hashedPassword);
        }
        if(updateInfoUserDto.getEmail() != null && !updateInfoUserDto.getEmail().isEmpty()){
            if(!updateInfoUserDto.getEmail().equals(user.getEmail()) && userGateway.existsByEmail(updateInfoUserDto.getEmail())){
                throw new Exception("El email ya está registrado.");
            }
            userUpdate.setEmail(updateInfoUserDto.getEmail());
        }

        User updatedUser = userGateway.SaveUser(userUpdate);

        return updatedUser;
    }
}
