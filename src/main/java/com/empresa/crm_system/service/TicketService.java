package com.empresa.crm_system.service;

import com.empresa.crm_system.Ticket;
import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.enums.StatusTicket;
import com.empresa.crm_system.enums.PrioridadeTicket;
import com.empresa.crm_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService extends BaseService<Ticket, Long> {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    protected JpaRepository<Ticket, Long> getRepository() {
        return ticketRepository;
    }

    /**
     * Hook: Define data de abertura e status padrão antes de salvar
     */
    @Override
    protected void beforeSave(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setDataAbertura(LocalDateTime.now());
            
            // Define status padrão
            if (ticket.getStatus() == null) {
                ticket.setStatus(StatusTicket.ABERTO);
            }
            
            // Define prioridade padrão
            if (ticket.getPrioridade() == null) {
                ticket.setPrioridade(PrioridadeTicket.MEDIA);
            }
        }
        
        // Define data de fechamento quando status é FECHADO ou RESOLVIDO
        if (ticket.getStatus() == StatusTicket.FECHADO || ticket.getStatus() == StatusTicket.RESOLVIDO) {
            if (ticket.getDataFechamento() == null) {
                ticket.setDataFechamento(LocalDateTime.now());
            }
        }
    }

    /**
     * Busca tickets por cliente
     */
    @Transactional(readOnly = true)
    public List<Ticket> buscarPorCliente(Cliente cliente) {
        return ticketRepository.findByCliente(cliente);
    }

    /**
     * Busca tickets por status
     */
    @Transactional(readOnly = true)
    public List<Ticket> buscarPorStatus(StatusTicket status) {
        return ticketRepository.findByStatus(status);
    }

    /**
     * Conta tickets por status
     */
    @Transactional(readOnly = true)
    public long contarPorStatus(StatusTicket status) {
        return ticketRepository.countByStatus(status);
    }

    /**
     * Conta tickets abertos
     */
    @Transactional(readOnly = true)
    public long contarAbertos() {
        return contarPorStatus(StatusTicket.ABERTO);
    }

    /**
     * Conta tickets em andamento
     */
    @Transactional(readOnly = true)
    public long contarEmAndamento() {
        return contarPorStatus(StatusTicket.EM_ANDAMENTO);
    }

    /**
     * Conta tickets resolvidos
     */
    @Transactional(readOnly = true)
    public long contarResolvidos() {
        return contarPorStatus(StatusTicket.RESOLVIDO);
    }

    /**
     * Busca tickets por prioridade
     */
    @Transactional(readOnly = true)
    public List<Ticket> buscarPorPrioridade(PrioridadeTicket prioridade) {
        return listarTodos().stream()
            .filter(t -> t.getPrioridade() == prioridade)
            .toList();
    }

    /**
     * Busca tickets urgentes em aberto
     */
    @Transactional(readOnly = true)
    public List<Ticket> buscarUrgentesAbertos() {
        return buscarPorStatus(StatusTicket.ABERTO).stream()
            .filter(t -> t.getPrioridade() == PrioridadeTicket.URGENTE)
            .toList();
    }

    /**
     * Inicia atendimento do ticket
     */
    @Transactional
    public Ticket iniciarAtendimento(Long ticketId) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                if (ticket.getStatus() != StatusTicket.ABERTO) {
                    throw new RuntimeException("Apenas tickets abertos podem ser iniciados");
                }
                ticket.setStatus(StatusTicket.EM_ANDAMENTO);
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Marca ticket como aguardando cliente
     */
    @Transactional
    public Ticket aguardarCliente(Long ticketId) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                ticket.setStatus(StatusTicket.AGUARDANDO_CLIENTE);
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Resolve o ticket
     */
    @Transactional
    public Ticket resolver(Long ticketId) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                ticket.setStatus(StatusTicket.RESOLVIDO);
                ticket.setDataFechamento(LocalDateTime.now());
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Fecha o ticket
     */
    @Transactional
    public Ticket fechar(Long ticketId) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                if (ticket.getStatus() != StatusTicket.RESOLVIDO) {
                    throw new RuntimeException("Apenas tickets resolvidos podem ser fechados");
                }
                ticket.setStatus(StatusTicket.FECHADO);
                ticket.setDataFechamento(LocalDateTime.now());
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Reabre um ticket
     */
    @Transactional
    public Ticket reabrir(Long ticketId) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                ticket.setStatus(StatusTicket.ABERTO);
                ticket.setDataFechamento(null);
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Altera prioridade do ticket
     */
    @Transactional
    public Ticket alterarPrioridade(Long ticketId, PrioridadeTicket novaPrioridade) {
        return buscarPorId(ticketId)
            .map(ticket -> {
                ticket.setPrioridade(novaPrioridade);
                return salvar(ticket);
            })
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado: " + ticketId));
    }

    /**
     * Calcula tempo médio de resolução (em horas)
     */
    @Transactional(readOnly = true)
    public double calcularTempoMedioResolucao() {
        List<Ticket> ticketsResolvidos = buscarPorStatus(StatusTicket.RESOLVIDO);
        
        if (ticketsResolvidos.isEmpty()) {
            return 0.0;
        }
        
        return ticketsResolvidos.stream()
            .filter(t -> t.getDataFechamento() != null)
            .mapToLong(t -> {
                long diff = java.time.Duration.between(
                    t.getDataAbertura(), 
                    t.getDataFechamento()
                ).toHours();
                return diff;
            })
            .average()
            .orElse(0.0);
    }
}