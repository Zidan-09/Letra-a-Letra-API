#!/usr/bin/env bash

set -e

cd "$(dirname "$0")/.."

echo "=========================="
echo "Starting Production API"
echo "=========================="

if docker container inspect letra-a-letra-api >/dev/null 2>&1; then
    echo "Erro: o container 'letra-a-letra-api' já existe."
    echo "Use 'restart' para reiniciá-lo ou 'stop' para pará-lo."
    exit 1
fi

echo "[1] Construindo imagem Docker..."

docker build -t letra-a-letra-api .

echo "[2] Iniciando container..."

docker run -d \
    --name letra-a-letra-api \
    --restart unless-stopped \
    --env-file .env \
    -p 8080:8080 \
    letra-a-letra-api

echo
echo "API de produção iniciada."