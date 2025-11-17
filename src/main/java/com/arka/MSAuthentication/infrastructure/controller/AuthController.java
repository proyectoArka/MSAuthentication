package com.arka.MSAuthentication.infrastructure.controller;

import com.arka.MSAuthentication.application.dto.LoginDto;
import com.arka.MSAuthentication.application.dto.ConsultUserDto;
import com.arka.MSAuthentication.application.dto.UpdateInfoUserDto;
import com.arka.MSAuthentication.application.dto.UserCreateDto;
import com.arka.MSAuthentication.application.dto.UserViewDto;
import com.arka.MSAuthentication.application.mapper.UserMapper;
import com.arka.MSAuthentication.domain.model.User;
import com.arka.MSAuthentication.domain.usecase.CreateUserUseCase;
import com.arka.MSAuthentication.infrastructure.adapters.entity.RefreshTokenEntity;
import com.arka.MSAuthentication.infrastructure.adapters.entity.UserEntity;
import com.arka.MSAuthentication.infrastructure.config.JwtUtil;
import com.arka.MSAuthentication.infrastructure.config.RefreshToken.RefreshTokenService;
import com.arka.MSAuthentication.infrastructure.Exception.TokenRefreshException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Autenticación", description = "Endpoints para autenticación y gestión de usuarios")
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

    @Operation(summary = "Crear administrador", description = "Crea un usuario con rol Admin", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping("/createadmin")
    @PreAuthorize("hasAuthority('CreateAdmin')") // Solo si tiene este permiso
    public ResponseEntity<UserViewDto> createadmin(@Valid @RequestBody UserCreateDto userCreateDto){
        User user = userMapper.toUser(userCreateDto);            // DTO → Dominio
        User newAdmin = createUserUseCase.create(user, "Admin"); // Lógica de negocio
        UserViewDto response = userMapper.toViewDto(newAdmin);     // Dominio → DTO
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear cliente", description = "Crea un usuario con rol Customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping("/createclient")
    public ResponseEntity<UserViewDto> createclient(@Valid @RequestBody UserCreateDto userCreateDto){
        User user = userMapper.toUser(userCreateDto);            // DTO → Dominio
        User newClient = createUserUseCase.create(user, "Customer"); // Lógica de negocio
        UserViewDto response = userMapper.toViewDto(newClient);     // Dominio → DTO
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login", description = "Autentica credenciales y retorna tokens JWT y refresh")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/login")
    //ResponseEntity<Void>
    public Map<String,String> login(@Valid @RequestBody LoginDto loginDto) {
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

    @Operation(summary = "Refrescar token", description = "Intercambia refresh token válido por nuevo par de tokens", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens emitidos"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public Map<String,String> refreshToken(@Valid @RequestBody DtoRefreshToken request) {

        // 1. Encontrar y Validar el Refresh Token Antiguo
        RefreshTokenEntity oldRefreshTokenEntity = refreshTokenService.findByToken(request.refreshToken)
                .orElseThrow(()-> new TokenRefreshException(request.refreshToken, "Token no encontrado"));

        // Obtener el UserEntity asociado antes de la posible expiración
        UserEntity userEntity = oldRefreshTokenEntity.getUserEntity();
        String userEmail = userEntity.getEmail();

        if(refreshTokenService.isRefreshTokenValidExpired(oldRefreshTokenEntity)) {
            // Si expira, lo revocamos y obligamos al usuario a loguearse de nuevo
            refreshTokenService.deleteRefreshToken(userEmail);
            throw new TokenRefreshException(request.refreshToken, "Token expirado. Por favor, inicie sesión de nuevo");
        }

        // --- Inicio de la Rotación ---

        // 2. ROTACIÓN: Revocar el Refresh Token Antiguo
        refreshTokenService.deleteRefreshToken(userEmail);

        // 3. ROTACIÓN: Crear un Nuevo Refresh Token
        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

        // 4. Generar el Nuevo Access Token
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

    @Operation(summary = "Consultar usuario", description = "Obtiene datos básicos del usuario por ID", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    @GetMapping("/consuluser/{id}")
    public ResponseEntity<ConsultUserDto> getConsultUserDto(@PathVariable Long id) {
        ConsultUserDto consultUserDto = createUserUseCase.getConsultUser(id);
        return ResponseEntity.ok(consultUserDto);
    }

    @Operation(summary = "Actualizar información del usuario", description = "Actualiza campos modificables", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    @PutMapping("/updateuserinfo")
    public ResponseEntity<UserViewDto> updateInfoUser(@RequestHeader("X-Auth-User-Id") Long userId,
                                                @Valid @RequestBody UpdateInfoUserDto updateInfoUserDto){
        User user = createUserUseCase.updateInfoUser(userId, updateInfoUserDto);
        UserViewDto response = userMapper.toViewDto(user);
        return ResponseEntity.ok(response);
    }
    public record DtoRefreshToken(@NotBlank(message = "El refreshToken no puede estar vacío") String refreshToken) {}
}
