package com.arrows_tienda.Controller;

import com.arrows_tienda.Dto.AuthRequestDto;
import com.arrows_tienda.Dto.AuthResponseDto;
import com.arrows_tienda.Dto.AuthStatus;
import com.arrows_tienda.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        try {
            var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());

            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS, "Inicio de sesion exitoso");

            return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            AuthStatus status = AuthStatus.LOGIN_FAILDED;

            if (e.getMessage().contains("Bad credentials")) {
                errorMessage = "Usuario o contraseña incorrectas";
            } else if (e.getMessage().contains("User not found")) {
                errorMessage = "Usuario no encontrado";
            }

            var authResponseDto = new AuthResponseDto(null, status, errorMessage);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDto);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto authRequestDto) {
        try {
            var jwtToken = authService.signup(authRequestDto.nombre(), authRequestDto.username(), authRequestDto.password(), authRequestDto.email());

            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.USER_CREATED_SUCCESSFULLY, "usuario creado correctamente");

            return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            AuthStatus status = AuthStatus.USER_NOT_CREATED;

            if (e.getMessage().contains("Username already exists")) {
                errorMessage = "El nombre de usuario ya esta en uso";
            } else if (e.getMessage().contains("Email already exists")) {
                errorMessage = "El correo electronico ya esta registrado";
            }

            var authResponseDto = new AuthResponseDto(null, status, errorMessage);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
        }
    }


}
