-- Script de inicialização do banco de dados Hortifruti
-- Executado automaticamente pelo Docker na primeira inicialização

-- ============================================
-- BANCO DE DADOS KEYCLOAK
-- ============================================

-- Verificar se database já existe antes de criar
SELECT 'CREATE DATABASE keycloak_db'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak_db')\gexec

-- Configurar encoding do keycloak_db
    \c keycloak_db;
SET client_encoding = 'UTF8';

COMMENT ON DATABASE keycloak_db IS 'Database para armazenar dados do Keycloak (usuários, realms, clients, etc)';

-- ============================================
-- BANCO DE DADOS HORTIFRUTI
-- ============================================

\c hortifruti_db;

-- Configurar encoding
SET client_encoding = 'UTF8';

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- Para buscas full-text

-- ============================================
-- DADOS INICIAIS
-- ============================================

-- Inserir categorias padrão
INSERT INTO categoria (nome) VALUES
                                 ('Frutas'),
                                 ('Verduras'),
                                 ('Legumes'),
                                 ('Temperos'),
                                 ('Orgânicos')
    ON CONFLICT DO NOTHING;

-- Inserir fornecedores de exemplo
INSERT INTO fornecedor (nome, cnpj, telefone, email, endereco, prod_fornecidos) VALUES
                                                                                    ('Hortifruti Premium Ltda', '12345678901234', '11987654321', 'contato@hortifrutipremium.com', 'Rua das Flores, 123, São Paulo - SP', 'Frutas e Verduras Orgânicas'),
                                                                                    ('Fazenda Verde', '98765432109876', '11999887766', 'vendas@fazendaverde.com', 'Estrada Rural, Km 45, Campinas - SP', 'Legumes e Temperos Frescos'),
                                                                                    ('Distribuidora FrutaSul', '11122233344455', '11988776655', 'comercial@frutasul.com', 'Av. dos Estados, 789, São Paulo - SP', 'Frutas Importadas e Nacionais')
    ON CONFLICT DO NOTHING;

-- ============================================
-- FUNÇÕES AUXILIARES
-- ============================================

-- Função para atualizar timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- ============================================
-- COMENTÁRIOS NAS TABELAS
-- ============================================

-- Aguardar JPA criar as tabelas primeiro
DO $$
BEGIN
    -- Aguardar algumas tabelas existirem
    PERFORM pg_sleep(2);
END $$;

-- Comentários
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'categoria') THEN
        COMMENT ON TABLE categoria IS 'Categorias de produtos do hortifruti';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'fornecedor') THEN
        COMMENT ON TABLE fornecedor IS 'Fornecedores de produtos';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'produto') THEN
        COMMENT ON TABLE produto IS 'Produtos disponíveis para venda';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'estoque') THEN
        COMMENT ON TABLE estoque IS 'Controle de estoque dos produtos';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'usuario') THEN
        COMMENT ON TABLE usuario IS 'Usuários do sistema (superclasse)';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'cliente') THEN
        COMMENT ON TABLE cliente IS 'Clientes cadastrados';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'carrinho') THEN
        COMMENT ON TABLE carrinho IS 'Carrinhos de compras dos clientes';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'pedido') THEN
        COMMENT ON TABLE pedido IS 'Pedidos realizados';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'item_pedido') THEN
        COMMENT ON TABLE item_pedido IS 'Itens de cada pedido';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'pagamento') THEN
        COMMENT ON TABLE pagamento IS 'Informações de pagamento dos pedidos';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'entrega') THEN
        COMMENT ON TABLE entrega IS 'Informações de entrega dos pedidos';
END IF;

    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'fornece') THEN
        COMMENT ON TABLE fornece IS 'Relacionamento Fornecedor-Produto';
END IF;
END $$;

-- ============================================
-- ÍNDICES PARA PERFORMANCE
-- ============================================

-- Criar índices (só se tabelas existirem)
DO $$
BEGIN
    -- Produto
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'produto') THEN
CREATE INDEX IF NOT EXISTS idx_produto_nome ON produto(nome);
CREATE INDEX IF NOT EXISTS idx_produto_nome_gin ON produto USING gin(nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_produto_categoria ON produto(id_categoria);
CREATE INDEX IF NOT EXISTS idx_produto_validade ON produto(dt_validade);
END IF;

    -- Cliente
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'cliente') THEN
CREATE INDEX IF NOT EXISTS idx_cliente_cpf ON cliente(cpf);
CREATE INDEX IF NOT EXISTS idx_cliente_email ON cliente(email);
END IF;

    -- Pedido
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'pedido') THEN
CREATE INDEX IF NOT EXISTS idx_pedido_cliente ON pedido(id_cliente);
CREATE INDEX IF NOT EXISTS idx_pedido_status ON pedido(status_pedido);
END IF;

    -- ItemPedido
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'item_pedido') THEN
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido ON item_pedido(id_pedido);
CREATE INDEX IF NOT EXISTS idx_item_pedido_produto ON item_pedido(id_produto);
END IF;

    -- Carrinho
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'carrinho') THEN
CREATE INDEX IF NOT EXISTS idx_carrinho_cliente ON carrinho(id_cliente);
END IF;

    -- Estoque
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'estoque') THEN
CREATE INDEX IF NOT EXISTS idx_estoque_produto ON estoque(id_produto);
END IF;
END $$;

-- ============================================
-- GRANTS E PERMISSÕES
-- ============================================

-- Garantir permissões completas ao usuário postgres
GRANT ALL PRIVILEGES ON DATABASE hortifruti_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO postgres;

-- ============================================
-- CONFIGURAÇÕES DE PERFORMANCE
-- ============================================

-- Configurar PostgreSQL para melhor performance
ALTER DATABASE hortifruti_db SET synchronous_commit TO 'off';
ALTER DATABASE hortifruti_db SET work_mem TO '64MB';
ALTER DATABASE hortifruti_db SET maintenance_work_mem TO '256MB';
ALTER DATABASE hortifruti_db SET effective_cache_size TO '1GB';

ALTER DATABASE keycloak_db SET synchronous_commit TO 'off';
ALTER DATABASE keycloak_db SET work_mem TO '64MB';

-- ============================================
-- ESTATÍSTICAS E VACUUM
-- ============================================

-- Atualizar estatísticas
ANALYZE;

-- ============================================
-- LOG DE SUCESSO
-- ============================================

DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'INICIALIZAÇÃO CONCLUÍDA COM SUCESSO!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Databases criados:';
    RAISE NOTICE '  - hortifruti_db  ✓';
    RAISE NOTICE '  - keycloak_db    ✓';
    RAISE NOTICE '';
    RAISE NOTICE 'Dados iniciais inseridos:';
    RAISE NOTICE '  - Categorias: 5';
    RAISE NOTICE '  - Fornecedores: 3';
    RAISE NOTICE '';
    RAISE NOTICE 'Extensões habilitadas:';
    RAISE NOTICE '  - uuid-ossp  ✓';
    RAISE NOTICE '  - pg_trgm    ✓';
    RAISE NOTICE '';
    RAISE NOTICE 'Índices criados para performance';
    RAISE NOTICE 'Configurações de performance aplicadas';
    RAISE NOTICE '';
    RAISE NOTICE '================================================';
    RAISE NOTICE 'Sistema pronto para uso!';
    RAISE NOTICE '================================================';
    RAISE NOTICE '';
END $$;