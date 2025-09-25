@echo off
echo Iniciando ambiente Docker para o sistema de Gestao de Projetos
echo ------------------------------------------------------

REM Verifica se o Docker está instalado
docker --version > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Docker nao encontrado. Por favor, instale o Docker Desktop.
    echo Voce pode baixar o Docker Desktop em: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

echo Docker encontrado. Iniciando os containers...

REM Inicia os containers com Docker Compose
docker-compose up

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha ao iniciar os containers. Verifique se o Docker esta em execucao.
    pause
    exit /b 1
)

pause