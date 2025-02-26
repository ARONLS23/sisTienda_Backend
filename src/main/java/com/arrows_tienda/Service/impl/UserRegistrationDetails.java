package com.arrows_tienda.Service.impl;

import com.arrows_tienda.Models.Usuario;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserRegistrationDetails implements UserDetails {

    private String userName;

    private String password;

    private boolean isEnabled;

    private List<GrantedAuthority> authorities;

    public UserRegistrationDetails(Usuario user) {
        this.isEnabled = user.isEnabled();
        this.password = user.getPassword();
        this.userName = user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
