#!/usr/bin/env bash

set -e

cd "$(dirname "$0")/.."

echo "=========================="
echo "Stopping Production API"
echo "=========================="

if ! docker stop letra-a-letra-api; then
    echo
    echo "Erro: o container 'letra-a-letra-api' não existe ou já está parado."
    exit 1
fi

echo
echo "API de produção parada."