\set ON_ERROR_STOP on
\connect estoque_smartphones

CREATE TABLE IF NOT EXISTS smartphones_estoque (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(40) NOT NULL UNIQUE,
    marca VARCHAR(30) NOT NULL,
    linha VARCHAR(40) NOT NULL,
    modelo VARCHAR(120) NOT NULL,
    armazenamento VARCHAR(20) NOT NULL,
    cor VARCHAR(40) NOT NULL,
    preco_mercado NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    estoque_minimo INTEGER NOT NULL,
    data_referencia_preco DATE NOT NULL,
    fonte_preco VARCHAR(120) NOT NULL,
    observacao TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO smartphones_estoque
    (sku, marca, linha, modelo, armazenamento, cor, preco_mercado, quantidade_estoque, estoque_minimo, data_referencia_preco, fonte_preco, observacao)
VALUES
    ('APL-17E-128-BLK', 'Apple', 'iPhone 17e', 'iPhone 17e', '128GB', 'Preto', 5799.00, 18, 5, DATE '2026-03-25', 'Apple Brasil', 'Preco baseado na pagina Comprar iPhone da Apple Brasil em 25/03/2026.'),
    ('APL-16-128-BLK', 'Apple', 'iPhone 16', 'iPhone 16', '128GB', 'Preto', 6799.00, 14, 5, DATE '2026-03-25', 'Apple Brasil', 'Preco baseado na pagina Comprar iPhone da Apple Brasil em 25/03/2026.'),
    ('APL-17-256-BLK', 'Apple', 'iPhone 17', 'iPhone 17', '256GB', 'Preto', 7999.00, 12, 4, DATE '2026-03-25', 'Apple Brasil', 'Preco baseado na pagina Comprar iPhone da Apple Brasil em 25/03/2026.'),
    ('APL-AIR-128-BLU', 'Apple', 'iPhone Air', 'iPhone Air', '128GB', 'Azul', 10499.00, 8, 3, DATE '2026-03-25', 'Apple Brasil', 'Preco baseado na pagina Comprar iPhone da Apple Brasil em 25/03/2026.'),
    ('APL-17PRO-256-ORN', 'Apple', 'iPhone 17 Pro', 'iPhone 17 Pro', '256GB', 'Laranja-cosmico', 12499.00, 6, 2, DATE '2026-03-25', 'Apple Brasil', 'Preco baseado na pagina Comprar iPhone 17 Pro da Apple Brasil em 25/03/2026.'),
    ('SMS-A26-256-BLK', 'Samsung', 'Galaxy A', 'Galaxy A26 5G', '256GB', 'Preto', 2299.00, 25, 8, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-A36-128-VIO', 'Samsung', 'Galaxy A', 'Galaxy A36 5G', '128GB', 'Violeta', 2699.00, 22, 8, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-A56-128-GRY', 'Samsung', 'Galaxy A', 'Galaxy A56 5G', '128GB', 'Cinza', 2999.00, 20, 8, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-S25-128-NVY', 'Samsung', 'Galaxy S', 'Galaxy S25', '128GB', 'Azul Marinho', 6999.00, 10, 4, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-S25P-256-SLV', 'Samsung', 'Galaxy S', 'Galaxy S25+', '256GB', 'Prata', 8499.00, 9, 3, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-S25U-256-TBK', 'Samsung', 'Galaxy S', 'Galaxy S25 Ultra', '256GB', 'Titanio Preto', 11999.00, 7, 2, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-ZFLIP6-256-BLU', 'Samsung', 'Galaxy Z', 'Galaxy Z Flip6', '256GB', 'Azul', 7999.00, 5, 2, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.'),
    ('SMS-ZFOLD6-512-GRY', 'Samsung', 'Galaxy Z', 'Galaxy Z Fold6', '512GB', 'Cinza', 13799.00, 4, 1, DATE '2026-03-25', 'Samsung Brasil', 'Preco sugerido divulgado pela Samsung para o mercado brasileiro.')
ON CONFLICT (sku) DO UPDATE
SET marca = EXCLUDED.marca,
    linha = EXCLUDED.linha,
    modelo = EXCLUDED.modelo,
    armazenamento = EXCLUDED.armazenamento,
    cor = EXCLUDED.cor,
    preco_mercado = EXCLUDED.preco_mercado,
    quantidade_estoque = EXCLUDED.quantidade_estoque,
    estoque_minimo = EXCLUDED.estoque_minimo,
    data_referencia_preco = EXCLUDED.data_referencia_preco,
    fonte_preco = EXCLUDED.fonte_preco,
    observacao = EXCLUDED.observacao;
