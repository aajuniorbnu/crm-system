package com.empresa.crm_system;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import br.com.objectos.serasa.relato.factoring.TipoCliente;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private String email;
    private String telefone;
    private String cpfCnpj;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    
    @Enumerated(EnumType.STRING)
    private TipoCliente tipo;
    
    @Enumerated(EnumType.STRING)
    private StatusCliente status;
    
    private LocalDateTime dataCadastro;
    private LocalDateTime ultimaCompra;
    
    @OneToMany(mappedBy = "cliente")
    private List<Venda> vendas;
    
    @OneToMany(mappedBy = "cliente")
    private List<Ticket> tickets;
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    
    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }
    
    public StatusCliente getStatus() { return status; }
    public void setStatus(StatusCliente status) { this.status = status; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(LocalDateTime ultimaCompra) { this.ultimaCompra = ultimaCompra; }
    
    public List<Venda> getVendas() { return vendas; }
    public void setVendas(List<Venda> vendas) { this.vendas = vendas; }
    
    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    
}
