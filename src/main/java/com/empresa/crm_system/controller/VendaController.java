package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vendas", vendaService.listarTodas());
        return "vendas";
    }
}
