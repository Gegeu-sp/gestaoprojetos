@echo off
echo Configurando banco de dados para o sistema de Gestao de Projetos
echo ------------------------------------------------------

REM Verifica se o MySQL está instalado
mysql --version > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: MySQL nao encontrado. Por favor, instale o MySQL e adicione-o ao PATH.
    echo Voce pode baixar o MySQL em: https://dev.mysql.com/downloads/installer/
    pause
    exit /b 1
)

echo MySQL encontrado. Iniciando configuracao do banco de dados...

REM Solicita a senha do root do MySQL
set /p MYSQL_ROOT_PASSWORD=Digite a senha do usuario root do MySQL (deixe em branco se nao tiver senha): 

REM Executa o script SQL para criar o banco de dados e o usuário
if "%MYSQL_ROOT_PASSWORD%"=="" (
    mysql -u root < database\create_tables.sql
) else (
    mysql -u root -p%MYSQL_ROOT_PASSWORD% < database\create_tables.sql
)

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha ao executar o script SQL. Verifique a senha e tente novamente.
    pause
    exit /b 1
)

echo Banco de dados configurado com sucesso!
echo Usuario: gp
echo Senha: gp123
echo Banco de dados: gestao_projetos

echo.
echo Para executar o aplicativo, use o comando: mvn exec:java
echo.

pause