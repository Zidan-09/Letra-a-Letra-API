@echo off
cd /d "%~dp0.."

echo ==========================
echo Restarting Docker Ambient
echo ==========================

call docker desktop start

docker compose down -v

echo.
echo ==========================
echo Loading Containers
echo ==========================

docker compose --env-file .env.dev up --build