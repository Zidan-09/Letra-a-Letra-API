@echo off
setlocal

cd /d "%~dp0.."

echo.
echo ==========================
echo Running Unit Tests
echo ==========================

echo [1] Executando testes unitarios...

call mvnw test

if errorlevel 1 (
    echo.
    echo ERRO: Testes unitarios falharam.
    goto cleanup
)

echo.
echo [2] Testes unitarios finalizados!

echo.
echo ==========================
echo Loading Integration Tests
echo ==========================

echo [3] Iniciando Docker Desktop...

call docker desktop start

echo [4] Iniciando MailHog...

docker run ^
    --name letra-a-letra-mailhog-test ^
    --hostname=mailhog ^
    -p 1025:1025 ^
    -p 8025:8025 ^
    -d ^
    mailhog/mailhog

if errorlevel 1 (
    echo.
    echo ERRO: Falha ao iniciar o MailHog.
    goto cleanup
)

echo [5] Gerando pacote...

call mvnw clean package -DskipTests

if errorlevel 1 (
    echo.
    echo ERRO: Falha ao gerar o pacote.
    goto cleanup
)

echo [6] Iniciando API...

start "API TEST" cmd /c "cd /d "%CD%" && mvnw spring-boot:run -Dspring-boot.run.profiles=test"

echo [7] Aguardando API ficar disponivel...

:wait
curl -sf http://localhost:8080/actuator/health > nul

if errorlevel 1 (
    timeout /t 2 /nobreak > nul
    goto wait
)

echo [8] API pronta!

echo.
echo ==========================
echo Running Integration Tests
echo ==========================

echo [9] Executando testes de integracao...

call node tools\runner.js

if errorlevel 1 (
    echo.
    echo ERRO: Testes de integracao falharam.
    goto cleanup
)

echo.
echo [10] Testes de integracao finalizados!

echo.
echo ==========================
echo ALL TESTS PASSED
echo ==========================

echo.
pause

:cleanup

echo.
echo ==========================
echo Cleaning Test Environment
echo ==========================

echo [1] Encerrando API...

taskkill /FI "WINDOWTITLE eq API TEST*" /T /F > nul 2>&1

echo [2] Encerrando MailHog...

docker stop letra-a-letra-mailhog-test > nul 2>&1
docker rm letra-a-letra-mailhog-test > nul 2>&1

echo.
echo Ambiente de testes encerrado.

endlocal