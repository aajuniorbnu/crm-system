package com.empresa.crm_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @GetMapping
    public String relatorios(Model model) {
        return "relatorios";
    }
}
