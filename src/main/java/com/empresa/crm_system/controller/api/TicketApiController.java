package com.empresa.crm_system.controller.api;

import com.empresa.crm_system.Ticket;
import com.empresa.crm_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketApiController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> listarTodos() {
        return ticketService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscarPorId(@PathVariable Long id) {
        return ticketService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ticket criar(@RequestBody Ticket ticket) {
        return ticketService.salvar(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> atualizar(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ticketService.buscarPorId(id)
            .map(ticketExistente -> {
                ticket.setId(id);
                return ResponseEntity.ok(ticketService.salvar(ticket));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ticketService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
