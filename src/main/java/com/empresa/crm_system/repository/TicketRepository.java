package com.empresa.crm_system.repository;

import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.Ticket;
import com.empresa.crm_system.enums.StatusTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCliente(Cliente cliente);
    List<Ticket> findByStatus(StatusTicket status);
    long countByStatus(StatusTicket status);
}
