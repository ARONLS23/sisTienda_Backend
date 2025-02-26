package com.arrows_tienda.Controller;

import com.arrows_tienda.Dto.AuthRequestDto;
import com.arrows_tienda.Dto.AuthResponseDto;
import com.arrows_tienda.Dto.AuthStatus;
import com.arrows_tienda.Service.AuthService;
import com.arrows_tienda.Service.impl.AuthServiceImpl;
import com.arrows_tienda.Token.VerificationToken;
import com.arrows_tienda.Token.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final AuthServiceImpl authServiceImpl;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        try {
            var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());

            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS, "Inicio de sesion exitoso");

            return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            AuthStatus status = AuthStatus.LOGIN_FAILDED;

            if (errorMessage.contains("Usuario no encontrado")) {
                errorMessage = "Usuario no encontrado";
            } else if (errorMessage.contains("La cuenta no ha sido verificada")) {
                errorMessage = "La cuenta no ha sido verificada. Por favor, revise su correo electrónico.";
            } else if (errorMessage.contains("Bad credentials")) {
                errorMessage = "Usuario o contraseña incorrectos";
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

    @GetMapping("/verifyEmail")
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        System.out.println("Verifying email: " + token);

        try {
            VerificationToken theToken = tokenRepository.findByToken(token);
            if (theToken == null) {
                System.out.println("Token not found");
                response.sendRedirect("http://localhost:3000/verification?status=invalid-token");
                return;
            }

            String result = authService.validateToken(token);
            System.out.println("Resultado de la validación: " + result);

            switch (result) {
                case "valido":
                    response.sendRedirect("http://localhost:3000/verification?status=success");
                    break;

                case "expired":
                    response.sendRedirect("http://localhost:3000/verification?status=expired");
                    break;

                default:
                    response.sendRedirect("http://localhost:3000/verification?status=error");
            }

        } catch (Exception e) {
            System.out.println("Error durante la validación: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/verification?status=error");
        }

    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }


}
