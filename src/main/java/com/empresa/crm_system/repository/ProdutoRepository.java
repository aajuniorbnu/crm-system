package com.empresa.crm_system.repository;

import com.empresa.crm_system.Produto;
import com.empresa.crm_system.enums.StatusProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByStatus(StatusProduto status);
    List<Produto> findByCategoria(String categoria);
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Produto p WHERE p.estoque <= p.estoqueMinimo")
    List<Produto> findProdutosEstoqueBaixo();
}
