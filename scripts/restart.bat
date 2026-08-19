@echo off
cd /d "%~dp0.."

echo ==========================
echo Restarting Production API
echo ==========================

docker restart letra-a-letra-api

echo.
echo API de producao reiniciada.