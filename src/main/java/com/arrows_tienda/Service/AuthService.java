package com.arrows_tienda.Service;

import com.arrows_tienda.Models.Usuario;

import java.util.Optional;

public interface AuthService {

    String login(String username, String password);

    String signup(String nombre, String username, String password, String email);

    String verifyToken(String token);

    Optional<Usuario> findByEmail(String email);

    void saveUserVerificationToken(Usuario usuario, String verificationToken);

    String validateToken(String token);

}
