@echo off
echo ==========================
echo Restarting Docker Ambient
echo ==========================

call docker desktop start

docker-compose down -v

echo.
echo ==========================
echo Loading Containers
echo ==========================

docker-compose up --build