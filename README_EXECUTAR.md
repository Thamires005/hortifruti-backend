# GUIA DE EXECUÇÃO - SISTEMA HORTIFRUTI

## CORREÇÕES APLICADAS

Todos os problemas de persistência foram corrigidos:

1. Configuração do Keycloak ajustada para modo desenvolvimento com PostgreSQL
2. Script de dados iniciais movido para execução manual após criação das tabelas
3. Volumes Docker configurados corretamente para persistência
4. Backend configurado para carregar variáveis do .env

## PASSO A PASSO PARA EXECUTAR

### 1. Parar containers anteriores (se existirem)
```bash
docker-compose down
```

### 2. Limpar volumes antigos (OPCIONAL - só se quiser começar do zero)
```bash
docker-compose down -v
docker volume prune
```

### 3. Subir os containers
```bash
docker-compose up -d --build
```

### 4. Acompanhar os logs
```bash
docker-compose logs -f
```

Aguarde até ver:
- `hortifruti-postgres | database system is ready to accept connections`
- `hortifruti-postgres-keycloak | database system is ready to accept connections`
- `hortifruti-keycloak | Listening on: http://0.0.0.0:8080`
- `hortifruti-backend | Started MeuHortifrutiApplication`

Isso pode levar de 1 a 2 minutos.

### 5. Verificar status dos containers
```bash
docker-compose ps
```

Todos devem estar com status "Up" ou "healthy".

### 6. Verificar se o backend criou as tabelas
```bash
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db -c "\dt"
```

Você deve ver as tabelas criadas pelo JPA.

### 7. Inserir dados iniciais
```bash
docker exec -i hortifruti-postgres psql -U hortifruti_user -d hortifruti_db < 02-dados-iniciais.sql
```

### 8. Configurar Keycloak

Acesse http://localhost:8180 e siga as instruções em `CONFIGURACAO_KEYCLOAK.md`

Resumo rápido:
1. Login: admin / admin
2. Criar realm: hortifruti-realm
3. Criar client: hortifruti-backend
4. Copiar client secret
5. Atualizar KEYCLOAK_CLIENT_SECRET no .env
6. Reconstruir backend: `docker-compose up -d --build backend`
7. Criar roles: ROLE_ADMIN e ROLE_CLIENTE
8. Criar usuários de teste

## VERIFICAR SE ESTÁ FUNCIONANDO

### PostgreSQL da Aplicação
```bash
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db -c "SELECT * FROM categoria;"
```

Deve retornar as categorias inseridas.

### PostgreSQL do Keycloak
```bash
docker exec -it hortifruti-postgres-keycloak psql -U keycloak_user -d keycloak_db -c "\dt"
```

Deve mostrar várias tabelas do Keycloak.

### Backend API
```bash
curl http://localhost:8080/actuator/health
```

Deve retornar: `{"status":"UP"}`

### Keycloak
Acesse: http://localhost:8180

## TESTAR PERSISTÊNCIA

### Teste 1: Reiniciar apenas um serviço
```bash
docker-compose restart postgres
docker-compose restart keycloak
```

Dados devem ser preservados.

### Teste 2: Parar e subir novamente
```bash
docker-compose down
docker-compose up -d
```

Todas as configurações devem ser preservadas.

## COMANDOS ÚTEIS

### Ver logs de um serviço específico
```bash
docker-compose logs -f postgres
docker-compose logs -f keycloak
docker-compose logs -f backend
```

### Acessar o shell de um container
```bash
docker exec -it hortifruti-postgres sh
docker exec -it hortifruti-keycloak bash
docker exec -it hortifruti-backend sh
```

### Verificar volumes
```bash
docker volume ls | grep hortifruti
docker volume inspect hortifruti-postgres-data
```

### Limpar tudo e começar do zero
```bash
docker-compose down -v
docker volume rm hortifruti-postgres-data hortifruti-postgres-keycloak-data hortifruti-keycloak-data hortifruti-pgadmin-data
docker-compose up -d --build
```

## SOLUÇÃO DE PROBLEMAS

### Problema: Backend não conecta no Keycloak
**Solução:** Verifique se o client secret está correto no .env e reconstrua o backend.

### Problema: Tabela não existe ao inserir dados
**Solução:** Aguarde o backend terminar de iniciar. Verifique com `\dt` se as tabelas existem.

### Problema: Keycloak não persiste configurações
**Solução:** Verifique se o volume keycloak_data foi criado: `docker volume ls`

### Problema: Erro "relation does not exist"
**Solução:** As tabelas ainda não foram criadas. Aguarde o backend iniciar completamente.

## PORTAS UTILIZADAS

| Serviço | Porta | URL |
|---------|-------|-----|
| Backend | 8080 | http://localhost:8080 |
| Keycloak | 8180 | http://localhost:8180 |
| PostgreSQL App | 5432 | localhost:5432 |
| PostgreSQL Keycloak | 5433 | localhost:5433 |
| PgAdmin | 5050 | http://localhost:5050 |

## PRÓXIMOS PASSOS

Após tudo funcionando:

1. Desenvolver controllers restantes (Produto, Fornecedor, etc)
2. Implementar autenticação completa no frontend
3. Criar testes automatizados
4. Configurar CI/CD

## OBSERVAÇÕES IMPORTANTES

- O modo `start-dev` do Keycloak é adequado para desenvolvimento
- Para produção, use `start --optimized` com build customizado
- Sempre use `docker-compose down` SEM `-v` para preservar dados
- Os volumes são nomeados e persistem entre reinicializações
- O script de dados iniciais deve ser executado APÓS o backend criar as tabelas
