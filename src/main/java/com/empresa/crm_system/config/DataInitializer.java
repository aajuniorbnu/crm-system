package com.empresa.crm_system.config;

import com.empresa.crm_system.Cliente;
import com.empresa.crm_system.Imovel;
import com.empresa.crm_system.Produto;
import com.empresa.crm_system.enums.CategoriaImovel;
import com.empresa.crm_system.enums.FinalidadeImovel;
import com.empresa.crm_system.enums.StatusCliente;
import com.empresa.crm_system.enums.StatusImovel;
import com.empresa.crm_system.enums.StatusProduto;
import com.empresa.crm_system.enums.TipoCliente;
import com.empresa.crm_system.repository.ClienteRepository;
import com.empresa.crm_system.repository.ImovelRepository;
import com.empresa.crm_system.repository.ProdutoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner seedData(ProdutoRepository produtoRepository, ClienteRepository clienteRepository,
                               ImovelRepository imovelRepository) {
        return args -> {
            seedProdutos(produtoRepository);
            seedClientes(clienteRepository);
            seedImoveis(imovelRepository);
        };
    }

    private void seedProdutos(ProdutoRepository produtoRepository) {
        List<Produto> produtos = List.of(
                produto("APL-IP16E-128", "iPhone 16e 128GB", "Apple", 5799.00, 18, 5,
                        "iPhone de entrada da Apple com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("APL-IP16-128", "iPhone 16 128GB", "Apple", 6799.00, 15, 5,
                        "iPhone 16 com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("APL-IP17-128", "iPhone 17 128GB", "Apple", 7999.00, 12, 4,
                        "iPhone 17 com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("APL-IPAIR-128", "iPhone Air 128GB", "Apple", 10499.00, 8, 3,
                        "iPhone Air com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("APL-IP17PRO-256", "iPhone 17 Pro 256GB", "Apple", 12499.00, 6, 2,
                        "iPhone 17 Pro com 256 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-A26-256", "Galaxy A26 5G 256GB", "Samsung", 2299.00, 25, 8,
                        "Galaxy A26 5G com 256 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-A36-128", "Galaxy A36 5G 128GB", "Samsung", 2699.00, 22, 8,
                        "Galaxy A36 5G com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-A56-128", "Galaxy A56 5G 128GB", "Samsung", 2999.00, 20, 8,
                        "Galaxy A56 5G com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-S25-128", "Galaxy S25 128GB", "Samsung", 6999.00, 10, 4,
                        "Galaxy S25 com 128 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-S25PLUS-256", "Galaxy S25+ 256GB", "Samsung", 8499.00, 9, 3,
                        "Galaxy S25+ com 256 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-S25ULTRA-256", "Galaxy S25 Ultra 256GB", "Samsung", 11999.00, 7, 2,
                        "Galaxy S25 Ultra com 256 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-ZFLIP6-256", "Galaxy Z Flip6 256GB", "Samsung", 7999.00, 5, 2,
                        "Galaxy Z Flip6 com 256 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026."),
                produto("SMS-ZFOLD6-512", "Galaxy Z Fold6 512GB", "Samsung", 13799.00, 4, 1,
                        "Galaxy Z Fold6 com 512 GB. Preco de referencia do mercado brasileiro consultado em 25/03/2026.")
        );

        List<Produto> novosProdutos = produtos.stream()
                .filter(produto -> !produtoRepository.existsByCodigo(produto.getCodigo()))
                .toList();

        if (!novosProdutos.isEmpty()) {
            produtoRepository.saveAll(novosProdutos);
        }
    }

    private void seedClientes(ClienteRepository clienteRepository) {
        List<Cliente> clientes = List.of(
                cliente("Ana Beatriz Lima", "ana.lima@exemplo.com", "(11) 98888-1001", "123.456.789-01",
                        "Rua das Palmeiras, 145", "Sao Paulo", "SP", "04567-000", TipoCliente.PESSOA_FISICA, StatusCliente.ATIVO, 30),
                cliente("Carlos Eduardo Souza", "carlos.souza@exemplo.com", "(21) 97777-2002", "987.654.321-00",
                        "Avenida Atlantica, 900", "Rio de Janeiro", "RJ", "22021-001", TipoCliente.PESSOA_FISICA, StatusCliente.ATIVO, 12),
                cliente("Fernanda Rocha", "fernanda.rocha@exemplo.com", "(31) 96666-3003", "456.123.789-22",
                        "Rua da Bahia, 350", "Belo Horizonte", "MG", "30160-011", TipoCliente.PESSOA_FISICA, StatusCliente.PROSPECTO, null),
                cliente("Lucas Martins", "lucas.martins@exemplo.com", "(41) 95555-4004", "741.852.963-55",
                        "Rua Marechal Deodoro, 77", "Curitiba", "PR", "80010-010", TipoCliente.PESSOA_FISICA, StatusCliente.ATIVO, 45),
                cliente("Mariana Costa", "mariana.costa@exemplo.com", "(85) 94444-5005", "159.357.468-99",
                        "Avenida Beira Mar, 1200", "Fortaleza", "CE", "60165-121", TipoCliente.PESSOA_FISICA, StatusCliente.ATIVO, 7),
                cliente("Tech Prime Solucoes Ltda", "contato@techprime.com.br", "(11) 3333-1000", "12.345.678/0001-90",
                        "Alameda Santos, 680", "Sao Paulo", "SP", "01418-100", TipoCliente.PESSOA_JURIDICA, StatusCliente.ATIVO, 20),
                cliente("Varejo Conecta SA", "compras@varejoconecta.com.br", "(21) 3000-2020", "98.765.432/0001-10",
                        "Rua do Ouvidor, 210", "Rio de Janeiro", "RJ", "20040-030", TipoCliente.PESSOA_JURIDICA, StatusCliente.ATIVO, 55),
                cliente("Inova Mobile Comercio", "vendas@inovamobile.com.br", "(51) 3222-3030", "45.678.901/0001-23",
                        "Avenida Ipiranga, 4040", "Porto Alegre", "RS", "90610-000", TipoCliente.PESSOA_JURIDICA, StatusCliente.PROSPECTO, null),
                cliente("Grupo Horizonte Digital", "financeiro@horizontedigital.com.br", "(62) 3555-4040", "67.890.123/0001-45",
                        "Rua 9, 512", "Goiania", "GO", "74013-010", TipoCliente.PESSOA_JURIDICA, StatusCliente.ATIVO, 18),
                cliente("Nordeste Smart Distribuicao", "atendimento@nordestesmart.com.br", "(81) 3777-5050", "23.456.789/0001-54",
                        "Avenida Recife, 1500", "Recife", "PE", "50820-001", TipoCliente.PESSOA_JURIDICA, StatusCliente.INATIVO, 120)
        );

        List<Cliente> novosClientes = clientes.stream()
                .filter(cliente -> !clienteRepository.existsByEmail(cliente.getEmail()))
                .toList();

        if (!novosClientes.isEmpty()) {
            clienteRepository.saveAll(novosClientes);
        }
    }

    private void seedImoveis(ImovelRepository imovelRepository) {
        List<Imovel> imoveis = List.of(
                imovel("SP-CB-001", "Cobertura panoramica com terraco gourmet", CategoriaImovel.COBERTURA,
                        FinalidadeImovel.VENDA, StatusImovel.DISPONIVEL, "Sao Paulo", "Vila Mariana",
                        "Rua Joaquim Tavora, 890", "SP", "04015-012", 186.0, 3, 4, 3,
                        2450000.0, 2100.0, 780.0, true,
                        "Vista aberta, automacao, suite master e area social integrada."),
                imovel("SP-AP-002", "Apartamento compacto perto do metrô", CategoriaImovel.APARTAMENTO,
                        FinalidadeImovel.ALUGUEL, StatusImovel.DISPONIVEL, "Sao Paulo", "Pinheiros",
                        "Rua dos Pinheiros, 520", "SP", "05422-001", 62.0, 2, 2, 1,
                        4800.0, 780.0, 190.0, true,
                        "Ideal para mobilidade urbana com varanda, coworking e lazer completo."),
                imovel("RJ-CS-003", "Casa com jardim e piscina para familia", CategoriaImovel.CASA,
                        FinalidadeImovel.VENDA, StatusImovel.DISPONIVEL, "Rio de Janeiro", "Barra da Tijuca",
                        "Avenida das Americas, 4500", "RJ", "22640-102", 320.0, 4, 5, 4,
                        3980000.0, 950.0, 620.0, true,
                        "Projeto contemporaneo, piscina aquecida, espaco gourmet e home office."),
                imovel("BH-AP-004", "Apartamento com excelente custo-beneficio", CategoriaImovel.APARTAMENTO,
                        FinalidadeImovel.VENDA, StatusImovel.DISPONIVEL, "Belo Horizonte", "Funcionarios",
                        "Rua dos Inconfidentes, 321", "MG", "30140-120", 94.0, 3, 2, 2,
                        920000.0, 540.0, 160.0, false,
                        "Planta inteligente, iluminacao natural e acesso rapido a servicos."),
                imovel("CT-CO-005", "Sala comercial pronta para consultorio", CategoriaImovel.COMERCIAL,
                        FinalidadeImovel.ALUGUEL, StatusImovel.DISPONIVEL, "Curitiba", "Centro",
                        "Rua Marechal Floriano, 120", "PR", "80020-090", 48.0, 0, 1, 1,
                        3200.0, 650.0, 145.0, false,
                        "Espaco versatil com recepcao, elevador e infraestrutura de seguranca."),
                imovel("GO-TR-006", "Terreno plano em condominio fechado", CategoriaImovel.TERRENO,
                        FinalidadeImovel.VENDA, StatusImovel.DISPONIVEL, "Goiania", "Jardins Milao",
                        "Quadra 12, lote 08", "GO", "74370-900", 420.0, 0, 0, 0,
                        690000.0, 390.0, 90.0, false,
                        "Terreno regular com clube, seguranca 24h e alto potencial de valorizacao.")
        );

        List<Imovel> novosImoveis = imoveis.stream()
                .filter(imovel -> !imovelRepository.existsByCodigo(imovel.getCodigo()))
                .toList();

        if (!novosImoveis.isEmpty()) {
            imovelRepository.saveAll(novosImoveis);
        }
    }

    private Produto produto(String codigo, String nome, String marca, Double preco, Integer estoque,
                            Integer estoqueMinimo, String descricao) {
        Produto produto = new Produto();
        produto.setCodigo(codigo);
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setCategoria("Smartphone " + marca);
        produto.setPreco(preco);
        produto.setEstoque(estoque);
        produto.setEstoqueMinimo(estoqueMinimo);
        produto.setUnidadeMedida("UN");
        produto.setStatus(estoque > 0 ? StatusProduto.ATIVO : StatusProduto.ESGOTADO);
        produto.setDataCadastro(LocalDateTime.now());
        return produto;
    }

    private Cliente cliente(String nome, String email, String telefone, String cpfCnpj, String endereco,
                            String cidade, String estado, String cep, TipoCliente tipo, StatusCliente status,
                            Integer diasDesdeUltimaCompra) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setCpfCnpj(cpfCnpj);
        cliente.setEndereco(endereco);
        cliente.setCidade(cidade);
        cliente.setEstado(estado);
        cliente.setCep(cep);
        cliente.setTipo(tipo);
        cliente.setStatus(status);
        cliente.setDataCadastro(LocalDateTime.now().minusDays(90));

        if (diasDesdeUltimaCompra != null) {
            cliente.setUltimaCompra(LocalDateTime.now().minusDays(diasDesdeUltimaCompra));
        }

        return cliente;
    }

    private Imovel imovel(String codigo, String titulo, CategoriaImovel categoria, FinalidadeImovel finalidade,
                          StatusImovel status, String cidade, String bairro, String endereco, String estado,
                          String cep, Double areaM2, Integer quartos, Integer banheiros, Integer vagas,
                          Double valor, Double condominio, Double iptu, Boolean destaque, String descricao) {
        Imovel imovel = new Imovel();
        imovel.setCodigo(codigo);
        imovel.setTitulo(titulo);
        imovel.setCategoria(categoria);
        imovel.setFinalidade(finalidade);
        imovel.setStatus(status);
        imovel.setCidade(cidade);
        imovel.setBairro(bairro);
        imovel.setEndereco(endereco);
        imovel.setEstado(estado);
        imovel.setCep(cep);
        imovel.setAreaM2(areaM2);
        imovel.setQuartos(quartos);
        imovel.setBanheiros(banheiros);
        imovel.setVagas(vagas);
        imovel.setValor(valor);
        imovel.setCondominio(condominio);
        imovel.setIptu(iptu);
        imovel.setDestaque(destaque);
        imovel.setDescricao(descricao);
        imovel.setDataCadastro(LocalDateTime.now().minusDays(15));
        return imovel;
    }
}
