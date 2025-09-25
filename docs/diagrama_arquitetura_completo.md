# Diagrama Completo da Arquitetura - Sistema de Gestão de Projetos

## 📋 Visão Geral da Arquitetura

O sistema segue uma arquitetura em camadas (Layered Architecture) com padrões MVC, DAO e Observer, implementada em Java com Swing para interface gráfica e MySQL como banco de dados.

## 🏗️ Estrutura de Camadas

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO (UI)                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │ MainFrame   │ │UsuariosPanel│ │ProjetosPanel│ │ TarefasPanel││
│  │             │ │             │ │             │ │             ││
│  │ - Login     │ │ - CRUD      │ │ - CRUD      │ │ - CRUD      ││
│  │ - Dashboard │ │ - Validação │ │ - Status    │ │ - Atribuição││
│  │ - Navegação │ │ - Perfis    │ │ - Gerentes  │ │ - Progresso ││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │EquipesPanel │ │AlocacoesPanel│ │LoginDialog  │               │
│  │             │ │             │ │             │               │
│  │ - Formação  │ │ - Vinculação│ │ - Autent.   │               │
│  │ - Membros   │ │ - Relatórios│ │ - Segurança │               │
│  └─────────────┘ └─────────────┘ └─────────────┘               │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                   CAMADA DE SERVIÇOS (BUSINESS)                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │UsuarioService│ │ProjetoService│ │TarefaService│ │EquipeService││
│  │             │ │             │ │             │ │             ││
│  │ - Validação │ │ - Regras    │ │ - Workflow  │ │ - Formação  ││
│  │ - Hash BCrypt│ │ - Status    │ │ - Dependên. │ │ - Atribuição││
│  │ - Perfis    │ │ - Datas     │ │ - Notific.  │ │ - Relatórios││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
│  ┌─────────────┐ ┌─────────────┐                               │
│  │ProjetoEquipe│ │NotificationM│                               │
│  │Service      │ │anager       │                               │
│  │             │ │             │                               │
│  │ - Vinculação│ │ - Observer  │                               │
│  │ - Relatórios│ │ - Eventos   │                               │
│  └─────────────┘ └─────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                   CAMADA DE ACESSO A DADOS (DAO)               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │UsuarioDaoImpl│ │ProjetoDaoImpl│ │TarefaDaoImpl│ │EquipeDaoImpl││
│  │             │ │             │ │             │ │             ││
│  │ - CRUD SQL  │ │ - CRUD SQL  │ │ - CRUD SQL  │ │ - CRUD SQL  ││
│  │ - Prepared  │ │ - Prepared  │ │ - Prepared  │ │ - Prepared  ││
│  │ - Mapping   │ │ - Mapping   │ │ - Mapping   │ │ - Mapping   ││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
│  ┌─────────────┐                                               │
│  │ProjetoEquipe│                                               │
│  │DaoImpl      │                                               │
│  │             │                                               │
│  │ - Relações  │                                               │
│  │ - N:N       │                                               │
│  └─────────────┘                                               │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CAMADA DE CONFIGURAÇÃO                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │DataSource   │ │AppProperties│ │HikariCP     │               │
│  │Provider     │ │             │ │Pool         │               │
│  │             │ │ - Config    │ │             │               │
│  │ - Singleton │ │ - Properties│ │ - Connection│               │
│  │ - Factory   │ │ - Ambiente  │ │ - Performance│               │
│  └─────────────┘ └─────────────┘ └─────────────┘               │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BANCO DE DADOS MySQL                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │   users     │ │  projects   │ │   tasks     │ │   teams     ││
│  │             │ │             │ │             │ │             ││
│  │ - id (PK)   │ │ - id (PK)   │ │ - id (PK)   │ │ - id (PK)   ││
│  │ - full_name │ │ - name      │ │ - title     │ │ - name      ││
│  │ - cpf       │ │ - description│ │ - description│ │ - description││
│  │ - email     │ │ - start_date│ │ - project_id│ │             ││
│  │ - position  │ │ - end_date  │ │ - assignee_id│ │             ││
│  │ - login     │ │ - status    │ │ - status    │ │             ││
│  │ - password  │ │ - manager_id│ │ - dates     │ │             ││
│  │ - profile   │ │             │ │             │ │             ││
│  │ - birth_date│ │             │ │             │ │             ││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
│  ┌─────────────┐                                               │
│  │project_teams│                                               │
│  │             │                                               │
│  │ - project_id│                                               │
│  │ - team_id   │                                               │
│  │ (Tabela N:N)│                                               │
│  └─────────────┘                                               │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo de Dados Principal

### 1. Fluxo de Autenticação
```
LoginDialog → UsuarioService → UsuarioDaoImpl → MySQL
     ↓              ↓              ↓           ↓
  Captura      Validação      Query SQL    Verificação
  Credenciais   + BCrypt      Prepared     Credenciais
     ↓              ↓              ↓           ↓
  MainFrame ← UsuarioService ← UsuarioDaoImpl ← MySQL
```

### 2. Fluxo CRUD (Exemplo: Projetos)
```
ProjetosPanel → ProjetoService → ProjetoDaoImpl → MySQL
      ↓              ↓               ↓           ↓
   Formulário    Validações      SQL CRUD    Persistência
   + Eventos     + Regras        Prepared    + Transações
      ↓              ↓               ↓           ↓
   Atualização ← NotificationM ← ProjetoDaoImpl ← MySQL
   Interface     Observer        Resultado     Confirmação
```

### 3. Fluxo de Relacionamentos (Projeto-Equipe)
```
AlocacoesPanel → ProjetoEquipeService → ProjetoEquipeDaoImpl → MySQL
       ↓                ↓                      ↓              ↓
   Seleção         Validação              INSERT/DELETE    Tabela N:N
   Combos          Regras                 project_teams    Relacionamento
       ↓                ↓                      ↓              ↓
   Tabela      ← Atualização        ← Confirmação      ← MySQL
   Alocações     Interface            Operação         Resultado
```

