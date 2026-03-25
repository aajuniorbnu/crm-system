\set ON_ERROR_STOP on
\connect clientes_crm

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    codigo_cliente VARCHAR(30) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefone VARCHAR(30) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,
    tipo_cliente VARCHAR(20) NOT NULL,
    status_cliente VARCHAR(20) NOT NULL,
    endereco VARCHAR(180) NOT NULL,
    cidade VARCHAR(80) NOT NULL,
    estado CHAR(2) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    ultima_compra DATE,
    limite_credito NUMERIC(10, 2),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO clientes
    (codigo_cliente, nome, email, telefone, cpf_cnpj, tipo_cliente, status_cliente, endereco, cidade, estado, cep, ultima_compra, limite_credito)
VALUES
    ('CLI-0001', 'Ana Beatriz Lima', 'ana.lima@exemplo.com', '(11) 98888-1001', '123.456.789-01', 'PESSOA_FISICA', 'ATIVO', 'Rua das Palmeiras, 145', 'Sao Paulo', 'SP', '04567-000', DATE '2026-02-23', 7000.00),
    ('CLI-0002', 'Carlos Eduardo Souza', 'carlos.souza@exemplo.com', '(21) 97777-2002', '987.654.321-00', 'PESSOA_FISICA', 'ATIVO', 'Avenida Atlantica, 900', 'Rio de Janeiro', 'RJ', '22021-001', DATE '2026-03-13', 5500.00),
    ('CLI-0003', 'Fernanda Rocha', 'fernanda.rocha@exemplo.com', '(31) 96666-3003', '456.123.789-22', 'PESSOA_FISICA', 'PROSPECTO', 'Rua da Bahia, 350', 'Belo Horizonte', 'MG', '30160-011', NULL, 3500.00),
    ('CLI-0004', 'Lucas Martins', 'lucas.martins@exemplo.com', '(41) 95555-4004', '741.852.963-55', 'PESSOA_FISICA', 'ATIVO', 'Rua Marechal Deodoro, 77', 'Curitiba', 'PR', '80010-010', DATE '2026-02-08', 4500.00),
    ('CLI-0005', 'Mariana Costa', 'mariana.costa@exemplo.com', '(85) 94444-5005', '159.357.468-99', 'PESSOA_FISICA', 'ATIVO', 'Avenida Beira Mar, 1200', 'Fortaleza', 'CE', '60165-121', DATE '2026-03-18', 6000.00),
    ('CLI-0006', 'Tech Prime Solucoes Ltda', 'contato@techprime.com.br', '(11) 3333-1000', '12.345.678/0001-90', 'PESSOA_JURIDICA', 'ATIVO', 'Alameda Santos, 680', 'Sao Paulo', 'SP', '01418-100', DATE '2026-03-05', 25000.00),
    ('CLI-0007', 'Varejo Conecta SA', 'compras@varejoconecta.com.br', '(21) 3000-2020', '98.765.432/0001-10', 'PESSOA_JURIDICA', 'ATIVO', 'Rua do Ouvidor, 210', 'Rio de Janeiro', 'RJ', '20040-030', DATE '2026-01-29', 40000.00),
    ('CLI-0008', 'Inova Mobile Comercio', 'vendas@inovamobile.com.br', '(51) 3222-3030', '45.678.901/0001-23', 'PESSOA_JURIDICA', 'PROSPECTO', 'Avenida Ipiranga, 4040', 'Porto Alegre', 'RS', '90610-000', NULL, 18000.00),
    ('CLI-0009', 'Grupo Horizonte Digital', 'financeiro@horizontedigital.com.br', '(62) 3555-4040', '67.890.123/0001-45', 'PESSOA_JURIDICA', 'ATIVO', 'Rua 9, 512', 'Goiania', 'GO', '74013-010', DATE '2026-03-07', 22000.00),
    ('CLI-0010', 'Nordeste Smart Distribuicao', 'atendimento@nordestesmart.com.br', '(81) 3777-5050', '23.456.789/0001-54', 'PESSOA_JURIDICA', 'INATIVO', 'Avenida Recife, 1500', 'Recife', 'PE', '50820-001', DATE '2025-11-25', 15000.00)
ON CONFLICT (codigo_cliente) DO UPDATE
SET nome = EXCLUDED.nome,
    email = EXCLUDED.email,
    telefone = EXCLUDED.telefone,
    cpf_cnpj = EXCLUDED.cpf_cnpj,
    tipo_cliente = EXCLUDED.tipo_cliente,
    status_cliente = EXCLUDED.status_cliente,
    endereco = EXCLUDED.endereco,
    cidade = EXCLUDED.cidade,
    estado = EXCLUDED.estado,
    cep = EXCLUDED.cep,
    ultima_compra = EXCLUDED.ultima_compra,
    limite_credito = EXCLUDED.limite_credito;
