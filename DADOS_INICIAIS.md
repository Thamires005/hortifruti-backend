# SCRIPT DE DADOS INICIAIS

## ATENÇÃO: Execute este script SOMENTE após o backend iniciar pela primeira vez

O Spring Boot criará as tabelas automaticamente quando iniciar. Após a criação das tabelas, você pode inserir os dados iniciais.

## Como executar

### Opção 1: Via Docker (Recomendado)
```bash
docker exec -i hortifruti-postgres psql -U hortifruti_user -d hortifruti_db < 02-dados-iniciais.sql
```

### Opção 2: Via PgAdmin
1. Acesse http://localhost:5050
2. Login: admin@hortifruti.com / admin
3. Adicione o servidor:
   - Host: postgres (ou localhost se fora do Docker)
   - Port: 5432
   - Database: hortifruti_db
   - Username: hortifruti_user
   - Password: hortifruti_pass
4. Abra Query Tool
5. Cole o conteúdo do arquivo 02-dados-iniciais.sql
6. Execute

### Opção 3: Via linha de comando (se PostgreSQL instalado localmente)
```bash
psql -h localhost -p 5432 -U hortifruti_user -d hortifruti_db -f 02-dados-iniciais.sql
```

## Verificar se as tabelas foram criadas

Antes de executar o script de dados iniciais, verifique se as tabelas existem:

```bash
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db -c "\dt"
```

Você deve ver as seguintes tabelas:
- categoria
- cliente
- usuario
- produto
- estoque
- fornecedor
- carrinho
- pedido
- item_pedido
- pagamento
- entrega
- fornece

## Ordem de execução

1. Subir os containers: `docker-compose up -d`
2. Aguardar backend iniciar (cerca de 1 minuto)
3. Verificar se as tabelas foram criadas
4. Executar o script de dados iniciais

## Troubleshooting

### Erro: relation "categoria" does not exist
Isso significa que o backend ainda não criou as tabelas. Aguarde mais alguns segundos e tente novamente.

### Erro: duplicate key value violates unique constraint
Os dados já foram inseridos. Não é necessário executar novamente.
