package com.empresa.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class PortalPageService {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private VendaService vendaService;

    @Autowired
    private TicketService ticketService;

    public String render(Model model, String pagina) {
        model.addAttribute("pagina", pagina);
        model.addAttribute("totalClientes", clienteService.contarTotal());
        model.addAttribute("clientes", clienteService.listarTodos());

        model.addAttribute("totalProdutos", produtoService.contarTotal());
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("valorEstoque", produtoService.calcularValorEstoque());
        model.addAttribute("produtosEstoqueBaixo", produtoService.buscarEstoqueBaixo());

        model.addAttribute("vendasMes", vendaService.contarVendasMesAtual());
        model.addAttribute("faturamentoMes", vendaService.calcularFaturamentoMesAtual());
        model.addAttribute("ultimasVendas", vendaService.buscarUltimas(5));
        model.addAttribute("vendas", vendaService.listarTodas());

        model.addAttribute("ticketsAbertos", ticketService.contarAbertos());
        model.addAttribute("ticketsResolvidos", ticketService.contarResolvidos());
        model.addAttribute("tickets", ticketService.listarTodos());

        return "app";
    }
}
