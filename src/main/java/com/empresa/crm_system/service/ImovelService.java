package com.empresa.crm_system.service;

import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.enums.CategoriaImovel;
import com.empresa.crm_system.enums.FinalidadeImovel;
import com.empresa.crm_system.enums.StatusImovel;
import com.empresa.crm_system.repository.ImovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImovelService extends BaseService<Imovel, Long> {

    @Autowired
    private ImovelRepository imovelRepository;

    @Override
    protected JpaRepository<Imovel, Long> getRepository() {
        return imovelRepository;
    }

    @Override
    protected void beforeSave(Imovel imovel) {
        if (imovel.getId() == null) {
            imovel.setDataCadastro(LocalDateTime.now());
        }

        if (imovel.getStatus() == null) {
            imovel.setStatus(StatusImovel.DISPONIVEL);
        }

        if (imovel.getDestaque() == null) {
            imovel.setDestaque(Boolean.FALSE);
        }
    }

    @Transactional(readOnly = true)
    public List<Imovel> filtrar(FinalidadeImovel finalidade, CategoriaImovel categoria, StatusImovel status,
                                String cidade, Boolean destaque) {
        return imovelRepository.findAll().stream()
                .filter(imovel -> finalidade == null || imovel.getFinalidade() == finalidade)
                .filter(imovel -> categoria == null || imovel.getCategoria() == categoria)
                .filter(imovel -> status == null || imovel.getStatus() == status)
                .filter(imovel -> destaque == null || destaque.equals(imovel.getDestaque()))
                .filter(imovel -> cidade == null || cidade.isBlank()
                        || (imovel.getCidade() != null && imovel.getCidade().equalsIgnoreCase(cidade.trim())))
                .toList();
    }

    @Transactional(readOnly = true)
    public double calcularValorMedioMetroQuadrado(FinalidadeImovel finalidade) {
        return filtrar(finalidade, null, StatusImovel.DISPONIVEL, null, null).stream()
                .filter(imovel -> imovel.getAreaM2() != null && imovel.getAreaM2() > 0)
                .filter(imovel -> imovel.getValor() != null && imovel.getValor() > 0)
                .mapToDouble(imovel -> imovel.getValor() / imovel.getAreaM2())
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public long contarDisponiveisPorFinalidade(FinalidadeImovel finalidade) {
        return filtrar(finalidade, null, StatusImovel.DISPONIVEL, null, null).size();
    }
}
