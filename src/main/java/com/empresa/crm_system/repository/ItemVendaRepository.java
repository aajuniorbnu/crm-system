package com.empresa.crm_system.repository;

import com.empresa.crm_system.ItemVenda;
import com.empresa.crm_system.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda(Venda venda);
}
