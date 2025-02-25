package com.arrows_tienda.Dto;


public record AuthResponseDto(
        String token,
        AuthStatus authStatus,
        String message) {
}
