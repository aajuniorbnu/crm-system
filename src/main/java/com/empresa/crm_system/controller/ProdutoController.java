package com.empresa.crm_system.controller;

import com.empresa.crm_system.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("totalProdutos", produtoService.contarTotal());
        model.addAttribute("valorEstoque", String.format("%.2f", produtoService.calcularValorEstoque()));
        model.addAttribute("produtosEstoqueBaixo", produtoService.buscarEstoqueBaixo().size());
        return "produtos";
    }
}
