package com.empresa.crm_system.security;

import com.empresa.crm_system.repository.CorretorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CorretorDetailsService implements UserDetailsService {

    private final CorretorRepository corretorRepository;

    public CorretorDetailsService(CorretorRepository corretorRepository) {
        this.corretorRepository = corretorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return corretorRepository.findByEmailIgnoreCase(username)
                .map(CorretorDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Corretor nao encontrado"));
    }
}
