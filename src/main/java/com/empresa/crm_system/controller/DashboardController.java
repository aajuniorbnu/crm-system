package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.ClienteService;
import com.empresa.crm_system.service.ProdutoService;
import com.empresa.crm_system.service.TicketService;
import com.empresa.crm_system.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private VendaService vendaService;

    @Autowired
    private TicketService ticketService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalClientes", clienteService.contarTotal());
        model.addAttribute("vendasMes", vendaService.contarVendasMesAtual());
        model.addAttribute("totalProdutos", produtoService.contarTotal());
        model.addAttribute("ticketsAbertos", ticketService.contarAbertos());
        model.addAttribute("ultimasVendas", vendaService.buscarUltimas(5));
        model.addAttribute("produtosEstoqueBaixo", produtoService.buscarEstoqueBaixo());
        return "dashboard";
    }
}
