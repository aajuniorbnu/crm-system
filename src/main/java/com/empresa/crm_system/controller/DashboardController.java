package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.PortalPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private PortalPageService portalPageService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        return portalPageService.render(model, "dashboard");
    }
}
