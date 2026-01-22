package com.empresa.crm_system.repository;

import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.enums.StatusCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByStatus(StatusCliente status);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    Cliente findByEmail(String email);
}
