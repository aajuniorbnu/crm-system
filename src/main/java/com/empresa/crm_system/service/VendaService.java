package com.empresa.crm_system.service;

import com.empresa.crm_system.Venda;
import com.empresa.crm_system.enums.StatusVenda;
import com.empresa.crm_system.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }

    public Venda salvar(Venda venda) {
        if (venda.getId() == null) {
            venda.setDataVenda(LocalDateTime.now());
        }
        return vendaRepository.save(venda);
    }

    public void deletar(Long id) {
        vendaRepository.deleteById(id);
    }

    public List<Venda> buscarUltimas(int quantidade) {
        return vendaRepository.findUltimasVendas().stream()
            .limit(quantidade)
            .toList();
    }

    public long contarVendasMesAtual() {
        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicio = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fim = mesAtual.atEndOfMonth().atTime(23, 59, 59);
        return vendaRepository.findByDataVendaBetween(inicio, fim).size();
    }
}
