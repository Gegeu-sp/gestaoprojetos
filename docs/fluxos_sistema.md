# Fluxos Detalhados do Sistema de Gestão de Projetos

## 🔄 Fluxos Principais do Sistema

### 1. **Fluxo de Autenticação e Autorização**

```mermaid
sequenceDiagram
    participant U as Usuário
    participant LD as LoginDialog
    participant US as UsuarioService
    participant UD as UsuarioDaoImpl
    participant DB as MySQL
    participant MF as MainFrame

    U->>LD: Insere credenciais
    LD->>US: autenticar(login, senha)
    US->>UD: buscarPorLogin(login)
    UD->>DB: SELECT * FROM users WHERE login=?
    DB-->>UD: Dados do usuário
    UD-->>US: Optional<Usuario>
    US->>US: BCrypt.checkpw(senha, hash)
    alt Autenticação válida
        US-->>LD: Usuario autenticado
        LD->>MF: new MainFrame(usuario)
        MF-->>U: Interface principal
    else Credenciais inválidas
        US-->>LD: Exception
        LD-->>U: Mensagem de erro
    end
```

**Componentes Envolvidos:**
- **LoginDialog**: Captura credenciais
- **UsuarioService**: Validação e lógica de autenticação
- **UsuarioDaoImpl**: Acesso aos dados do usuário
- **BCrypt**: Hash de senhas para segurança

---

### 2. **Fluxo CRUD de Projetos**

#### 2.1 Criação de Projeto
```mermaid
sequenceDiagram
    participant PP as ProjetosPanel
    participant PS as ProjetoService
    participant PD as ProjetoDaoImpl
    participant UD as UsuarioDaoImpl
    participant DB as MySQL
    participant NM as NotificationManager

    PP->>PP: Preenche formulário
    PP->>PS: salvar(projeto)
    PS->>PS: validarDados(projeto)
    PS->>UD: buscarPorId(gerenteId)
    UD-->>PS: Gerente válido
    PS->>PD: salvar(projeto)
    PD->>DB: INSERT INTO projects...
    DB-->>PD: ID gerado
    PD-->>PS: Projeto salvo
    PS->>NM: notificarProjetoCriado(projeto)
    NM->>PP: onProjetoCadastrado(projeto)
    PP->>PP: atualizarTabela()
    PS-->>PP: Sucesso
```

#### 2.2 Listagem de Projetos
```mermaid
sequenceDiagram
    participant PP as ProjetosPanel
    participant PS as ProjetoService
    participant PD as ProjetoDaoImpl
    participant DB as MySQL

    PP->>PS: listarTodos()
    PS->>PD: listarTodos()
    PD->>DB: SELECT * FROM projects ORDER BY start_date DESC
    DB-->>PD: ResultSet
    PD->>PD: mapper(rs) para cada linha
    PD-->>PS: List<Projeto>
    PS-->>PP: Lista de projetos
    PP->>PP: atualizarTableModel(projetos)
```

---

### 3. **Fluxo de Gestão de Tarefas**

#### 3.1 Criação de Tarefa
```mermaid
sequenceDiagram
    participant TP as TarefasPanel
    participant TS as TarefaService
    participant TD as TarefaDaoImpl
    participant PD as ProjetoDaoImpl
    participant UD as UsuarioDaoImpl
    participant DB as MySQL

    TP->>TS: salvar(tarefa)
    TS->>PD: buscarPorId(projetoId)
    PD-->>TS: Projeto válido
    TS->>UD: buscarPorId(responsavelId)
    UD-->>TS: Usuário válido
    TS->>TS: validarDatas(tarefa)
    TS->>TD: salvar(tarefa)
    TD->>DB: INSERT INTO tasks...
    DB-->>TD: Tarefa criada
    TD-->>TS: Sucesso
    TS-->>TP: Tarefa salva
    TP->>TP: atualizarInterface()
```

