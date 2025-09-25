package br.com.projetogestao.dao;

import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDaoImpl implements UsuarioDao {
    private final DataSource dataSource;

    public UsuarioDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT id, full_name, cpf, email, position, login, password_hash, role, birth_date FROM users WHERE login = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuario por login", e);
        }
        return Optional.empty();
    }

    @Override
    public void salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            String sql = "INSERT INTO users (full_name, cpf, email, position, login, password_hash, role, birth_date) VALUES (?,?,?,?,?,?,?,?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNomeCompleto());
                ps.setString(2, usuario.getCpf());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, usuario.getCargo());
                ps.setString(5, usuario.getLogin());
                ps.setString(6, usuario.getSenhaHash());
                ps.setString(7, perfilToDb(usuario.getPerfil()));
                ps.setObject(8, usuario.getDataNascimento());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        usuario.setId(keys.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao inserir usuario", e);
            }
        } else {
            String sql = "UPDATE users SET full_name=?, cpf=?, email=?, position=?, login=?, password_hash=?, role=?, birth_date=? WHERE id=?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario.getNomeCompleto());
                ps.setString(2, usuario.getCpf());
                ps.setString(3, usuario.getEmail());
                ps.setString(4, usuario.getCargo());
                ps.setString(5, usuario.getLogin());
                ps.setString(6, usuario.getSenhaHash());
                ps.setString(7, perfilToDb(usuario.getPerfil()));
                ps.setObject(8, usuario.getDataNascimento());
                ps.setLong(9, usuario.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao atualizar usuario", e);
            }
        }
    }

    @Override
    public long contarAdministradores() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMINISTRATOR'";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao contar administradores", e);
        }
        return 0;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT id, full_name, cpf, email, position, login, password_hash, role, birth_date FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuario por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT id, full_name, cpf, email, position, login, password_hash, role, birth_date FROM users ORDER BY full_name";
        List<Usuario> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar usuarios", e);
        }
        return list;
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir usuario", e);
        }
    }

    private Usuario mapper(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNomeCompleto(rs.getString("full_name"));
        u.setCpf(rs.getString("cpf"));
        u.setEmail(rs.getString("email"));
        u.setCargo(rs.getString("position"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("password_hash"));
        u.setPerfil(perfilFromDb(rs.getString("role")));
        u.setDataNascimento(rs.getObject("birth_date", java.time.LocalDate.class));
        return u;
    }

    private String perfilToDb(Perfil perfil) {
        if (perfil == null) return "COLLABORATOR";
        switch (perfil) {
            case ADMINISTRADOR: return "ADMINISTRATOR";
            case GERENTE: return "MANAGER";
            case COLABORADOR: return "COLLABORATOR";
            default: return "COLLABORATOR";
        }
    }

    private Perfil perfilFromDb(String db) {
        if (db == null) return Perfil.COLABORADOR;
        switch (db) {
            case "ADMINISTRATOR": return Perfil.ADMINISTRADOR;
            case "MANAGER": return Perfil.GERENTE;
            case "COLLABORATOR":
            default: return Perfil.COLABORADOR;
        }
    }
}


