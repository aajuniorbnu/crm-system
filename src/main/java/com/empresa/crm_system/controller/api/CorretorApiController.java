package com.empresa.crm_system.controller.api;

import com.empresa.crm_system.Corretor;
import com.empresa.crm_system.security.CorretorDetails;
import com.empresa.crm_system.service.CorretorService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/corretores/me")
public class CorretorApiController {

    private final CorretorService corretorService;

    public CorretorApiController(CorretorService corretorService) {
        this.corretorService = corretorService;
    }

    @GetMapping
    public Corretor perfil(@AuthenticationPrincipal CorretorDetails principal) {
        return principal.getCorretor();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@AuthenticationPrincipal CorretorDetails principal) {
        return corretorService.montarDashboard(principal.getCorretor());
    }

    @PutMapping(value = "/perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Corretor atualizarPerfil(
            @AuthenticationPrincipal CorretorDetails principal,
            @RequestParam String nome,
            @RequestParam String telefone,
            @RequestParam String creci,
            @RequestParam(required = false) MultipartFile foto) {
        return corretorService.atualizarPerfil(principal.getCorretor(), nome, telefone, creci, foto);
    }
}
