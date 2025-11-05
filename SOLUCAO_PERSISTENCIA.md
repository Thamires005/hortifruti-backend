# GUIA RÁPIDO - SOLUÇÃO DE PROBLEMAS DE PERSISTÊNCIA

## CORREÇÕES APLICADAS

### 1. application.yml - CORRIGIDO
**Problema:** Campo `n:` ao invés de `name:`
**Solução:** Corrigido para `spring.application.name: meu-hortifruti`
**Adicionado:** Configurações de conexão pool e timezone

### 2. docker-compose.yml - CORRIGIDO
**Problema:** Backend não carregava .env e Keycloak em modo dev
**Soluções aplicadas:**
- Adicionado `env_file: .env` no serviço backend
- Keycloak mudado de `start-dev` para `start --optimized`
- Adicionado volume `keycloak_data` para persistir configurações
- Adicionado volume `pgadmin_data` para persistir configurações do PgAdmin
- PostgreSQL do Keycloak exposto na porta 5433 para não conflitar

### 3. Keycloak - MODO PRODUÇÃO
**Mudanças:**
- Comando: `start --optimized --http-enabled=true`
- Desabilitado XA transactions para melhor performance
- Configurado proxy edge mode
- Volume dedicado para dados

## COMO VERIFICAR SE ESTÁ FUNCIONANDO

### 1. Verificar se os volumes foram criados
```bash
docker volume ls | grep hortifruti
```

Devem aparecer:
- hortifruti-postgres-data
- hortifruti-postgres-keycloak-data
- hortifruti-keycloak-data
- hortifruti-pgadmin-data

### 2. Testar persistência do PostgreSQL da aplicação
```bash
# Conectar ao banco
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db

# Criar uma tabela de teste
CREATE TABLE teste_persistencia (id SERIAL PRIMARY KEY, dados TEXT);
INSERT INTO teste_persistencia (dados) VALUES ('teste');
SELECT * FROM teste_persistencia;
\q

# Reiniciar container
docker-compose restart postgres

# Conectar novamente e verificar se a tabela ainda existe
docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db
SELECT * FROM teste_persistencia;
\q
```

### 3. Testar persistência do Keycloak
```bash
# Acesse http://localhost:8180
# Login: admin / admin
# Crie um realm de teste
# Pare os containers: docker-compose down
# Suba novamente: docker-compose up -d
# Acesse novamente e verifique se o realm ainda existe
```

## SE OS DADOS AINDA NÃO PERSISTEM

### Cenário 1: Volumes não foram criados
```bash
# Parar tudo
docker-compose down

# Remover containers antigos
docker-compose rm -f

# Verificar e remover volumes órfãos
docker volume ls
docker volume prune

# Subir novamente
docker-compose up -d
```

### Cenário 2: Docker usando volumes anônimos
```bash
# Verificar volumes anônimos
docker volume ls | grep -v NAME | grep -v hortifruti

# Se houver volumes sem nome, algo está errado
# Parar tudo
docker-compose down -v

# Editar docker-compose.yml e garantir que os volumes estão nomeados corretamente
# Subir novamente
docker-compose up -d
```

### Cenário 3: Permissões de arquivo no Windows
```bash
# No PowerShell como Administrador
docker-compose down
docker volume rm hortifruti-postgres-data hortifruti-postgres-keycloak-data hortifruti-keycloak-data
docker-compose up -d
```

### Cenário 4: Backend não persiste no PostgreSQL
Verifique o arquivo application.yml:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # NÃO pode ser "create-drop"
```

Se estiver `create-drop`, mude para `update` e reconstrua:
```bash
docker-compose up -d --build backend
```

## COMANDOS PARA INVESTIGAÇÃO

### Ver configuração dos volumes
```bash
docker volume inspect hortifruti-postgres-data
docker volume inspect hortifruti-keycloak-data
```

### Ver onde os volumes estão no disco
```bash
# Windows
docker volume inspect hortifruti-postgres-data | findstr Mountpoint

# Linux/Mac
docker volume inspect hortifruti-postgres-data | grep Mountpoint
```

### Ver se há dados nos volumes
```bash
# Windows PowerShell
docker run --rm -v hortifruti-postgres-data:/data alpine ls -la /data

# Ver tamanho do volume
docker run --rm -v hortifruti-postgres-data:/data alpine du -sh /data
```

### Backup manual de um volume
```bash
# Criar backup
docker run --rm -v hortifruti-postgres-data:/data -v ${PWD}:/backup alpine tar czf /backup/backup-postgres.tar.gz /data

# Restaurar backup
docker run --rm -v hortifruti-postgres-data:/data -v ${PWD}:/backup alpine tar xzf /backup/backup-postgres.tar.gz
```

## CHECKLIST DE VERIFICAÇÃO

- [ ] Arquivo .env existe e está preenchido
- [ ] Backend tem `env_file: .env` no docker-compose.yml
- [ ] Todos os volumes têm `name:` definido no docker-compose.yml
- [ ] application.yml tem `ddl-auto: update` (não create-drop)
- [ ] application.yml tem `name:` (não `n:`)
- [ ] Keycloak usa comando `start` (não `start-dev`)
- [ ] Keycloak tem volume `keycloak_data` configurado
- [ ] Ao executar `docker volume ls`, aparecem 4 volumes nomeados

## EM CASO DE DÚVIDA

1. Pare tudo: `docker-compose down`
2. NÃO use `-v` (não apague os volumes)
3. Verifique os arquivos corrigidos
4. Suba novamente: `docker-compose up -d`
5. Aguarde 2 minutos
6. Verifique logs: `docker-compose logs -f`

## RECONSTRUIR DO ZERO (ÚLTIMO RECURSO)

```bash
# ATENÇÃO: Isso apaga todos os dados
docker-compose down -v
docker system prune -a --volumes
docker-compose up -d --build
```

Após reconstruir, configure o Keycloak seguindo o arquivo CONFIGURACAO_KEYCLOAK.md
