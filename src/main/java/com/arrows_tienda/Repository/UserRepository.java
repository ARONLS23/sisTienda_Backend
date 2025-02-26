package com.arrows_tienda.Repository;

import com.arrows_tienda.Models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {

    boolean existsByUsername(String username);

    Optional<Usuario> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

}
