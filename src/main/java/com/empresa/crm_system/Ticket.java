package com.empresa.crm_system;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    private String assunto;
    
    @Column(length = 2000)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    private PrioridadeTicket prioridade;
    
    @Enumerated(EnumType.STRING)
    private StatusTicket status;
    
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public PrioridadeTicket getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeTicket prioridade) { this.prioridade = prioridade; }
    
    public StatusTicket getStatus() { return status; }
    public void setStatus(StatusTicket status) { this.status = status; }
    
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
}
