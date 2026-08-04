@echo off
setlocal ENABLEDELAYEDEXPANSION

echo Iniciando processo de cobertura de testes com Cucumber
echo .\mvnw.cmd test -pl application -Dtest=CucumberRunner
echo Para ver o relatorio HTML gerado apos a execucao, abra: application\target\cucumber-reports\report.html


REM Executa testes com cobertura usando o Maven Wrapper
call .\mvnw.cmd test -pl application -Dtest=CucumberRunner
if errorlevel 1 (
  echo ERRO: Falha ao executar cobertura (mvn test -pl application -Dtest=CucumberRunner).
  pause
  exit /b 1
)

echo Processo concluido: relatorio de cobertura Cucumber gerado.
pause

endlocal
