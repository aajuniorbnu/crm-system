package com.empresa.crm_system.security;

import com.empresa.crm_system.Corretor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CorretorDetails implements UserDetails {

    private final Corretor corretor;

    public CorretorDetails(Corretor corretor) {
        this.corretor = corretor;
    }

    public Corretor getCorretor() {
        return corretor;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CORRETOR"));
    }

    @Override
    public String getPassword() {
        return corretor.getSenha();
    }

    @Override
    public String getUsername() {
        return corretor.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(corretor.getAtivo());
    }
}
