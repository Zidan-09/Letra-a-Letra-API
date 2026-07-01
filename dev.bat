@echo off

echo.
echo ==========================
echo Loading API
echo ==========================

echo [1] Gerando pacote...
call mvnw clean package

echo [2] Pacote gerado!

echo [3] Iniciando API...
call mvnw spring-boot:run -Dspring-boot.run.profiles=dev