#### 3.2 Atualização de Status
```mermaid
sequenceDiagram
    participant TP as TarefasPanel
    participant TS as TarefaService
    participant TD as TarefaDaoImpl
    participant DB as MySQL
    participant NM as NotificationManager

    TP->>TS: atualizarStatus(tarefaId, novoStatus)
    TS->>TD: buscarPorId(tarefaId)
    TD-->>TS: Tarefa atual
    TS->>TS: validarTransicaoStatus()
    TS->>TD: atualizar(tarefa)
    TD->>DB: UPDATE tasks SET status=?, updated_at=?
    DB-->>TD: Atualização confirmada
    TD-->>TS: Sucesso
    TS->>NM: notificarStatusAlterado(tarefa)
    NM->>TP: onTarefaAtualizada(tarefa)
    TS-->>TP: Status atualizado
```

---

### 4. **Fluxo de Alocação Projeto-Equipe**

#### 4.1 Vinculação de Equipe a Projeto
```mermaid
sequenceDiagram
    participant AP as AlocacoesPanel
    participant PES as ProjetoEquipeService
    participant PED as ProjetoEquipeDaoImpl
    participant DB as MySQL

    AP->>PES: vincular(projetoId, equipeId)
    PES->>PED: atribuirEquipeAoProjeto(projetoId, equipeId)
    PED->>DB: INSERT INTO project_teams (project_id, team_id)
    alt Vinculação bem-sucedida
        DB-->>PED: Registro criado
        PED-->>PES: Sucesso
        PES-->>AP: Equipe vinculada
        AP->>AP: atualizarTabelaAlocacoes()
    else Erro (já vinculado)
        DB-->>PED: Constraint violation
        PED-->>PES: Exception
        PES-->>AP: Erro: Já vinculado
    end
```

#### 4.2 Relatório de Alocações
```mermaid
sequenceDiagram
    participant AP as AlocacoesPanel
    participant PES as ProjetoEquipeService
    participant PED as ProjetoEquipeDaoImpl
    participant PD as ProjetoDaoImpl
    participant ED as EquipeDaoImpl
    participant DB as MySQL

    AP->>PES: gerarRelatorioAlocacoes()
    PES->>PED: listarTodasAlocacoes()
    PED->>DB: SELECT pt.*, p.name, e.name FROM project_teams pt...
    DB-->>PED: Dados das alocações
    
    loop Para cada alocação
        PED->>PD: buscarPorId(projetoId)
        PD-->>PED: Dados do projeto
        PED->>ED: buscarPorId(equipeId)
        ED-->>PED: Dados da equipe
    end
    
    PED-->>PES: Lista completa com detalhes
    PES-->>AP: Relatório formatado
    AP->>AP: exibirRelatorio()
```

---

### 5. **Fluxo de Notificações (Observer Pattern)**

```mermaid
sequenceDiagram
    participant Service as Qualquer Service
    participant NM as NotificationManager
    participant L1 as Listener 1 (Panel)
    participant L2 as Listener 2 (Dashboard)
    participant L3 as Listener 3 (Reports)

    Service->>NM: notificar(evento, dados)
    
    par Notificação paralela
        NM->>L1: onEventoOcorrido(dados)
        L1->>L1: atualizarInterface()
    and
        NM->>L2: onEventoOcorrido(dados)
        L2->>L2: atualizarEstatisticas()
    and
        NM->>L3: onEventoOcorrido(dados)
        L3->>L3: regenerarRelatorio()
    end
```

**Eventos Suportados:**
- `onUsuarioCadastrado(Usuario)`
- `onUsuarioAtualizado(Usuario)`
- `onProjetoCriado(Projeto)`
- `onProjetoStatusAlterado(Projeto)`
- `onTarefaCriada(Tarefa)`
- `onTarefaStatusAlterada(Tarefa)`
- `onEquipeFormada(Equipe)`
- `onAlocacaoRealizada(ProjetoId, EquipeId)`

---

### 6. **Fluxo de Inicialização do Sistema**

