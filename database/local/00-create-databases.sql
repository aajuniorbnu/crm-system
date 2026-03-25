\set ON_ERROR_STOP on

SELECT 'CREATE DATABASE estoque_smartphones'
WHERE NOT EXISTS (
    SELECT 1 FROM pg_database WHERE datname = 'estoque_smartphones'
)\gexec

SELECT 'CREATE DATABASE clientes_crm'
WHERE NOT EXISTS (
    SELECT 1 FROM pg_database WHERE datname = 'clientes_crm'
)\gexec
