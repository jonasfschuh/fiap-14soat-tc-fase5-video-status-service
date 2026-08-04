@echo off
setlocal ENABLEDELAYEDEXPANSION

REM ==============================
REM Script: Cobertura + SonarCloud
REM ==============================

REM Defina a variavel de ambiente SONAR_TOKEN antes de executar este script.
if "%SONAR_TOKEN%"=="" (
  echo ERRO: variavel de ambiente SONAR_TOKEN nao definida.
  echo Defina com: set SONAR_TOKEN=seu_token_aqui
  pause
  exit /b 1
)

REM Executa testes com cobertura usando o Maven Wrapper
call .\mvnw.cmd clean verify -Pcoverage
if errorlevel 1 (
  echo ERRO: Falha ao executar cobertura (mvn clean verify -Pcoverage).
  pause
  exit /b 1
)

REM Executa análise no SonarCloud usando o token informado
call .\mvnw.cmd sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=%SONAR_TOKEN%
if errorlevel 1 (
  echo ERRO: Falha ao executar analise no SonarCloud.
  pause
  exit /b 1
)

echo Processo concluido: cobertura gerada e analise enviada ao SonarCloud.
pause

endlocal
