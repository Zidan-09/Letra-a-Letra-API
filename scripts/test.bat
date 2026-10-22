@echo off
setlocal EnableExtensions

cd /d "%~dp0.."

set "API_LOG=%CD%\logs\test-api.log"
set "API_WINDOW_TITLE=API TEST"
set "MAX_WAIT_SECONDS=120"
set "WAIT_COUNT=0"
set "WAIT_INTERVAL=2"

if not exist "logs" mkdir "logs"

del "%API_LOG%" 2>nul

echo.
echo ==========================
echo Running Unit Tests
echo ==========================

echo [1] Executando testes unitarios...

call mvnw test

if errorlevel 1 (
    echo.
    echo ERRO: Testes unitarios falharam.
    goto cleanup_with_error
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
    goto cleanup_with_error
)

echo [5] Gerando pacote...

call mvnw clean package -DskipTests

if errorlevel 1 (
    echo.
    echo ERRO: Falha ao gerar o pacote.
    goto cleanup_with_error
)

echo [6] Iniciando API...

start "%API_WINDOW_TITLE%" /D "%CD%" cmd /k "mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.useTestClasspath=true"

echo [7] Aguardando API ficar disponivel...

:wait
curl.exe -sf http://localhost:8080/actuator/health >nul 2>&1

if %ERRORLEVEL% EQU 0 goto api_ready

set /a WAIT_COUNT=WAIT_COUNT+WAIT_INTERVAL

if %WAIT_COUNT% GEQ %MAX_WAIT_SECONDS% goto api_failed

timeout /t %WAIT_INTERVAL% /nobreak >nul
goto wait

:api_ready
echo [8] API pronta!

echo.
echo ==========================
echo Running Integration Tests
echo ==========================

echo [9] Executando testes de integracao...

echo [DEBUG] Iniciando runner.js...
call node tools\runner.js
set "RUNNER_EXIT=%ERRORLEVEL%"

echo.
echo [DEBUG] runner.js terminou com codigo: %RUNNER_EXIT%
pause

if not "%RUNNER_EXIT%"=="0" (
    echo.
    echo ERRO: Testes de integracao falharam.
    goto cleanup_with_error
)

echo.
echo [10] Testes de integracao finalizados!

echo.
echo ==========================
echo ALL TESTS PASSED
echo ==========================

echo.
pause

goto cleanup

:api_failed
echo.
echo ===============================================
echo FALHA AO INICIAR A API
echo ===============================================
echo.
if exist "%API_LOG%" (
    echo Log salvo em: %API_LOG%
    echo.
    echo Ultimas linhas do log:
    echo ------------------------------------------------
    powershell -Command "Get-Content '%API_LOG%' -Tail 50"
    echo ------------------------------------------------
) else (
    echo Log nao encontrado em: %API_LOG%
)
echo.
echo Verifique a janela '%API_WINDOW_TITLE%' para mais detalhes.
echo.
goto cleanup_with_error

:cleanup
echo.
echo ==========================
echo Cleaning Test Environment
echo ==========================

call :stop_environment
endlocal
exit /b 0

:cleanup_with_error
echo.
echo ==========================
echo Cleaning Test Environment
echo ==========================

call :stop_environment
endlocal
exit /b 1

:stop_environment
echo [1] Encerrando API...

taskkill /FI "WINDOWTITLE eq %API_WINDOW_TITLE%*" /T /F > nul 2>&1

echo [2] Encerrando MailHog...

docker stop letra-a-letra-mailhog-test > nul 2>&1
docker rm letra-a-letra-mailhog-test > nul 2>&1

echo.
echo Ambiente de testes encerrado.
exit /b 0
