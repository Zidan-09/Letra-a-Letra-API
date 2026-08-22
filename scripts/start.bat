@echo off
cd /d "%~dp0.."

echo ==========================
echo Starting Production API
echo ==========================

call docker desktop start

docker build -t letra-a-letra-api .

if errorlevel 1 (
    echo Falha ao construir a imagem.
    exit /b 1
)

docker run -d ^
    --name letra-a-letra-api ^
    --restart unless-stopped ^
    --env-file .env.prod ^
    -p 8080:8080 ^
    letra-a-letra-api

echo.
echo API de producao iniciada.