```mermaid
sequenceDiagram
    participant Main as Main Class
    participant DSP as DataSourceProvider
    participant AP as AppProperties
    participant HC as HikariConfig
    participant LD as LoginDialog
    participant DB as MySQL

    Main->>AP: getInstance()
    AP->>AP: carregarPropriedades()
    Main->>DSP: getInstance()
    DSP->>HC: configurarPool()
    HC->>DB: testarConexao()
    
    alt Conexão bem-sucedida
        DB-->>HC: Conexão OK
        HC-->>DSP: Pool configurado
        DSP-->>Main: DataSource pronto
        Main->>LD: new LoginDialog()
        LD-->>Main: Interface de login
    else Falha na conexão
        DB-->>HC: Erro de conexão
        HC-->>DSP: Exception
        DSP-->>Main: Erro de configuração
        Main->>Main: exibirModoPreview()
    end
```

---

### 7. **Fluxo de Validações**

#### 7.1 Validação de CPF
```mermaid
flowchart TD
    A[Entrada CPF] --> B{Formato válido?}
    B -->|Não| C[Erro: Formato inválido]
    B -->|Sim| D[Remover formatação]
    D --> E{11 dígitos?}
    E -->|Não| F[Erro: Tamanho inválido]
    E -->|Sim| G[Calcular dígitos verificadores]
    G --> H{Dígitos corretos?}
    H -->|Não| I[Erro: CPF inválido]
    H -->|Sim| J[CPF válido]
```

#### 7.2 Validação de Datas de Projeto
```mermaid
flowchart TD
    A[Datas do Projeto] --> B{Data início válida?}
    B -->|Não| C[Erro: Data início inválida]
    B -->|Sim| D{Data fim >= Data início?}
    D -->|Não| E[Erro: Data fim anterior ao início]
    D -->|Sim| F{Projeto já iniciado?}
    F -->|Sim| G{Data real <= Hoje?}
    F -->|Não| H[Validação OK]
    G -->|Não| I[Erro: Data real futura]
    G -->|Sim| H
```

---

### 8. **Fluxo de Relatórios e Dashboard**

```mermaid
sequenceDiagram
    participant MF as MainFrame
    participant PS as ProjetoService
    participant TS as TarefaService
    participant ES as EquipeService
    participant DB as MySQL

    MF->>MF: carregarDashboard()
    
    par Carregamento paralelo de dados
        MF->>PS: contarProjetos()
        PS-->>MF: Total de projetos
    and
        MF->>PS: contarProjetosPorStatus()
        PS-->>MF: Estatísticas por status
    and
        MF->>TS: contarTarefas()
        TS-->>MF: Total de tarefas
    and
        MF->>TS: contarTarefasPorStatus()
        TS-->>MF: Progresso das tarefas
    and
        MF->>ES: contarEquipes()
        ES-->>MF: Total de equipes
    end
    
    MF->>MF: atualizarWidgetsDashboard()
    MF->>MF: gerarGraficos()
```

---

## 🔧 Configurações e Otimizações

### **Pool de Conexões HikariCP**
```java
// Configuração otimizada para o sistema
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(10);           // Máximo 10 conexões
config.setMinimumIdle(2);                // Mínimo 2 conexões ociosas
config.setConnectionTimeout(30000);      // Timeout de 30s
config.setIdleTimeout(600000);           // Idle timeout de 10min
config.setMaxLifetime(1800000);          // Vida máxima de 30min
config.setLeakDetectionThreshold(60000); // Detecção de vazamentos
```

### **Transações e Integridade**
- **Autocommit**: Desabilitado para operações críticas
- **Isolation Level**: READ_COMMITTED
- **Rollback**: Automático em caso de exceções
- **Prepared Statements**: Prevenção de SQL Injection

### **Performance e Monitoramento**
- **Índices**: Criados em colunas de busca frequente
- **Logs**: Operações críticas registradas
- **Cache**: Dados de configuração em memória
- **Validação**: Client-side e server-side

Este documento detalha todos os fluxos principais do sistema, mostrando como os componentes interagem para fornecer as funcionalidades completas de gestão de projetos.