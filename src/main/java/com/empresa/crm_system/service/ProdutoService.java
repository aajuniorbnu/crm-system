package com.empresa.crm_system.service;

import com.empresa.crm_system.Produto;
import com.empresa.crm_system.enums.StatusProduto;
import com.empresa.crm_system.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService extends BaseService<Produto, Long> {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    protected JpaRepository<Produto, Long> getRepository() {
        return produtoRepository;
    }

    /**
     * Hook: Define data de cadastro e validações antes de salvar
     */
    @Override
    protected void beforeSave(Produto produto) {
        if (produto.getId() == null) {
            produto.setDataCadastro(LocalDateTime.now());
        }
        
        // Define status padrão
        if (produto.getStatus() == null) {
            produto.setStatus(StatusProduto.ATIVO);
        }
        
        // Define estoque mínimo padrão
        if (produto.getEstoqueMinimo() == null) {
            produto.setEstoqueMinimo(10);
        }
        
        // Atualiza status baseado no estoque
        if (produto.getEstoque() != null && produto.getEstoque() == 0) {
            produto.setStatus(StatusProduto.ESGOTADO);
        }
    }

    /**
     * Busca produtos por status
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarPorStatus(StatusProduto status) {
        return produtoRepository.findByStatus(status);
    }

    /**
     * Busca produtos por categoria
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    /**
     * Busca produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarEstoqueBaixo() {
        return produtoRepository.findProdutosEstoqueBaixo();
    }

    /**
     * Calcula valor total do estoque
     */
    @Transactional(readOnly = true)
    public double calcularValorEstoque() {
        return produtoRepository.findAll().stream()
            .filter(p -> p.getPreco() != null && p.getEstoque() != null)
            .mapToDouble(p -> p.getPreco() * p.getEstoque())
            .sum();
    }

    /**
     * Calcula valor do estoque por categoria
     */
    @Transactional(readOnly = true)
    public double calcularValorEstoquePorCategoria(String categoria) {
        return buscarPorCategoria(categoria).stream()
            .filter(p -> p.getPreco() != null && p.getEstoque() != null)
            .mapToDouble(p -> p.getPreco() * p.getEstoque())
            .sum();
    }

    /**
     * Adiciona estoque ao produto
     */
    @Transactional
    public Produto adicionarEstoque(Long produtoId, Integer quantidade) {
        return buscarPorId(produtoId)
            .map(produto -> {
                produto.setEstoque(produto.getEstoque() + quantidade);
                
                // Atualiza status se necessário
                if (produto.getEstoque() > 0 && produto.getStatus() == StatusProduto.ESGOTADO) {
                    produto.setStatus(StatusProduto.ATIVO);
                }
                
                return salvar(produto);
            })
            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));
    }

    /**
     * Remove estoque do produto
     */
    @Transactional
    public Produto removerEstoque(Long produtoId, Integer quantidade) {
        return buscarPorId(produtoId)
            .map(produto -> {
                if (produto.getEstoque() < quantidade) {
                    throw new RuntimeException("Estoque insuficiente");
                }
                
                produto.setEstoque(produto.getEstoque() - quantidade);
                
                // Atualiza status se esgotado
                if (produto.getEstoque() == 0) {
                    produto.setStatus(StatusProduto.ESGOTADO);
                }
                
                return salvar(produto);
            })
            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));
    }

    /**
     * Verifica se produto tem estoque disponível
     */
    @Transactional(readOnly = true)
    public boolean temEstoqueDisponivel(Long produtoId, Integer quantidadeDesejada) {
        return buscarPorId(produtoId)
            .map(produto -> produto.getEstoque() >= quantidadeDesejada)
            .orElse(false);
    }

    /**
     * Conta produtos ativos
     */
    @Transactional(readOnly = true)
    public long contarAtivos() {
        return buscarPorStatus(StatusProduto.ATIVO).size();
    }

    /**
     * Conta produtos esgotados
     */
    @Transactional(readOnly = true)
    public long contarEsgotados() {
        return buscarPorStatus(StatusProduto.ESGOTADO).size();
    }
}
