package com.empresa.crm_system.service;

import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.enums.StatusCliente;
import com.empresa.crm_system.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService extends BaseService<Cliente, Long> {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected JpaRepository<Cliente, Long> getRepository() {
        return clienteRepository;
    }

    /**
     * Hook: Define data de cadastro antes de salvar novo cliente
     */
    @Override
    protected void beforeSave(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setDataCadastro(LocalDateTime.now());
        }
        
        // Validações adicionais
        if (cliente.getStatus() == null) {
            cliente.setStatus(StatusCliente.ATIVO);
        }
    }

    /**
     * Busca clientes por status
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorStatus(StatusCliente status) {
        return clienteRepository.findByStatus(status);
    }

    /**
     * Busca clientes por nome (case insensitive)
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Busca cliente por email
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Atualiza status do cliente
     */
    @Transactional
    public Cliente atualizarStatus(Long id, StatusCliente novoStatus) {
        return buscarPorId(id)
            .map(cliente -> {
                cliente.setStatus(novoStatus);
                return salvar(cliente);
            })
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
    }

    /**
     * Atualiza data da última compra
     */
    @Transactional
    public void atualizarUltimaCompra(Long clienteId) {
        buscarPorId(clienteId).ifPresent(cliente -> {
            cliente.setUltimaCompra(LocalDateTime.now());
            salvar(cliente);
        });
    }

    /**
     * Conta clientes ativos
     */
    @Transactional(readOnly = true)
    public long contarAtivos() {
        return buscarPorStatus(StatusCliente.ATIVO).size();
    }

    /**
     * Conta clientes prospects
     */
    @Transactional(readOnly = true)
    public long contarProspectos() {
        return buscarPorStatus(StatusCliente.PROSPECTO).size();
    }
}