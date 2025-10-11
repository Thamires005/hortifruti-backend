-- Script de inicialização do banco de dados hortifruti_db
-- Executado automaticamente na criação do container PostgreSQL

-- Configurar encoding e locale
SET client_encoding = 'UTF8';

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Criar schemas para organização lógica
CREATE SCHEMA IF NOT EXISTS public;
CREATE SCHEMA IF NOT EXISTS auditoria;

-- Comentários dos schemas
COMMENT ON SCHEMA public IS 'Schema principal com tabelas do sistema';
COMMENT ON SCHEMA auditoria IS 'Schema para tabelas de auditoria e logs';

-- Configurar search_path padrão
ALTER DATABASE hortifruti_db SET search_path TO public, auditoria;

-- Criar tabela de auditoria genérica
CREATE TABLE IF NOT EXISTS auditoria.log_alteracoes (
    id_log SERIAL PRIMARY KEY,
    tabela VARCHAR(100) NOT NULL,
    operacao VARCHAR(10) NOT NULL,
    usuario VARCHAR(100),
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dados_antigos JSONB,
    dados_novos JSONB
);

COMMENT ON TABLE auditoria.log_alteracoes IS 'Tabela para registro de alterações no sistema';

-- Grant de permissões
GRANT ALL PRIVILEGES ON SCHEMA public TO hortifruti_user;
GRANT ALL PRIVILEGES ON SCHEMA auditoria TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auditoria TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hortifruti_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auditoria TO hortifruti_user;

-- Mensagem de conclusão
DO $$
BEGIN
    RAISE NOTICE 'Banco de dados hortifruti_db inicializado com sucesso!';
END $$;
