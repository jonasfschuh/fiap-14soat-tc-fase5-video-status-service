@echo off
echo ============================================
echo  Iniciando Ambiente Completo (Container)
echo  App + PostgreSQL + Adminer
echo  Microservico: ms-video-status
echo ============================================

docker-compose up --build -d

echo.
echo    Servicos iniciados:
echo    - API:        http://localhost:8085
echo    - Swagger:    http://localhost:8085/swagger-ui.html
echo    - Actuator:   http://localhost:8085/actuator
echo                  http://localhost:8085/actuator/health
echo                  http://localhost:8085/actuator/health/liveness
echo                  http://localhost:8085/actuator/health/readiness
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
echo    - Localstack (compartilhado com video-upload-service): http://localhost:4566
echo.

