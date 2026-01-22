package com.empresa.crm_system.service;

import com.empresa.crm_system.Ticket;
import com.empresa.crm_system.enums.StatusTicket;
import com.empresa.crm_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> buscarPorId(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket salvar(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setDataAbertura(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    public void deletar(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> buscarPorStatus(StatusTicket status) {
        return ticketRepository.findByStatus(status);
    }

    public long contarAbertos() {
        return ticketRepository.countByStatus(StatusTicket.ABERTO);
    }
}
