package com.empresa.crm_system.service;

import com.empresa.crm_system.Venda;
import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.enums.StatusVenda;
import com.empresa.crm_system.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class VendaService extends BaseService<Venda, Long> {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteService clienteService;

    @Override
    protected JpaRepository<Venda, Long> getRepository() {
        return vendaRepository;
    }

    /**
     * Hook: Define data de venda e calcula valores antes de salvar
     */
    @Override
    protected void beforeSave(Venda venda) {
        if (venda.getId() == null) {
            venda.setDataVenda(LocalDateTime.now());
        }
        
        // Define status padrão
        if (venda.getStatus() == null) {
            venda.setStatus(StatusVenda.PENDENTE);
        }
        
        // Calcula valor final
        calcularValorFinal(venda);
    }

    /**
     * Hook: Atualiza última compra do cliente após salvar venda
     */
    @Override
    protected void afterSave(Venda venda) {
        if (venda.getCliente() != null && venda.getCliente().getId() != null) {
            clienteService.atualizarUltimaCompra(venda.getCliente().getId());
        }
    }

    /**
     * Calcula o valor final da venda
     */
    private void calcularValorFinal(Venda venda) {
        if (venda.getValorTotal() != null) {
            Double desconto = venda.getDesconto() != null ? venda.getDesconto() : 0.0;
            venda.setValorFinal(venda.getValorTotal() - desconto);
        }
    }

    /**
     * Busca vendas por cliente
     */
    @Transactional(readOnly = true)
    public List<Venda> buscarPorCliente(Cliente cliente) {
        return vendaRepository.findByCliente(cliente);
    }

    /**
     * Busca vendas por status
     */
    @Transactional(readOnly = true)
    public List<Venda> buscarPorStatus(StatusVenda status) {
        return vendaRepository.findByStatus(status);
    }

    /**
     * Busca vendas por período
     */
    @Transactional(readOnly = true)
    public List<Venda> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return vendaRepository.findByDataVendaBetween(inicio, fim);
    }

    /**
     * Busca últimas N vendas
     */
    @Transactional(readOnly = true)
    public List<Venda> buscarUltimas(int quantidade) {
        return vendaRepository.findUltimasVendas().stream()
            .limit(quantidade)
            .toList();
    }

    /**
     * Conta vendas do mês atual
     */
    @Transactional(readOnly = true)
    public long contarVendasMesAtual() {
        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicio = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fim = mesAtual.atEndOfMonth().atTime(23, 59, 59);
        return buscarPorPeriodo(inicio, fim).size();
    }

    /**
     * Calcula faturamento do mês atual
     */
    @Transactional(readOnly = true)
    public double calcularFaturamentoMesAtual() {
        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicio = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fim = mesAtual.atEndOfMonth().atTime(23, 59, 59);
        
        return buscarPorPeriodo(inicio, fim).stream()
            .filter(v -> v.getStatus() == StatusVenda.APROVADA || v.getStatus() == StatusVenda.ENTREGUE)
            .mapToDouble(v -> v.getValorFinal() != null ? v.getValorFinal() : 0.0)
            .sum();
    }

    /**
     * Calcula faturamento por período
     */
    @Transactional(readOnly = true)
    public double calcularFaturamentoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return buscarPorPeriodo(inicio, fim).stream()
            .filter(v -> v.getStatus() == StatusVenda.APROVADA || v.getStatus() == StatusVenda.ENTREGUE)
            .mapToDouble(v -> v.getValorFinal() != null ? v.getValorFinal() : 0.0)
            .sum();
    }

    /**
     * Aprova uma venda
     */
    @Transactional
    public Venda aprovar(Long vendaId) {
        return buscarPorId(vendaId)
            .map(venda -> {
                venda.setStatus(StatusVenda.APROVADA);
                return salvar(venda);
            })
            .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + vendaId));
    }

    /**
     * Cancela uma venda
     */
    @Transactional
    public Venda cancelar(Long vendaId) {
        return buscarPorId(vendaId)
            .map(venda -> {
                venda.setStatus(StatusVenda.CANCELADA);
                return salvar(venda);
            })
            .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + vendaId));
    }

    /**
     * Marca venda como entregue
     */
    @Transactional
    public Venda marcarComoEntregue(Long vendaId) {
        return buscarPorId(vendaId)
            .map(venda -> {
                if (venda.getStatus() != StatusVenda.APROVADA) {
                    throw new RuntimeException("Apenas vendas aprovadas podem ser marcadas como entregues");
                }
                venda.setStatus(StatusVenda.ENTREGUE);
                return salvar(venda);
            })
            .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + vendaId));
    }

    /**
     * Conta vendas por status
     */
    @Transactional(readOnly = true)
    public long contarPorStatus(StatusVenda status) {
        return buscarPorStatus(status).size();
    }

    public List<Venda> listarTodas() {
        return listarTodos();
    }
}
