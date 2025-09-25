package br.com.projetogestao.dao;

import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação do DAO para operações de persistência de usuários.
 * 
 * Esta classe implementa o padrão DAO (Data Access Object) para fornecer
 * uma interface de acesso aos dados de usuários no banco de dados.
 * Utiliza PreparedStatements para prevenir SQL injection e gerencia
 * automaticamente as conexões com o banco.
 * 
 * Funcionalidades principais:
 * - CRUD completo de usuários
 * - Busca por login e ID
 * - Contagem de administradores
 * - Mapeamento entre objetos Java e registros do banco
 * 
 * @author Sistema de Gestão de Projetos
 * @version 1.0
 * @since 2024
 */
public class UsuarioDaoImpl implements UsuarioDao {
    
    /** DataSource para obtenção de conexões com o banco de dados */
    private final DataSource dataSource;

    /**
     * Construtor que inicializa o DAO com o DataSource fornecido.
     * 
     * @param dataSource fonte de dados para conexões com o banco (não pode ser null)
     * @throws NullPointerException se o dataSource for null
     */
    public UsuarioDaoImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource não pode ser null");
    }

    /**
     * Busca um usuário pelo seu login único.
     * 
     * Este método é frequentemente usado para autenticação e validação
     * de credenciais no sistema.
     * 
     * @param login o login do usuário a ser buscado (não pode ser null ou vazio)
     * @return Optional contendo o usuário se encontrado, Optional.empty() caso contrário
     * @throws RuntimeException se ocorrer erro na consulta
     * @throws IllegalArgumentException se o login for null ou vazio
     */
    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login não pode ser null ou vazio");
        }
        
        final String sql = "SELECT id, full_name, cpf, email, position, login, " +
                          "password_hash, role, birth_date FROM users WHERE login = ?";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, login.trim());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearResultSetParaUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuário por login: " + login, e);
        }
        
        return Optional.empty();
    }

    /**
     * Salva um usuário no banco de dados.
     * 
     * Se o usuário não possui ID (novo usuário), executa INSERT.
     * Se o usuário já possui ID (usuário existente), executa UPDATE.
     * 
     * @param usuario o usuário a ser salvo (não pode ser null)
     * @throws RuntimeException se ocorrer erro na operação de banco de dados
     * @throws NullPointerException se o usuário for null
     */
    @Override
    public void salvar(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuário não pode ser null");
        
        if (usuario.getId() == null) {
            // Inserção de novo usuário
            inserirNovoUsuario(usuario);
        } else {
            // Atualização de usuário existente
            atualizarUsuarioExistente(usuario);
        }
    }

    /**
     * Insere um novo usuário no banco de dados.
     * 
     * @param usuario o usuário a ser inserido
     * @throws RuntimeException se ocorrer erro na inserção
     */
    private void inserirNovoUsuario(Usuario usuario) {
        final String sql = "INSERT INTO users (full_name, cpf, email, position, login, " +
                          "password_hash, role, birth_date) VALUES (?,?,?,?,?,?,?,?)";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Configuração dos parâmetros do PreparedStatement
            configurarParametrosUsuario(ps, usuario);
            
            // Execução da inserção
            ps.executeUpdate();
            
            // Recuperação do ID gerado automaticamente
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getLong(1));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao inserir usuário: " + usuario.getLogin(), e);
        }
    }

    /**
     * Atualiza um usuário existente no banco de dados.
     * 
     * @param usuario o usuário a ser atualizado
     * @throws RuntimeException se ocorrer erro na atualização
     */
    private void atualizarUsuarioExistente(Usuario usuario) {
        final String sql = "UPDATE users SET full_name=?, cpf=?, email=?, position=?, " +
                          "login=?, password_hash=?, role=?, birth_date=? WHERE id=?";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Configuração dos parâmetros do PreparedStatement
            configurarParametrosUsuario(ps, usuario);
            ps.setLong(9, usuario.getId()); // ID para a cláusula WHERE
            
            // Execução da atualização
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar usuário ID: " + usuario.getId(), e);
        }
    }

    /**
     * Configura os parâmetros comuns do PreparedStatement para operações de usuário.
     * 
     * @param ps o PreparedStatement a ser configurado
     * @param usuario o usuário com os dados a serem configurados
     * @throws SQLException se ocorrer erro na configuração dos parâmetros
     */
    private void configurarParametrosUsuario(PreparedStatement ps, Usuario usuario) throws SQLException {
        ps.setString(1, usuario.getNomeCompleto());
        ps.setString(2, usuario.getCpf());
        ps.setString(3, usuario.getEmail());
        ps.setString(4, usuario.getCargo());
        ps.setString(5, usuario.getLogin());
        ps.setString(6, usuario.getSenhaHash());
        ps.setString(7, converterPerfilParaBanco(usuario.getPerfil()));
        ps.setObject(8, usuario.getDataNascimento());
    }

    /**
     * Conta o número total de administradores no sistema.
     * 
     * Este método é útil para validações de negócio, como garantir
     * que sempre existe pelo menos um administrador no sistema.
     * 
     * @return o número de administradores cadastrados
     * @throws RuntimeException se ocorrer erro na consulta
     */
    @Override
    public long contarAdministradores() {
        final String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMINISTRATOR'";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao contar administradores", e);
        }
        
        return 0;
    }

    /**
     * Busca um usuário pelo seu ID único.
     * 
     * @param id o ID do usuário a ser buscado (não pode ser null)
     * @return Optional contendo o usuário se encontrado, Optional.empty() caso contrário
     * @throws RuntimeException se ocorrer erro na consulta
     * @throws NullPointerException se o ID for null
     */
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        Objects.requireNonNull(id, "ID do usuário não pode ser null");
        
        final String sql = "SELECT id, full_name, cpf, email, position, login, " +
                          "password_hash, role, birth_date FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearResultSetParaUsuario(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuário por ID: " + id, e);
        }
        
        return Optional.empty();
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     * 
     * Os usuários são ordenados por nome completo em ordem alfabética
     * para facilitar a visualização e busca manual.
     * 
     * @return lista de todos os usuários
     * @throws RuntimeException se ocorrer erro na consulta
     */
    @Override
    public List<Usuario> listarTodos() {
        final String sql = "SELECT id, full_name, cpf, email, position, login, " +
                          "password_hash, role, birth_date FROM users ORDER BY full_name";
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearResultSetParaUsuario(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar todos os usuários", e);
        }
        
        return usuarios;
    }

    /**
     * Exclui um usuário do banco de dados.
     * 
     * ATENÇÃO: Esta operação é irreversível. Certifique-se de que
     * não existem dependências (projetos, tarefas) associadas ao usuário
     * antes de executar a exclusão.
     * 
     * @param id o ID do usuário a ser excluído (não pode ser null)
     * @throws RuntimeException se ocorrer erro na exclusão
     * @throws NullPointerException se o ID for null
     */
    @Override
    public void excluir(Long id) {
        Objects.requireNonNull(id, "ID do usuário não pode ser null");
        
        final String sql = "DELETE FROM users WHERE id=?";
        
        try (Connection conn = dataSource.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir usuário ID: " + id, e);
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Usuario.
     * 
     * Este método é responsável por converter os dados do banco de dados
     * em um objeto Usuario, tratando adequadamente valores nulos e
     * conversões de tipo.
     * 
     * @param rs o ResultSet contendo os dados do usuário
     * @return o objeto Usuario mapeado
     * @throws SQLException se ocorrer erro no acesso aos dados do ResultSet
     */
    private Usuario mapearResultSetParaUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        
        // Mapeamento dos campos básicos
        usuario.setId(rs.getLong("id"));
        usuario.setNomeCompleto(rs.getString("full_name"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setEmail(rs.getString("email"));
        usuario.setCargo(rs.getString("position"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenhaHash(rs.getString("password_hash"));
        
        // Mapeamento do perfil com conversão
        usuario.setPerfil(converterPerfilDoBanco(rs.getString("role")));
        
        // Mapeamento da data de nascimento com tratamento de nulos
        LocalDate dataNascimento = rs.getObject("birth_date", LocalDate.class);
        usuario.setDataNascimento(dataNascimento);
        
        return usuario;
    }

    /**
     * Converte um Perfil para sua representação no banco de dados.
     * 
     * @param perfil o perfil do usuário a ser convertido
     * @return a string correspondente no banco de dados
     */
    private String converterPerfilParaBanco(Perfil perfil) {
        if (perfil == null) {
            return "COLLABORATOR"; // Valor padrão
        }
        
        switch (perfil) {
            case ADMINISTRADOR:
                return "ADMINISTRATOR";
            case GERENTE:
                return "MANAGER";
            case COLABORADOR:
                return "COLLABORATOR";
            default:
                return "COLLABORATOR";
        }
    }

    /**
     * Converte uma string do banco de dados para Perfil.
     * 
     * @param perfilDb a string do banco de dados
     * @return o Perfil correspondente
     */
    private Perfil converterPerfilDoBanco(String perfilDb) {
        if (perfilDb == null) {
            return Perfil.COLABORADOR; // Valor padrão
        }
        
        switch (perfilDb) {
            case "ADMINISTRATOR":
                return Perfil.ADMINISTRADOR;
            case "MANAGER":
                return Perfil.GERENTE;
            case "COLLABORATOR":
            default:
                return Perfil.COLABORADOR;
        }
    }
}
