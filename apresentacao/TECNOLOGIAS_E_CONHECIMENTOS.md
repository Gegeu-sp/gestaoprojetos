# 🛠️ Tecnologias Utilizadas e Conhecimentos Adquiridos

## 📋 Índice
1. [Stack Tecnológica Completa](#stack-tecnológica-completa)
2. [Conhecimentos de Programação](#conhecimentos-de-programação)
3. [Banco de Dados](#banco-de-dados)
4. [Interface Gráfica](#interface-gráfica)
5. [Segurança](#segurança)
6. [DevOps e Ferramentas](#devops-e-ferramentas)
7. [Padrões de Projeto](#padrões-de-projeto)
8. [Competências Desenvolvidas](#competências-desenvolvidas)

---

## 🛠️ Stack Tecnológica Completa

### Backend e Core
| Tecnologia | Versão | Propósito | Conhecimentos Adquiridos |
|------------|--------|-----------|--------------------------|
| **Java** | 11+ | Linguagem principal | POO avançada, Streams, Lambda expressions, Optional |
| **Maven** | 3.11.0 | Build e dependências | Ciclo de vida, plugins, profiles, dependency management |
| **JDBC** | Nativo | Conectividade BD | Connection pooling, prepared statements, transações |

### Banco de Dados
| Tecnologia | Versão | Propósito | Conhecimentos Adquiridos |
|------------|--------|-----------|--------------------------|
| **MySQL** | 8.3.0 | SGBD principal | Modelagem relacional, otimização, índices, constraints |
| **HikariCP** | 5.1.0 | Pool de conexões | Configuração de pool, performance tuning |

### Interface Gráfica
| Tecnologia | Versão | Propósito | Conhecimentos Adquiridos |
|------------|--------|-----------|--------------------------|
| **Java Swing** | Nativo | GUI Desktop | Layouts avançados, eventos, componentes customizados |
| **AWT** | Nativo | Componentes base | Event handling, painting, threading |

### Segurança
| Tecnologia | Versão | Propósito | Conhecimentos Adquiridos |
|------------|--------|-----------|--------------------------|
| **BCrypt** | 0.4 | Hash de senhas | Criptografia, salt, verificação segura |
| **SLF4J** | 2.0.13 | Logging | Níveis de log, configuração, debugging |

### DevOps e Containerização
| Tecnologia | Versão | Propósito | Conhecimentos Adquiridos |
|------------|--------|-----------|--------------------------|
| **Docker** | Latest | Containerização | Dockerfile, imagens, volumes, networks |
| **Docker Compose** | Latest | Orquestração | Multi-container, services, dependencies |

---

## 💻 Conhecimentos de Programação

### Programação Orientada a Objetos (POO)

#### 🔒 Encapsulamento
**Conceito Aplicado:**
- Atributos privados com getters/setters
- Validação de dados nos métodos de acesso
- Proteção da integridade dos objetos

**Exemplo Prático:**
```java
public class Usuario {
    private String cpf;
    private String senha;
    
    public void setCpf(String cpf) {
        if (validarCpf(cpf)) {
            this.cpf = formatarCpf(cpf);
        } else {
            throw new IllegalArgumentException("CPF inválido");
        }
    }
}
```

#### 🧬 Herança
**Conceito Aplicado:**
- Hierarquia de classes para diferentes tipos de usuários
- Reutilização de código comum
- Especialização de comportamentos

**Exemplo Prático:**
```java
public abstract class BaseDao<T> {
    protected Connection getConnection() { /* implementação comum */ }
    public abstract void salvar(T entity);
}

public class UsuarioDaoImpl extends BaseDao<Usuario> {
    @Override
    public void salvar(Usuario usuario) { /* implementação específica */ }
}
```

#### 🎭 Polimorfismo
**Conceito Aplicado:**
- Interfaces para diferentes implementações
- Métodos sobrescritos para comportamentos específicos
- Tratamento uniforme de objetos diferentes

**Exemplo Prático:**
```java
public interface UsuarioListener {
    void onUsuarioCadastrado(Usuario usuario);
}

// Diferentes painéis implementam a mesma interface
public class EquipesPanel implements UsuarioListener { /* ... */ }
public class RelatoriosPanel implements UsuarioListener { /* ... */ }
```

#### 🎨 Abstração
**Conceito Aplicado:**
- Classes abstratas para funcionalidades comuns
- Interfaces para contratos bem definidos
- Separação entre interface e implementação

### Tratamento de Exceções
**Conhecimentos Aplicados:**
- Try-catch estruturado
- Exceções customizadas
- Logging de erros
- Rollback de transações

**Exemplo Prático:**
```java
public void salvarUsuario(Usuario usuario) throws ServiceException {
    try {
        validarDados(usuario);
        usuarioDao.salvar(usuario);
        notificarListeners(usuario);
    } catch (SQLException e) {
        logger.error("Erro ao salvar usuário", e);
        throw new ServiceException("Erro interno do sistema", e);
    }
}
```

### Programação Funcional (Java 8+)
**Conceitos Aplicados:**
- Streams para processamento de coleções
- Lambda expressions para código conciso
- Optional para tratamento de nulos
- Method references

**Exemplo Prático:**
```java
List<Projeto> projetosAtrasados = projetos.stream()
    .filter(p -> p.getDataFim().isBefore(LocalDate.now()))
    .filter(p -> !p.getStatus().equals(StatusProjeto.CONCLUIDO))
    .collect(Collectors.toList());
```

---

## 🗄️ Banco de Dados

### Modelagem Relacional
**Conhecimentos Aplicados:**

#### Entidades Principais
1. **usuarios** - Dados pessoais e autenticação
2. **projetos** - Informações de projetos
3. **equipes** - Grupos de trabalho
4. **tarefas** - Atividades do projeto
5. **projeto_equipe** - Relacionamento N:N

#### Relacionamentos
- **1:N** - Usuario → Projetos (um usuário pode gerenciar vários projetos)
- **N:N** - Projetos ↔ Equipes (via tabela projeto_equipe)
- **N:1** - Tarefas → Projetos (várias tarefas por projeto)
- **N:1** - Tarefas → Usuarios (responsável pela tarefa)

#### Normalização
- **1FN** - Eliminação de grupos repetitivos
- **2FN** - Eliminação de dependências parciais
- **3FN** - Eliminação de dependências transitivas

### SQL Avançado
**Técnicas Aplicadas:**

#### Joins Complexos
```sql
SELECT p.nome as projeto, u.nome as responsavel, 
       COUNT(t.id) as total_tarefas,
       SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as concluidas
FROM projetos p
LEFT JOIN tarefas t ON p.id = t.projeto_id
LEFT JOIN usuarios u ON p.responsavel_id = u.id
GROUP BY p.id, p.nome, u.nome;
```

#### Subqueries
```sql
SELECT * FROM projetos 
WHERE id IN (
    SELECT DISTINCT projeto_id 
    FROM tarefas 
    WHERE data_fim_real > data_fim_prevista
);
```

### Otimização e Performance
**Técnicas Implementadas:**
- **Índices** em chaves estrangeiras e campos de busca
- **Prepared Statements** para prevenção de SQL Injection
- **Connection Pooling** com HikariCP
- **Transações** para consistência de dados

### Integridade de Dados
**Controles Implementados:**
- **Chaves primárias** auto-incrementais
- **Chaves estrangeiras** com cascade
- **Constraints** de validação (NOT NULL, UNIQUE)
- **Triggers** para auditoria (se necessário)

---

## 🖥️ Interface Gráfica

### Java Swing Avançado
**Componentes Utilizados:**

#### Layouts Managers
- **BorderLayout** - Layout principal das janelas
- **GridBagLayout** - Formulários complexos
- **FlowLayout** - Botões e controles simples
- **BoxLayout** - Arranjos verticais/horizontais

#### Componentes Customizados
- **JTable** com modelos customizados
- **JComboBox** com dados dinâmicos
- **JDateChooser** para seleção de datas
- **JFormattedTextField** para CPF e formatações

### Event Handling
**Padrões Implementados:**
- **ActionListener** para botões
- **DocumentListener** para campos de texto
- **TableModelListener** para tabelas
- **WindowListener** para eventos de janela

### UX/UI Design
**Princípios Aplicados:**

#### Usabilidade
- **Feedback visual** para ações do usuário
- **Mensagens claras** em português
- **Validação em tempo real**
- **Navegação intuitiva**

#### Acessibilidade
- **Atalhos de teclado** (mnemonics)
- **Ordem de tabulação** lógica
- **Cores contrastantes**
- **Ícones descritivos**

#### Responsividade
- **Layouts flexíveis** que se adaptam
- **Componentes redimensionáveis**
- **Scrollbars** quando necessário

### Internacionalização
**Implementações:**
- **Textos em português** brasileiro
- **Formato de data** dd/MM/yyyy
- **Formatação de CPF** xxx.xxx.xxx-xx
- **Timezone** America/Sao_Paulo

---

## 🔐 Segurança

### Criptografia de Senhas
**BCrypt Implementation:**
```java
public class AuthService {
    private static final int SALT_ROUNDS = 12;
    
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }
    
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

**Conhecimentos Adquiridos:**
- **Salt** para prevenção de rainbow tables
- **Cost factor** para ajuste de performance/segurança
- **Verificação segura** sem exposição de dados

### Prevenção de SQL Injection
**Prepared Statements:**
```java
public Usuario buscarPorLogin(String login) throws SQLException {
    String sql = "SELECT * FROM usuarios WHERE login = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, login);
        ResultSet rs = stmt.executeQuery();
        // processamento...
    }
}
```

### Controle de Acesso
**Sistema de Perfis:**
```java
public enum PerfilUsuario {
    ADMIN("Administrador", Arrays.asList("*")),
    GERENTE("Gerente", Arrays.asList("projetos", "equipes", "relatorios")),
    DESENVOLVEDOR("Desenvolvedor", Arrays.asList("tarefas", "projetos:read"));
}
```

### Validação de Entrada
**Sanitização de Dados:**
- **Regex** para validação de CPF
- **Trim** para remoção de espaços
- **Escape** de caracteres especiais
- **Validação de tipos** e ranges

---

## 🔧 DevOps e Ferramentas

### Docker e Containerização
**Conhecimentos Adquiridos:**

#### Dockerfile
```dockerfile
FROM openjdk:11-jre-slim
COPY target/gestao-projetos.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Docker Compose
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.3.0
    environment:
      MYSQL_DATABASE: gestao_projetos
      MYSQL_USER: gp
      MYSQL_PASSWORD: gp123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
```

### Maven Build System
**Configurações Avançadas:**

#### Plugins Utilizados
- **maven-compiler-plugin** - Compilação Java 11
- **maven-shade-plugin** - Fat JAR com dependências
- **exec-maven-plugin** - Execução da aplicação
- **maven-surefire-plugin** - Execução de testes

#### Profiles
```xml
<profiles>
    <profile>
        <id>development</id>
        <properties>
            <db.url>jdbc:mysql://localhost:3306/gestao_projetos</db.url>
        </properties>
    </profile>
    <profile>
        <id>production</id>
        <properties>
            <db.url>jdbc:mysql://mysql:3306/gestao_projetos</db.url>
        </properties>
    </profile>
</profiles>
```

### Scripts de Automação
**Conhecimentos em Shell/Batch:**

#### Windows (Batch)
```batch
@echo off
echo Iniciando aplicacao...
docker-compose up mysql -d
timeout /t 10
mvn clean compile exec:java
```

#### Linux/macOS (Shell)
```bash
#!/bin/bash
echo "Iniciando aplicação..."
docker-compose up mysql -d
sleep 10
mvn clean compile exec:java
```

### Logging e Monitoramento
**SLF4J Configuration:**
```java
private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

public void salvarUsuario(Usuario usuario) {
    logger.info("Iniciando salvamento do usuário: {}", usuario.getLogin());
    try {
        // operação...
        logger.info("Usuário salvo com sucesso: {}", usuario.getId());
    } catch (Exception e) {
        logger.error("Erro ao salvar usuário: {}", usuario.getLogin(), e);
        throw e;
    }
}
```

---

## 🏗️ Padrões de Projeto

### DAO (Data Access Object)
**Implementação:**
```java
public interface UsuarioDao {
    void salvar(Usuario usuario) throws SQLException;
    Usuario buscarPorId(Long id) throws SQLException;
    List<Usuario> listarTodos() throws SQLException;
    void atualizar(Usuario usuario) throws SQLException;
    void excluir(Long id) throws SQLException;
}

public class UsuarioDaoImpl implements UsuarioDao {
    private final DataSource dataSource;
    
    // implementações específicas...
}
```

**Benefícios Obtidos:**
- **Abstração** do acesso a dados
- **Testabilidade** com mocks
- **Flexibilidade** para trocar implementações
- **Separação de responsabilidades**

### MVC (Model-View-Controller)
**Estrutura Implementada:**

#### Model
```java
public class Usuario {
    private Long id;
    private String nome;
    private String login;
    // getters/setters, validações...
}
```

#### View
```java
public class UsuariosPanel extends JPanel {
    private JTable tabelaUsuarios;
    private JButton btnNovo, btnEditar, btnExcluir;
    // componentes da interface...
}
```

#### Controller/Service
```java
public class UsuarioService {
    private UsuarioDao usuarioDao;
    
    public void salvarUsuario(Usuario usuario) throws ServiceException {
        // validações e regras de negócio
        usuarioDao.salvar(usuario);
        // notificações
    }
}
```

### Observer Pattern
**Sistema de Notificações:**
```java
public interface UsuarioListener {
    void onUsuarioCadastrado(Usuario usuario);
    void onUsuarioAtualizado(Usuario usuario);
    void onUsuarioExcluido(Long usuarioId);
}

public class UsuarioNotificationManager {
    private List<UsuarioListener> listeners = new ArrayList<>();
    
    public void addListener(UsuarioListener listener) {
        listeners.add(listener);
    }
    
    public void notificarUsuarioCadastrado(Usuario usuario) {
        listeners.forEach(listener -> listener.onUsuarioCadastrado(usuario));
    }
}
```

### Singleton Pattern
**Gerenciadores Únicos:**
```java
public class DataSourceProvider {
    private static DataSourceProvider instance;
    private HikariDataSource dataSource;
    
    private DataSourceProvider() {
        initializeDataSource();
    }
    
    public static synchronized DataSourceProvider getInstance() {
        if (instance == null) {
            instance = new DataSourceProvider();
        }
        return instance;
    }
}
```

### Factory Pattern
**Criação de Objetos:**
```java
public class DaoFactory {
    public static UsuarioDao createUsuarioDao() {
        return new UsuarioDaoImpl(DataSourceProvider.getInstance().getDataSource());
    }
    
    public static ProjetoDao createProjetoDao() {
        return new ProjetoDaoImpl(DataSourceProvider.getInstance().getDataSource());
    }
}
```

---

## 🎓 Competências Desenvolvidas

### Competências Técnicas

#### Programação
- ✅ **Java Avançado** - Streams, Lambda, Optional, Generics
- ✅ **POO Completa** - Encapsulamento, Herança, Polimorfismo, Abstração
- ✅ **Padrões de Projeto** - DAO, MVC, Observer, Singleton, Factory
- ✅ **Tratamento de Exceções** - Try-catch, exceções customizadas
- ✅ **Programação Funcional** - Streams, Lambda expressions

#### Banco de Dados
- ✅ **Modelagem Relacional** - Entidades, relacionamentos, normalização
- ✅ **SQL Avançado** - Joins, subqueries, funções agregadas
- ✅ **Otimização** - Índices, prepared statements, connection pooling
- ✅ **Integridade** - Constraints, chaves estrangeiras, transações

#### Interface Gráfica
- ✅ **Swing Avançado** - Layouts, eventos, componentes customizados
- ✅ **UX/UI Design** - Usabilidade, acessibilidade, responsividade
- ✅ **Internacionalização** - Localização, formatação regional

#### DevOps
- ✅ **Docker** - Containerização, imagens, volumes
- ✅ **Docker Compose** - Orquestração multi-container
- ✅ **Maven** - Build, dependências, plugins, profiles
- ✅ **Scripts** - Automação em Batch e Shell

### Competências Profissionais

#### Resolução de Problemas
- **Debugging** sistemático e eficiente
- **Troubleshooting** de problemas complexos
- **Análise de logs** para identificação de erros
- **Pesquisa** em documentação e comunidades

#### Documentação
- **Código limpo** e bem comentado
- **JavaDoc** para documentação de APIs
- **README** completo e estruturado
- **Comentários** explicativos em português

#### Metodologia
- **Desenvolvimento incremental** módulo por módulo
- **Versionamento** com Git (commits descritivos)
- **Testes manuais** sistemáticos
- **Refatoração** contínua do código

#### Organização
- **Estrutura de projeto** bem definida
- **Separação de responsabilidades** clara
- **Nomenclatura** consistente e descritiva
- **Configuração** flexível e documentada

### Soft Skills Desenvolvidas

#### Planejamento
- **Análise de requisitos** detalhada
- **Arquitetura** bem estruturada
- **Cronograma** realista de desenvolvimento
- **Priorização** de funcionalidades

#### Persistência
- **Resolução de bugs** complexos
- **Aprendizado** de novas tecnologias
- **Superação** de desafios técnicos
- **Melhoria contínua** do código

#### Comunicação
- **Documentação** clara e objetiva
- **Comentários** explicativos no código
- **Mensagens** de commit descritivas
- **Interface** intuitiva para usuários

---

## 📊 Métricas de Aprendizado

### Conhecimentos Quantificados
- **25+ classes** implementadas com POO
- **4 padrões de projeto** aplicados na prática
- **5 entidades** modeladas no banco de dados
- **6 módulos** integrados no sistema
- **7 interfaces gráficas** desenvolvidas
- **~3.000 linhas** de código Java estruturado

### Tecnologias Dominadas
- **100%** - Java, Swing, MySQL, Maven
- **90%** - Docker, SQL avançado, Padrões de projeto
- **80%** - BCrypt, SLF4J, HikariCP
- **70%** - Docker Compose, Scripts de automação

### Tempo de Desenvolvimento
- **Planejamento e Arquitetura**: 20% do tempo
- **Desenvolvimento Core**: 50% do tempo
- **Interface Gráfica**: 20% do tempo
- **Testes e Correções**: 10% do tempo

---

## 🚀 Próximos Passos de Aprendizado

### Tecnologias para Evoluir
1. **Spring Framework** - Para desenvolvimento enterprise
2. **JUnit/Mockito** - Para testes automatizados
3. **REST APIs** - Para integração com outros sistemas
4. **React/Angular** - Para interfaces web modernas
5. **Microservices** - Para arquiteturas distribuídas

### Conceitos para Aprofundar
1. **Design Patterns** avançados (Strategy, Command, etc.)
2. **Clean Architecture** e SOLID principles
3. **CI/CD** com GitHub Actions ou Jenkins
4. **Monitoring** com Prometheus/Grafana
5. **Cloud Computing** (AWS, Azure, GCP)

### Certificações Relevantes
1. **Oracle Certified Professional Java SE**
2. **MySQL Database Administrator**
3. **Docker Certified Associate**
4. **AWS Cloud Practitioner**
5. **Scrum Master Certification**

---

**Este documento representa o conhecimento técnico adquirido durante o desenvolvimento do Sistema de Gestão de Projetos, demonstrando competência em desenvolvimento de software completo e profissional.**
