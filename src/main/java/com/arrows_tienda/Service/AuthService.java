package com.arrows_tienda.Service;

public interface AuthService {

    String login(String username, String password);

    String signup(String nombre, String username, String password, String email);

    String verifyToken(String token);

}
