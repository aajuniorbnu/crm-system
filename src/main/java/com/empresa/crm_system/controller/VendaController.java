package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.PortalPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private PortalPageService portalPageService;

    @GetMapping
    public String listar(Model model) {
        return portalPageService.render(model, "vendas");
    }
}
