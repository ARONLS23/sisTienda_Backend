package com.arrows_tienda.Dto;

public record AuthRequestDto(
        String nombre,
        String username,
        String password,
        String email
) {
}
