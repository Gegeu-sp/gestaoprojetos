#!/bin/bash

echo "Iniciando ambiente Docker para o sistema de Gestao de Projetos"
echo "------------------------------------------------------"

# Verifica se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "ERRO: Docker nao encontrado. Por favor, instale o Docker."
    echo "Em sistemas baseados em Debian/Ubuntu: sudo apt-get install docker.io docker-compose"
    echo "Em sistemas baseados em Red Hat/Fedora: sudo dnf install docker docker-compose"
    echo "Em macOS com Homebrew: brew install --cask docker"
    exit 1
fi

echo "Docker encontrado. Iniciando os containers..."

# Inicia os containers com Docker Compose
docker-compose up

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao iniciar os containers. Verifique se o Docker esta em execucao."
    exit 1
fi