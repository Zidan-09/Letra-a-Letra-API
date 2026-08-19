#!/usr/bin/env bash

set -e

cd "$(dirname "$0")/.."

API_PID=""

cleanup() {
    echo
    echo "=========================="
    echo "Cleaning Test Environment"
    echo "=========================="

    if [[ -n "$API_PID" ]] && kill -0 "$API_PID" 2>/dev/null; then
        echo "[1] Encerrando API..."
        kill "$API_PID" 2>/dev/null || true
        wait "$API_PID" 2>/dev/null || true
    fi

    echo "[2] Encerrando MailHog..."

    docker stop letra-a-letra-mailhog-test >/dev/null 2>&1 || true
    docker rm letra-a-letra-mailhog-test >/dev/null 2>&1 || true

    echo
    echo "Ambiente de testes encerrado."
}

trap cleanup EXIT

echo
echo "=========================="
echo "Starting Tests Dependencies"
echo "=========================="

echo "[1] Iniciando MailHog..."

docker run \
    --name letra-a-letra-mailhog-test \
    --hostname=mailhog \
    -p 1025:1025 \
    -p 8025:8025 \
    -d \
    mailhog/mailhog

echo
echo "=========================="
echo "Running Unit Tests"
echo "=========================="

echo "[1] Executando testes unitários..."

./mvnw test

echo
echo "[2] Testes unitários finalizados!"

echo
echo "=========================="
echo "Loading Integration Tests"
echo "=========================="

echo "[3] Gerando pacote..."

./mvnw clean package -DskipTests

echo "[4] Iniciando API..."

./mvnw spring-boot:run \
    -Dspring-boot.run.profiles=test \
    > api-test.log 2>&1 &

API_PID=$!

echo "[5] Aguardando API ficar disponível..."

until curl -sf http://localhost:8080/actuator/health > /dev/null; do

    if ! kill -0 "$API_PID" 2>/dev/null; then
        echo
        echo "Erro: a API foi encerrada antes de ficar disponível."
        echo
        cat api-test.log
        exit 1
    fi

    sleep 2
done

echo "[6] API pronta!"

echo
echo "=========================="
echo "Running Integration Tests"
echo "=========================="

echo "[7] Executando testes de integração..."

node tools/runner.js

echo
echo "[8] Testes de integração finalizados!"

echo
echo "=========================="
echo "ALL TESTS PASSED"
echo "=========================="

echo
read -n 1 -s -r -p "Pressione qualquer tecla para encerrar o ambiente de testes..."
echo