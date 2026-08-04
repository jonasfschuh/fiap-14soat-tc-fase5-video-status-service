.gitignore@echo off
echo ============================================
echo  Iniciando Ambiente de Desenvolvimento
echo  PostgreSQL + Adminer
echo  Microservico: ms-video-status
echo ============================================
echo.

docker-compose up -d postgres-video-status adminer

echo.
echo   Servicos iniciados:
echo    - PostgreSQL: localhost:5434
echo            Host: localhost:5434
echo            Database: video_status_db
echo            Username: postgres
echo            Password: postgres
echo    - Adminer:    http://localhost:8094
echo            Sistema: PostgreSQL
echo            Servidor: postgres-video-status (ou localhost se acessar de fora)
echo            Usuario: postgres
echo            Senha: postgres
echo            Base de dados: video_status_db
echo.
echo  Para consumir as filas SQS (video-uploaded / video-events), suba o
echo  LocalStack compartilhado do video-upload-service (fiap-localstack).
echo.
echo  Execute a aplicacao no IntelliJ com as seguintes variaveis de ambiente:
echo    AWS_SQS_ENABLED=true
echo    AWS_SQS_ENDPOINT=http://localhost:4566
echo    SPRING_PROFILES_ACTIVE=dev
echo.
echo  Porta local da API: 8085
echo  http://localhost:8085/swagger-ui.html
echo.
