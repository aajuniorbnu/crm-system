package com.empresa.crm_system.service;

import com.empresa.crm_system.Produto;
import com.empresa.crm_system.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            produto.setDataCadastro(LocalDateTime.now());
        }
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public List<Produto> buscarEstoqueBaixo() {
        return produtoRepository.findProdutosEstoqueBaixo();
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    public long contarTotal() {
        return produtoRepository.count();
    }

    public double calcularValorEstoque() {
        return produtoRepository.findAll().stream()
            .mapToDouble(p -> p.getPreco() * p.getEstoque())
            .sum();
    }
}
