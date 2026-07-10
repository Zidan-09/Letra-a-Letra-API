@echo off

echo.
echo ==========================
echo Loading API
echo ==========================

echo [1] Gerando pacote...
call mvnw clean package

echo [2] Pacote gerado!

echo [3] Iniciando API...
start "API" cmd /k "mvnw spring-boot:run -Dspring-boot.run.profiles=test"

echo [4] Aguardando API ficar disponivel...

:wait
curl -s http://localhost:8080/actuator/health > nul

if errorlevel 1 (
    timeout /t 2 > nul
    goto wait
)

echo [5] API pronta!

echo [6] Iniciando testes javascript...

call node tools\apiMatchmakingTestAuto.js
call node tools\apiRankingTestAuto.js
call node tools\apiTestAuto.js
call node tools\apiFriendTestAuto.js

echo [7] Testes finalizados!