#!/bin/bash
echo "Executando o Sistema de Gestao de Projetos localmente..."
echo ""

mvn clean compile && mvn exec:java