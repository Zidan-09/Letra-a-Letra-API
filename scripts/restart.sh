#!/usr/bin/env bash

set -e

cd "$(dirname "$0")/.."

echo "=========================="
echo "Restarting Production API"
echo "=========================="

if ! docker restart letra-a-letra-api; then
    echo
    echo "Erro: o container 'letra-a-letra-api' não existe ou não está disponível."
    echo "Use 'start' para iniciar a API."
    exit 1
fi

echo
echo "API de produção reiniciada."