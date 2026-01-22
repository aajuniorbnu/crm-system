package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tickets", ticketService.listarTodos());
        return "tickets";
    }
}
