<h1 align="center">
  Sistema de Gestão de Projetos
</h1>

<p align="center">
  <strong>Projeto acadêmico do 2º semestre de Análise e Desenvolvimento de Sistemas (ADS).</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/universidade-anhembi_morumbi-blue" alt="Universidade Anhembi Morumbi">
  <img src="https://img.shields.io/badge/status-concluído-green" alt="Status do Projeto: Concluído">
  <img src="https://img.shields.io/badge/java-11+-orange" alt="Java 11+">
  <img src="https://img.shields.io/badge/mysql-8.3.0-blue" alt="MySQL 8.3.0">
  <img src="https://img.shields.io/badge/docker-containerizado-blue" alt="Docker">
</p>

<p align="center">
  <a href="#-contexto">Contexto</a> •
  <a href="#-sobre-o-projeto">Sobre</a> •
  <a href="#-funcionalidades">Funcionalidades</a> •
  <a href="#-tecnologias">Tecnologias</a> •
  <a href="#-como-executar">Como Executar</a> •
  <a href="#-equipe">Equipe</a>
</p>

<div align="center">
</div>

## 🎓 Contexto

Este projeto está sendo desenvolvido como parte da avaliação da disciplina de **PROGRAMAÇÃO DE SOLUÇÕES COMPUTACIONAIS** do **2º semestre** do curso de **Análise e Desenvolvimento de Sistemas** da **Universidade Anhembi Morumbi**. O objetivo é aplicar os conceitos aprendidos nas disciplinas para criar uma solução de software funcional e profissional.

## 🎯 Sobre o Projeto

O **Sistema de Gestão de Projetos** é uma aplicação desktop robusta desenvolvida em Java que oferece uma solução completa para gerenciamento de projetos, equipes e tarefas. Com interface gráfica intuitiva e banco de dados estruturado, o sistema permite controle total sobre o ciclo de vida de projetos empresariais, desde o planejamento até a conclusão.

## ✨ Funcionalidades Principais

- **✔️ Gestão de Usuários:** Sistema completo de autenticação com criptografia BCrypt e controle de permissões por níveis de acesso
- **✔️ Gestão de Projetos:** CRUD completo com controle de status, prazos e associação com equipes responsáveis
- **✔️ Gestão de Equipes:** Formação de equipes multidisciplinares com atribuição de papéis e controle de disponibilidade
- **✔️ Gestão de Tarefas:** Criação e acompanhamento com sistema de prioridades e controle de dependências
- **✔️ Sistema de Relatórios:** Relatórios gerenciais personalizados com análise de produtividade e métricas de projeto
- **✔️ Gestão de Alocações:** Distribuição inteligente de recursos com otimização de cronogramas

## 🚀 Tecnologias Utilizadas

As seguintes ferramentas foram utilizadas na construção do projeto:

| Ferramenta | Descrição |
|-----------|-----------|
| **Backend** | `Java 11+, Maven, JDBC, HikariCP` |
| **Frontend** | `Java Swing, AWT, Interface Gráfica Desktop` |
| **Banco de Dados**| `MySQL 8.3.0, Docker Compose` |
| **Segurança** | `BCrypt, Validação de Entrada, Controle de Acesso` |
| **DevOps** | `Docker, Maven, Scripts de Automação` |
| **Logging** | `SLF4J, Sistema de Logs Estruturado` |

## 📁 Arquitetura do Projeto

```
projetogestao_facul/
├── src/main/java/br/com/projetogestao/
│   ├── Main.java                    # Classe principal com inicialização
│   ├── config/                      # Configurações do sistema
│   │   ├── AppProperties.java       # Propriedades da aplicação
│   │   └── DataSourceProvider.java  # Provedor de conexões
│   ├── dao/                         # Data Access Objects
│   │   ├── UsuarioDaoImpl.java      # Operações de usuário
│   │   ├── ProjetoDaoImpl.java      # Operações de projeto
│   │   ├── EquipeDaoImpl.java       # Operações de equipe
│   │   ├── TarefaDaoImpl.java       # Operações de tarefa
│   │   └── ProjetoEquipeDaoImpl.java # Relacionamentos
│   ├── model/                       # Modelos de dados
│   │   ├── Usuario.java             # Entidade usuário
│   │   ├── Projeto.java             # Entidade projeto
│   │   ├── Equipe.java              # Entidade equipe
│   │   ├── Tarefa.java              # Entidade tarefa
│   │   └── enums/                   # Enumerações (Status, Perfil)
│   ├── service/                     # Lógica de negócio
│   │   ├── AuthService.java         # Autenticação
│   │   ├── UsuarioService.java      # Regras de usuário
│   │   ├── ProjetoService.java      # Regras de projeto
│   │   ├── EquipeService.java       # Regras de equipe
│   │   └── TarefaService.java       # Regras de tarefa
│   ├── ui/                          # Interface gráfica
│   │   ├── MainFrame.java           # Frame principal
│   │   ├── LoginFrame.java          # Tela de login
│   │   ├── UsuariosPanel.java       # Painel de usuários
│   │   ├── ProjetosPanel.java       # Painel de projetos
│   │   ├── EquipesPanel.java        # Painel de equipes
│   │   ├── PainelTarefas.java       # Painel de tarefas
│   │   ├── RelatoriosPanel.java     # Dashboard e relatórios
│   │   ├── AlocacoesPanel.java      # Painel de alocações
│   │   └── UsuarioNotificationManager.java # Sistema de notificações
│   └── util/                        # Utilitários
│       └── DateUtil.java            # Formatação de datas
├── src/main/resources/
│   ├── application.properties       # Configurações da aplicação
│   └── database/create_tables.sql   # Scripts de criação do banco
├── database/                        # Configurações do banco
├── docker-compose.yml               # Orquestração Docker
├── run-local.bat/.sh               # Scripts de execução local
└── pom.xml                         # Configuração Maven
```

