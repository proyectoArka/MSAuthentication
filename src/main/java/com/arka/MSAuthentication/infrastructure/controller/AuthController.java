package com.arka.MSAuthentication.infrastructure.controller;

import com.arka.MSAuthentication.application.dto.LoginDto;
import com.arka.MSAuthentication.application.dto.UserCreateDto;
import com.arka.MSAuthentication.application.mapper.UserMapper;
import com.arka.MSAuthentication.domain.model.User;
import com.arka.MSAuthentication.domain.usecase.CreateUserUseCase;
import com.arka.MSAuthentication.infrastructure.adapters.entity.RefreshTokenEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import com.arka.MSAuthentication.infrastructure.config.JwtUtil;
import com.arka.MSAuthentication.infrastructure.config.RefreshToken.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CreateUserUseCase createUserUseCase;
    private final UserMapper userMapper ;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CreateUserUseCase createUserUseCase,
                          RefreshTokenService refreshTokenService,
                          UserDetailsService userDetailsService) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.createUserUseCase = createUserUseCase;
        this.userMapper = new UserMapper();
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/createadmin")
    @PreAuthorize("hasAuthority('usuarios:crear_admin')") // Solo si tiene este permiso
    public ResponseEntity<UserCreateDto> createadmin(@RequestBody UserCreateDto userCreateDto){
        User user = userMapper.toUser(userCreateDto);            // DTO → Dominio
        User newAdmin = createUserUseCase.create(user, "ADMIN"); // Lógica de negocio
        UserCreateDto response = userMapper.toDto(newAdmin);     // Dominio → DTO
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createclient")
    public ResponseEntity<UserCreateDto> createclient(@RequestBody UserCreateDto userCreateDto){
        User user = userMapper.toUser(userCreateDto);            // DTO → Dominio
        User newClient = createUserUseCase.create(user, "USER"); // Lógica de negocio
        UserCreateDto response = userMapper.toDto(newClient);     // Dominio → DTO
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    //ResponseEntity<Void>
    public Map<String,String> login(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        );
        Authentication authentication = this.authenticationManager.authenticate(login);

        Long userId = createUserUseCase.idUSer(loginDto.getEmail());

        String jwt = jwtUtil.generateToken(authentication, userId);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(loginDto.getEmail());
        return Map.of(
                "accessToken", jwt,
                "refreshToken", refreshToken.getToken()
        );
    }

    @PostMapping("/refresh")
    public Map<String,String> refreshToken(@RequestBody DtoRefreshToken request) {

        // 1. Encontrar y Validar el Refresh Token Antiguo
        RefreshTokenEntity oldRefreshTokenEntity = refreshTokenService.findByToken(request.refreshToken)
                .orElseThrow(()-> new RuntimeException("Refresh token no válido"));

        // Obtener el UserEntity asociado antes de la posible expiración
        UserEntity userEntity = oldRefreshTokenEntity.getUserEntity();
        String userEmail = userEntity.getEmail();

        if(refreshTokenService.isRefreshTokenValidExpired(oldRefreshTokenEntity)) {
            // Si expira, lo revocamos y obligamos al usuario a loguearse de nuevo
            refreshTokenService.deleteRefreshToken(userEmail); // Usamos el email para la revocación
            throw new RuntimeException("Refresh token expirado. Por favor, inicie sesión de nuevo.");
        }

        // --- Inicio de la Rotación ---

        // 2. ROTACIÓN: Revocar el Refresh Token Antiguo
        // Esto garantiza que el token usado sea de un solo uso
        refreshTokenService.deleteRefreshToken(userEmail); // Asume una política de 1 RT por usuario

        // 3. ROTACIÓN: Crear un Nuevo Refresh Token (Nuevo UUID)
        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

        // 4. Generar el Nuevo Access Token (JWT)
        // Cargamos los detalles para recrear la autenticación (necesario para JwtUtil.generateToken)
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userEmail, null, userDetails.getAuthorities()
        );

        Long userId = createUserUseCase.idUSer(userEmail);

        String newJwt = jwtUtil.generateToken(newAuthentication, userId);

        // 5. Devolver Ambos Tokens
        return Map.of(
                "accessToken", newJwt,
                "refreshToken", newRefreshToken.getToken()
        );
    }

    record DtoRefreshToken(String refreshToken) {}
}

//  @PostMapping("/refresh")
//    public Map<String,String> refreshToken(@RequestBody DtoRefreshToken request) {
//        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(request.refreshToken)
//                .orElseThrow(()-> new RuntimeException("Refresh token not valid"));
//
//        if(refreshTokenService.isRefreshTokenValidExpired(refreshTokenEntity)) {
//            refreshTokenService.deleteRefreshToken(refreshTokenEntity.getUserEntity().getName());
//            throw new RuntimeException("Refresh token expired");
//        }
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(
//                refreshTokenEntity.getUserEntity().getEmail()
//        );
//
//
//        String jwt = jwtUtil.generateToken(new
//                UsernamePasswordAuthenticationToken(refreshTokenEntity.getUserEntity().getEmail(), null, userDetails.getAuthorities()));
//
//        return Map.of("accessToken", jwt);
//    }
