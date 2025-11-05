@echo off
echo ================================================
echo SISTEMA HORTIFRUTI - INICIAR CONTAINERS
echo ================================================
echo.

echo [1/3] Parando containers anteriores...
docker-compose down
echo.

echo [2/3] Subindo containers...
docker-compose up --build
echo.

echo [3/3] Aguardando inicializacao (60 segundos)...
timeout /t 60 /nobreak
echo.

echo ================================================
echo STATUS DOS CONTAINERS
echo ================================================
docker-compose ps
echo.

echo ================================================
echo PROXIMOS PASSOS
echo ================================================
echo.
echo 1. Verifique se todos os containers estao UP
echo 2. Aguarde mais 30 segundos para o backend inicializar
echo 3. Verifique as tabelas: docker exec -it hortifruti-postgres psql -U hortifruti_user -d hortifruti_db -c "\dt"
echo 4. Insira dados iniciais: docker exec -i hortifruti-postgres psql -U hortifruti_user -d hortifruti_db ^< ./init-scripts/02-dados-iniciais.sql
echo 5. Configure Keycloak: http://localhost:8180
echo.
echo Para ver logs: docker-compose logs -f
echo.
pause
