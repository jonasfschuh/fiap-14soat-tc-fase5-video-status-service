@echo off
echo ============================================
echo  Iniciando Ambiente de Desenvolvimento
echo  Adminer (UI para o PostgreSQL do K8s)
echo  Microservico: ms-video-status
echo ============================================
echo.
echo  PRE-REQUISITO: infraestrutura do iac-terraform rodando.
echo  Os recursos abaixo sao provisionados pelo repositorio:
echo    fiap-14soat-tc-fase5-iac-terraform
echo.

docker-compose up -d adminer

echo.
echo   Servicos iniciados:
echo    - Adminer:    http://localhost:8094
echo            Sistema: PostgreSQL
echo            Servidor: host.docker.internal:5434
echo            Usuario: postgres
echo            Senha: postgres
echo            Base de dados: video_status_db
echo.
echo   Recursos compartilhados (iac-terraform):
echo    - PostgreSQL: localhost:5434   (banco: video_status_db)
echo    - RabbitMQ:   localhost:5672   (vhost: fiapx / user: fiapx / pass: fiapx123)
echo    - RabbitMQ UI: http://localhost:15672
echo.
echo  Execute a aplicacao no IntelliJ com as seguintes variaveis de ambiente:
echo    SPRING_PROFILES_ACTIVE=dev
echo    SERVER_PORT=8084
echo    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/video_status_db
echo    SPRING_DATASOURCE_USERNAME=postgres
echo    SPRING_DATASOURCE_PASSWORD=postgres
echo    RABBITMQ_HOST=localhost
echo    RABBITMQ_PORT=5672
echo    RABBITMQ_VHOST=fiapx
echo    RABBITMQ_USER=fiapx
echo    RABBITMQ_PASSWORD=fiapx123
echo    AUTH_SERVICE_URL=http://localhost:8090
echo.
echo  Porta local da API: 8084
echo  http://localhost:8084/swagger-ui.html
echo  RabbitMQ UI: http://localhost:15672
echo.
