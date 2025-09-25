#!/bin/bash

echo "Configurando banco de dados para o sistema de Gestao de Projetos"
echo "------------------------------------------------------"

# Verifica se o MySQL está instalado
if ! command -v mysql &> /dev/null; then
    echo "ERRO: MySQL nao encontrado. Por favor, instale o MySQL."
    echo "Em sistemas baseados em Debian/Ubuntu: sudo apt-get install mysql-server"
    echo "Em sistemas baseados em Red Hat/Fedora: sudo dnf install mysql-server"
    echo "Em macOS com Homebrew: brew install mysql"
    exit 1
fi

echo "MySQL encontrado. Iniciando configuracao do banco de dados..."

# Solicita a senha do root do MySQL
read -sp "Digite a senha do usuario root do MySQL (deixe em branco se nao tiver senha): " MYSQL_ROOT_PASSWORD
echo ""

# Executa o script SQL para criar o banco de dados e o usuário
if [ -z "$MYSQL_ROOT_PASSWORD" ]; then
    mysql -u root < database/create_tables.sql
else
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" < database/create_tables.sql
fi

if [ $? -ne 0 ]; then
    echo "ERRO: Falha ao executar o script SQL. Verifique a senha e tente novamente."
    exit 1
fi

echo "Banco de dados configurado com sucesso!"
echo "Usuario: gp"
echo "Senha: gp123"
echo "Banco de dados: gestao_projetos"

echo ""
echo "Para executar o aplicativo, use o comando: mvn exec:java"
echo ""