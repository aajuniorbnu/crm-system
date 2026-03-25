package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.PortalPageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private PortalPageService portalPageService;

    @GetMapping
    public String relatorios(Model model) {
        return portalPageService.render(model, "relatorios");
    }
}
