@echo off

echo.
echo ==========================
echo Loading API
echo ==========================

echo [1] Gerando pacote...
call mvnw clean package
echo [2] Pacote gerado!

echo [3] Iniciando API em outro processo...
start "API" cmd /k "mvnw spring-boot:run -Dspring-boot.run.profiles=test"

:wait
netstat -an | find ":8080" | find "LISTENING" > nul
if errorlevel 1 (
    timeout /t 1 > nul
    goto wait
)

echo [4] API iniciada.

echo [5] Iniciando testes javascript.
call node tools\autotests\apiMatchmakingTestAuto.js
call node tools\autotests\apiTestAuto.js
echo [6] Testes javascript finalizados!