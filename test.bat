@echo off

echo.
echo ==========================
echo Loading API
echo ==========================

echo [0] Iniciando Docker Desktop e Serviço de Mail...

call docker desktop start
call docker run --hostname=2bda65a30326 --user=mailhog --env=PATH=/go/bin:/usr/local/go/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin --env=GOLANG_VERSION=1.14.7 --env=GOPATH=/go --network=bridge --workdir=/home/mailhog -p 1025:1025 -p 8025:8025 --restart=no --runtime=runc -d mailhog/mailhog

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

echo [6] Iniciando testes de integração...

call node tools\tests\runner.js

echo [8] Testes finalizados!