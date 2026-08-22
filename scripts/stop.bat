@echo off
cd /d "%~dp0.."

echo ==========================
echo Stopping Production API
echo ==========================

docker stop letra-a-letra-api

echo.
echo API de producao parada.