package com.empresa.crm_system.repository;

import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.Venda;
import com.empresa.crm_system.enums.StatusVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByCliente(Cliente cliente);
    List<Venda> findByStatus(StatusVenda status);
    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT v FROM Venda v ORDER BY v.dataVenda DESC")
    List<Venda> findUltimasVendas();
}
