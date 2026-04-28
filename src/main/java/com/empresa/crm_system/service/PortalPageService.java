package com.empresa.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class PortalPageService {

    @Autowired
    private ClienteService clienteService;

    public String render(Model model, String pagina) {
        model.addAttribute("pagina", pagina);
        model.addAttribute("totalClientes", clienteService.contarTotal());
        model.addAttribute("clientes", clienteService.listarTodos());

        return "app";
    }
}
