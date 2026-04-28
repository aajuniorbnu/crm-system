package com.empresa.crm_system.repository;

import com.empresa.crm_system.Corretor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorretorRepository extends JpaRepository<Corretor, Long> {
    Optional<Corretor> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