## 🏁 Como Executar

Siga os passos abaixo para rodar o projeto em seu ambiente local.

### Opção 1: Usando Docker (Recomendado)

```bash
# 1. Clone este repositório
$ git clone https://github.com/Gegeu-sp/gestaoprojetos.git

# 2. Acesse a pasta do projeto
$ cd gestaoprojetos

# 3. Execute com Docker Compose
$ docker-compose up -d

# 4. Compile e execute a aplicação
$ mvn clean compile exec:java
```

### Opção 2: Instalação Local

```bash
# 1. Clone este repositório
$ git clone https://github.com/Gegeu-sp/gestaoprojetos.git

# 2. Acesse a pasta do projeto
$ cd gestaoprojetos

# 3. Configure o MySQL local (porta 3306)
# Crie o banco: gestao_projetos

# 4. Compile e execute
$ mvn clean compile exec:java
```

### 🗄️ Configuração do Banco de Dados

**Configurações Docker:**
- **Host**: localhost
- **Porta**: 3306
- **Database**: gestao_projetos
- **Usuário**: gp
- **Senha**: gp123
- **Encoding**: UTF-8
- **Timezone**: America/Sao_Paulo

**Inicialização Automática:**
- Criação automática das tabelas
- Inserção de dados de exemplo
- Usuários padrão configurados

### Usuários Padrão do Sistema

| Usuário | Senha | Perfil |
|---------|-------|--------|
| admin | admin123 | Administrador |
| gerente | gerente123 | Gerente |
| dev | dev123 | Desenvolvedor |

## 👥 Equipe do Projeto

Este projeto foi idealizado e desenvolvido em equipe pelos seguintes alunos:

* **Argeu Rodrigues** - [LinkedIn](https://www.linkedin.com/in/argeu-rodrigues-9a6b7174)
* **Josevaldo dos Santos Lima** - [LinkedIn](https://www.linkedin.com/in/josevaldo-dos-santos-lima-47919714b)
* **Victor Souza da Rocha** - [LinkedIn](https://www.linkedin.com/in/victor-souza-da-rocha)

---

## 💡 Aprendizados e Práticas

Durante o desenvolvimento deste projeto, a equipe aplicou diversos conceitos fundamentais do ciclo de desenvolvimento de software, consolidando o conhecimento adquirido em sala de aula. O trabalho foi realizado de forma colaborativa, onde todos os membros participaram ativamente das etapas de planejamento, desenvolvimento e testes.

Os principais aprendizados e tecnologias aplicadas foram:

* **Versionamento com Git e GitHub:**
    * Utilizamos o GitHub como repositório centralizado para o código.
    * Adotamos práticas de versionamento com `commits` semânticos, criação de `branches` para novas funcionalidades e resolução de conflitos (`merge`), garantindo a integridade do projeto e a colaboração eficiente.

* **Desenvolvimento Backend:**
    * Construímos a lógica de negócio e as regras da aplicação.
    * Desenvolvemos a API para comunicação com o frontend, gerenciando as rotas, requisições e respostas.
    * **Tecnologias:** `Java 11+, Maven, JDBC, HikariCP, BCrypt`

* **Desenvolvimento Frontend:**
    * Criamos a interface do usuário, focando em uma experiência de uso clara e intuitiva.
    * Estruturamos os componentes da aplicação e implementamos a lógica para interagir com o backend.
    * **Tecnologias:** `Java Swing, AWT, Interface Gráfica Desktop`

* **Modelagem e Banco de Dados:**
    * Planejamos e estruturamos o banco de dados para armazenar as informações de projetos, tarefas e usuários de forma eficiente.
    * Implementamos as consultas e a lógica para manipulação dos dados.
    * **Tecnologias:** `MySQL 8.3.0, Docker Compose, SQL`

* **Trabalho em Equipe e Metodologia:**
    * Organizamos nossas tarefas utilizando um quadro Kanban simples para visualizar o progresso.
    * Realizamos reuniões periódicas para alinhar as demandas e garantir que todos estivessem na mesma página, promovendo uma comunicação clara e contínua.

<br>
<p align="center">
  2º semestre de Análise e Desenvolvimento de Sistemas (ADS) Anhembi Morumbi.
</p>
