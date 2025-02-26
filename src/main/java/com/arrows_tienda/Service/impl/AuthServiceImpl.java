package com.arrows_tienda.Service.impl;

import com.arrows_tienda.Listener.RegistrationCompleteEvent;
import com.arrows_tienda.Models.Usuario;
import com.arrows_tienda.Repository.UserRepository;
import com.arrows_tienda.Service.AuthService;
import com.arrows_tienda.Token.VerificationToken;
import com.arrows_tienda.Token.VerificationTokenRepository;
import com.arrows_tienda.Util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String login(String username, String password) {
        Optional<Usuario> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario not found");
        }

        Usuario user = optionalUser.get();

        if (!user.isEnabled()) {
            throw new RuntimeException("La cuenta no ha sido verificada");
        }

        var authToken = new UsernamePasswordAuthenticationToken(username, password);

        var authenticate = authenticationManager.authenticate(authToken);

        return JwtUtils.generateToken(((UserDetails) (authenticate.getPrincipal())).getUsername());
    }

    @Override
    public String signup(String nombre, String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El correo electronico ya existe");
        }
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setNombre(nombre);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        user.setFechaRegistro(LocalDateTime.now());
        user.setEnabled(false);

        userRepository.save(user);

        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, "..."));

        return "Verifica tu email";
    }

    @Override
    public String verifyToken(String token) {
        var usernameOptional = JwtUtils.getUsernameFromToken(token);

        if (usernameOptional.isPresent()) {
            return usernameOptional.get();
        }

        throw new RuntimeException("Invalid token");
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(Usuario usuario, String token) {
        var verificationToken = new VerificationToken(token, usuario);
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String token) {
        VerificationToken tokenValid = tokenRepository.findByToken(token);

        if (tokenValid == null) {
            System.out.println("Token not found en la BD");
            return "token de verificación no valido";
        }

        Usuario user = tokenValid.getUser();
        System.out.println("Estado actual del usuario: " + user.isEnabled());

        Calendar calendar = Calendar.getInstance();
        if ((tokenValid.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            tokenRepository.delete(tokenValid);
            System.out.println("Token expirado y eliminado");

            return "expired";
        }

        user.setEnabled(true);

        try {
            userRepository.save(user);
            System.out.println("Usuario actualizado. Nuevo estado: {} " + user.isEnabled());

            tokenRepository.delete(tokenValid);
            return "valido";

        } catch (Exception e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return "Error al actualizar usuario";
        }
    }
}