## 📊 Modelo de Entidades (Diagrama ER)

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│     Usuario     │         │     Projeto     │         │     Tarefa      │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id: Long (PK)   │    1    │ id: Long (PK)   │    1    │ id: Long (PK)   │
│ nomeCompleto    │ ────────│ nome: String    │ ────────│ titulo: String  │
│ cpf: String     │ gerencia│ descricao       │ contém  │ descricao       │
│ email: String   │         │ dataInicio      │    *    │ projetoId (FK)  │
│ cargo: String   │         │ dataTermPrev    │         │ responsavelId   │
│ login: String   │         │ dataTermReal    │         │ status: Enum    │
│ senhaHash       │         │ status: Enum    │         │ dataInicioPrev  │
│ perfil: Enum    │         │ gerenteId (FK)  │         │ dataTermPrev    │
│ dataNascimento  │         │                 │         │ dataInicioReal  │
└─────────────────┘         └─────────────────┘         │ dataTermReal    │
         │                           │                   └─────────────────┘
         │                           │                            │
         │ 1                         │ *                          │ *
         │                           │                            │
         │                  ┌─────────────────┐                  │ 1
         │                  │ ProjetoEquipe   │                  │
         │                  ├─────────────────┤                  │
         │                  │ projetoId (FK)  │                  │
         │                  │ equipeId (FK)   │                  │
         │                  └─────────────────┘                  │
         │                           │                            │
         │                           │ *                          │
         │                           │                            │
         │ *                ┌─────────────────┐                  │
         └──────────────────│     Equipe      │──────────────────┘
                      *     ├─────────────────┤     *
                            │ id: Long (PK)   │ composta por
                            │ nome: String    │
                            │ descricao       │
                            └─────────────────┘
```

## 🎯 Padrões de Projeto Implementados

### 1. **DAO (Data Access Object)**
```java
// Interface
public interface UsuarioDao {
    void salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
}

// Implementação
public class UsuarioDaoImpl implements UsuarioDao {
    private final DataSource dataSource;
    // Implementações com SQL
}
```

### 2. **MVC (Model-View-Controller)**
- **Model**: Entidades (Usuario, Projeto, Tarefa, Equipe)
- **View**: Painéis Swing (UsuariosPanel, ProjetosPanel, etc.)
- **Controller**: Services (UsuarioService, ProjetoService, etc.)

### 3. **Observer Pattern**
```java
public interface UsuarioListener {
    void onUsuarioCadastrado(Usuario usuario);
    void onUsuarioAtualizado(Usuario usuario);
}

public class NotificationManager {
    private List<UsuarioListener> listeners;
    public void notificar(Usuario usuario) {
        listeners.forEach(l -> l.onUsuarioCadastrado(usuario));
    }
}
```

### 4. **Singleton Pattern**
- **DataSourceProvider**: Conexão única com banco
- **AppProperties**: Configurações globais
- **NotificationManager**: Gerenciador de eventos

### 5. **Factory Pattern**
- Criação de DAOs
- Configuração de DataSource
- Instanciação de Services

## 🔧 Tecnologias e Componentes

### **Frontend (Swing)**
- **JFrame**: Janela principal
- **JPanel**: Painéis de funcionalidades
- **JTable**: Listagens com modelos customizados
- **JFormattedTextField**: Campos com validação
- **JComboBox**: Seleções dinâmicas
- **Layout Managers**: BorderLayout, GridBagLayout

### **Backend (Java)**
- **Services**: Lógica de negócio
- **DAOs**: Acesso a dados
- **Models**: Entidades do domínio
- **Enums**: Status e perfis
- **Validações**: CPF, datas, regras de negócio

### **Banco de Dados (MySQL)**
- **HikariCP**: Pool de conexões
- **JDBC**: Conectividade
- **Prepared Statements**: Segurança SQL
- **Transações**: Integridade de dados

### **Configuração**
- **Properties**: Configurações externas
- **DataSource**: Gerenciamento de conexões
- **Logging**: Rastreamento de operações

## 📈 Fluxos de Negócio Principais

### **1. Gestão de Usuários**
1. Cadastro com validação de CPF
2. Definição de perfis (Admin, Gerente, Colaborador)
3. Autenticação com hash BCrypt
4. Notificações automáticas de mudanças

### **2. Gestão de Projetos**
1. Criação com gerente responsável
2. Controle de status (Planejado → Em Andamento → Concluído)
3. Datas de início e término (prevista/real)
4. Vinculação com equipes

### **3. Gestão de Tarefas**
1. Criação vinculada a projetos
2. Atribuição a responsáveis
3. Controle de progresso
4. Datas de execução

### **4. Gestão de Equipes**
1. Formação de equipes
2. Atribuição a projetos (N:N)
3. Relatórios de alocação
4. Controle de membros

## 🔒 Aspectos de Segurança

- **Autenticação**: Login/senha com hash BCrypt
- **Autorização**: Controle por perfis de usuário
- **SQL Injection**: Prepared Statements
- **Validação**: Entrada de dados rigorosa
- **Conexões**: Pool gerenciado com HikariCP

## 📊 Métricas e Monitoramento

- **Performance**: Pool de conexões otimizado
- **Logs**: Rastreamento de operações críticas
- **Validações**: Feedback imediato ao usuário
- **Transações**: Integridade garantida
- **Notificações**: Atualizações em tempo real

Este diagrama representa a arquitetura completa do sistema, mostrando todas as camadas, componentes, relacionamentos e fluxos de dados implementados no projeto de Gestão de Projetos.