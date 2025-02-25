package com.arrows_tienda.Controller;

import com.arrows_tienda.Dto.AuthRequestDto;
import com.arrows_tienda.Dto.AuthResponseDto;
import com.arrows_tienda.Dto.AuthStatus;
import com.arrows_tienda.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());

        var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
    }

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody AuthRequestDto authRequestDto) {
        try {
            var jwtToken = authService.signup(authRequestDto.nombre(), authRequestDto.username(), authRequestDto.password(), authRequestDto.email());

            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.USER_CREATED_SUCCESSFULLY);

            return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
        }
    }


}
