package com.empresa.crm_system.repository;

import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.enums.CategoriaImovel;
import com.empresa.crm_system.enums.FinalidadeImovel;
import com.empresa.crm_system.enums.StatusImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {
    List<Imovel> findByFinalidade(FinalidadeImovel finalidade);
    List<Imovel> findByCategoria(CategoriaImovel categoria);
    List<Imovel> findByStatus(StatusImovel status);
    List<Imovel> findByDestaqueTrue();
    boolean existsByCodigo(String codigo);
}
