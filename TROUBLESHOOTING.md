# 🔧 Guia de Troubleshooting - Sistema Hortifruti

Soluções para problemas comuns encontrados durante o desenvolvimento e execução do sistema.

## 📋 Índice

- [Problemas com Docker](#problemas-com-docker)
- [Problemas com Banco de Dados](#problemas-com-banco-de-dados)
- [Problemas com Keycloak](#problemas-com-keycloak)
- [Problemas com Backend](#problemas-com-backend)
- [Problemas com Frontend](#problemas-com-frontend)
- [Problemas de Performance](#problemas-de-performance)
- [Erros Comuns da API](#erros-comuns-da-api)

---

## 🐳 Problemas com Docker

### Erro: "Port already in use"

**Sintoma:**
```
Error: bind: address already in use
```

**Solução:**
```bash
# Verificar qual processo está usando a porta
# No Linux/Mac:
sudo lsof -i :8080
sudo lsof -i :5432
sudo lsof -i :8180

# No Windows:
netstat -ano | findstr :8080

# Matar processo ou mudar porta no docker-compose.yml
```

---

### Erro: Containers não iniciam

**Sintoma:**
```
Container hortifruti-backend exited with code 1
```

**Diagnóstico:**
```bash
# Ver logs detalhados
docker-compose logs backend
docker-compose logs postgres
docker-compose logs keycloak

# Ver logs em tempo real
docker-compose logs -f
```

**Soluções Comuns:**
```bash
# 1. Remover volumes e recriar
docker-compose down -v
docker-compose up -d

# 2. Rebuild das imagens
docker-compose build --no-cache
docker-compose up -d

# 3. Verificar espaço em disco
docker system df
docker system prune -a  # Liberar espaço
```

---

### Erro: Network issues

**Sintoma:**
```
Error: network hortifruti-network not found
```

**Solução:**
```bash
# Recriar network
docker network rm hortifruti-network
docker network create hortifruti-network

# Ou reiniciar Docker Compose
docker-compose down
docker-compose up -d
```

---

## 🗄️ Problemas com Banco de Dados

### Erro: "Connection refused"

**Sintoma:**
```
Connection to localhost:5432 refused
```

**Diagnóstico:**
```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Verificar logs
docker logs hortifruti-postgres

# Testar conexão
docker exec -it hortifruti-postgres psql -U postgres -d hortifruti_db
```

**Soluções:**
```bash
# 1. Reiniciar container
docker-compose restart postgres

# 2. Verificar health
docker inspect hortifruti-postgres | grep -A 10 Health

# 3. Recriar database
docker-compose down -v
docker-compose up -d postgres
```

---

### Erro: "Authentication failed"

**Sintoma:**
```
FATAL: password authentication failed for user "postgres"
```

**Solução:**
```bash
# Verificar variáveis de ambiente no docker-compose.yml
POSTGRES_USER: postgres
POSTGRES_PASSWORD: postgres

# Ou no application.yml
spring:
  datasource:
    username: postgres
    password: postgres
```

---

### Erro: "Database does not exist"

**Sintoma:**
```
FATAL: database "hortifruti_db" does not exist
```

**Solução:**
```bash
# Recriar database
docker exec -it hortifruti-postgres psql -U postgres
CREATE DATABASE hortifruti_db;
\q

# Ou reiniciar com init script
docker-compose down -v
docker-compose up -d
```

---

## 🔐 Problemas com Keycloak

### Erro: Keycloak não inicia

**Sintoma:**
```
ERROR [org.keycloak] Failed to start Keycloak
```

**Diagnóstico:**
```bash
# Ver logs
docker logs hortifruti-keycloak

# Verificar porta
curl http://localhost:8180/health
```

**Soluções:**
```bash
# 1. Aumentar tempo de start
# No docker-compose.yml, aumentar start_period
start_period: 120s

# 2. Verificar memória
docker stats hortifruti-keycloak

# 3. Recriar container
docker-compose down
docker volume rm pimiv_keycloak_data
docker-compose up -d
```

---

### Erro: "Realm not found"

**Sintoma:**
```
Realm 'hortifruti' not found
```

**Solução:**
```bash
# Acessar console admin
http://localhost:8180
# Login: admin / admin

# Criar Realm manualmente:
# 1. Click "Create Realm"
# 2. Name: hortifruti
# 3. Enabled: ON
# 4. Save
```

---

### Erro: Token inválido/expirado

**Sintoma:**
```
401 Unauthorized: Token expired
```

**Solução:**
```bash
# Obter novo token
curl -X POST http://localhost:8180/realms/hortifruti/protocol/openid-connect/token \
  -d "grant_type=password" \
  -d "client_id=hortifruti-client" \
  -d "username=seu-usuario" \
  -d "password=sua-senha"

# Ou usar refresh token
curl -X POST http://localhost:8180/realms/hortifruti/protocol/openid-connect/token \
  -d "grant_type=refresh_token" \
  -d "client_id=hortifruti-client" \
  -d "refresh_token=SEU_REFRESH_TOKEN"
```

---

## ☕ Problemas com Backend

### Erro: Application fails to start

**Sintoma:**
```
APPLICATION FAILED TO START
```

**Diagnóstico:**
```bash
# Ver stacktrace completo
mvn spring-boot:run

# Ou nos logs Docker
docker logs hortifruti-backend
```

**Soluções Comuns:**

#### 1. Porta 8080 em uso
```bash
# Mudar porta no application.yml
server:
  port: 8081
```

#### 2. Dependência faltando
```bash
mvn clean install
mvn dependency:tree
```

#### 3. Problema no application.yml
```yaml
# Verificar sintaxe YAML
# Usar validador online ou:
yamllint application.yml
```

---

### Erro: LazyInitializationException

**Sintoma:**
```
org.hibernate.LazyInitializationException: could not initialize proxy
```

**Solução:**
```java
// Opção 1: Adicionar @Transactional no método
@Transactional(readOnly = true)
public EntityDTO getEntity(Integer id) {
    // ...
}

// Opção 2: Usar JOIN FETCH na query
@Query("SELECT e FROM Entity e JOIN FETCH e.relation WHERE e.id = :id")
Entity findByIdWithRelation(Integer id);

// Opção 3: Eager loading (não recomendado)
@ManyToOne(fetch = FetchType.EAGER)
private Relation relation;
```

---

### Erro: ConstraintViolationException

**Sintoma:**
```
ConstraintViolationException: duplicate key value violates unique constraint
```

**Solução:**
```bash
# Verificar dados duplicados
docker exec -it hortifruti-postgres psql -U postgres -d hortifruti_db
SELECT * FROM tabela WHERE campo = 'valor';

# Limpar dados duplicados
DELETE FROM tabela WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY campo ORDER BY id) as rn
        FROM tabela
    ) t WHERE t.rn > 1
);
```

---

### Erro: Out of Memory

**Sintoma:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solução:**
```bash
# Aumentar memória no Dockerfile
ENV JAVA_OPTS="-Xmx1024m -Xms512m"

# Ou via docker-compose
environment:
  JAVA_OPTS: "-Xmx1024m -Xms512m"

# Ou no maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m"
```

---

## 🎨 Problemas com Frontend

### Erro: "Cannot find module"

**Sintoma:**
```
Cannot find module '@angular/core'
```

**Solução:**
```bash
# Reinstalar dependências
rm -rf node_modules package-lock.json
npm install

# Ou limpar cache
npm cache clean --force
npm install
```

---

### Erro: CORS blocked

**Sintoma:**
```
Access to XMLHttpRequest blocked by CORS policy
```

**Solução:**
```java
// Backend - CorsConfig.java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

### Erro: Keycloak not initialized

**Sintoma:**
```
KeycloakService not initialized
```

**Solução:**
```typescript
// app.config.ts ou main.ts
import { APP_INITIALIZER } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

export function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8180',
        realm: 'hortifruti',
        clientId: 'hortifruti-client'
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html'
      }
    });
}

// No providers
{
  provide: APP_INITIALIZER,
  useFactory: initializeKeycloak,
  multi: true,
  deps: [KeycloakService]
}
```

---

## ⚡ Problemas de Performance

### Consultas lentas

**Diagnóstico:**
```sql
-- Ativar log de queries lentas
ALTER DATABASE hortifruti_db SET log_min_duration_statement = 1000;

-- Ver queries em execução
SELECT pid, now() - query_start as duration, query 
FROM pg_stat_activity 
WHERE state = 'active' 
ORDER BY duration DESC;

-- Analisar query específica
EXPLAIN ANALYZE SELECT * FROM produto WHERE nome LIKE '%maçã%';
```

**Soluções:**
```sql
-- Criar índices
CREATE INDEX idx_produto_nome_gin ON produto USING gin(nome gin_trgm_ops);

-- Ou índice normal
CREATE INDEX idx_produto_nome ON produto(nome);

-- Vacuum e analyze
VACUUM ANALYZE produto;
```

---

### N+1 Queries

**Identificar:**
```properties
# application.yml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

**Solução:**
```java
// Usar JOIN FETCH
@Query("SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.id = :id")
Produto findByIdWithCategoria(Integer id);

// Ou EntityGraph
@EntityGraph(attributePaths = {"categoria"})
@Query("SELECT p FROM Produto p WHERE p.id = :id")
Produto findByIdWithCategoriaGraph(Integer id);

// Ou batch fetch
@BatchSize(size = 10)
@OneToMany(mappedBy = "produto")
private List<ItemPedido> itens;
```

---

### Timeout de conexão

**Sintoma:**
```
Connection timeout after 30000ms
```

**Solução:**
```yaml
# application.yml
spring:
  datasource:
    hikari:
      connection-timeout: 60000
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1800000
```

---

## 🚨 Erros Comuns da API

### 400 Bad Request

**Possíveis causas:**
1. JSON malformado
2. Validação falhou
3. Parâmetros obrigatórios faltando
4. Tipo de dados incorreto

**Debug:**
```bash
# Ver detalhes do erro
curl -v http://localhost:8080/v1/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome": "Teste"}'
```

---

### 401 Unauthorized

**Possíveis causas:**
1. Token ausente
2. Token expirado
3. Token inválido

**Solução:**
```bash
# Verificar token
echo "SEU_TOKEN" | base64 -d

# Obter novo token
curl -X POST http://localhost:8180/realms/hortifruti/protocol/openid-connect/token \
  -d "grant_type=password" \
  -d "client_id=hortifruti-client" \
  -d "username=usuario" \
  -d "password=senha"
```

---

### 403 Forbidden

**Possíveis causas:**
1. Role insuficiente
2. Permissão negada

**Solução:**
```bash
# Verificar roles no token
# Decodificar JWT em https://jwt.io

# Adicionar role no Keycloak
# Admin Console → Users → User → Role Mappings
```

---

### 404 Not Found

**Possíveis causas:**
1. Recurso não existe
2. ID incorreto
3. Endpoint incorreto

**Debug:**
```bash
# Verificar endpoints disponíveis
curl http://localhost:8080/actuator/mappings | jq .
```

---

### 409 Conflict

**Possíveis causas:**
1. CPF/CNPJ/Email duplicado
2. Constraint de unicidade violada

**Solução:**
```bash
# Verificar registro existente
curl http://localhost:8080/v1/clientes/email/joao@email.com \
  -H "Authorization: Bearer TOKEN"

# Usar outro valor único ou atualizar registro existente
```

---

### 500 Internal Server Error

**Debug:**
```bash
# Ver stacktrace completo nos logs
docker logs hortifruti-backend

# Ou verificar actuator
curl http://localhost:8080/actuator/health
```

**Ações:**
1. Verificar logs do backend
2. Verificar logs do banco
3. Verificar conectividade
4. Reiniciar serviços se necessário

---

## 🔍 Ferramentas de Debug

### Docker
```bash
# Entrar no container
docker exec -it hortifruti-backend sh

# Ver variáveis de ambiente
docker exec hortifruti-backend env

# Ver processos
docker top hortifruti-backend

# Stats em tempo real
docker stats
```

### PostgreSQL
```bash
# Conectar ao banco
docker exec -it hortifruti-postgres psql -U postgres -d hortifruti_db

# Comandos úteis:
\dt          # Listar tabelas
\d tabela    # Descrever tabela
\du          # Listar usuários
\l           # Listar databases
\q           # Sair
```

### Backend
```bash
# Endpoints de monitoramento (Actuator)
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/loggers
```

---

## 📞 Suporte Adicional

Se o problema persistir:

1. **Verificar logs completos:**
   ```bash
   docker-compose logs > logs.txt
   ```

2. **Coletar informações do sistema:**
   ```bash
   docker version
   docker-compose version
   java -version
   node --version
   ```

3. **Abrir issue no GitHub** com:
   - Descrição do problema
   - Passos para reproduzir
   - Logs relevantes
   - Informações do ambiente

---

**Última atualização:** Outubro 2025
