package br.com.projetogestao.dao;

import br.com.projetogestao.model.Equipe;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipeDaoImpl implements EquipeDao {
    private final DataSource dataSource;

    public EquipeDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Equipe equipe) {
        if (equipe.getId() == null) {
            String sql = "INSERT INTO teams (name, description) VALUES (?,?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, equipe.getNome());
                ps.setString(2, equipe.getDescricao());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) equipe.setId(keys.getLong(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao inserir equipe", e);
            }
        } else {
            String sql = "UPDATE teams SET name=?, description=? WHERE id=?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, equipe.getNome());
                ps.setString(2, equipe.getDescricao());
                ps.setLong(3, equipe.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao atualizar equipe", e);
            }
        }
    }

    @Override
    public Optional<Equipe> buscarPorId(Long id) {
        String sql = "SELECT id, name, description FROM teams WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar equipe", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Equipe> listarTodas() {
        String sql = "SELECT id, name, description FROM teams ORDER BY name";
        List<Equipe> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar equipes", e);
        }
        return list;
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM teams WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir equipe", e);
        }
    }

    @Override
    public void adicionarMembro(Long equipeId, Long usuarioId) {
        String sql = "INSERT IGNORE INTO team_members (team_id, user_id) VALUES (?,?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, equipeId);
            ps.setLong(2, usuarioId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao adicionar membro", e);
        }
    }

    @Override
    public void removerMembro(Long equipeId, Long usuarioId) {
        String sql = "DELETE FROM team_members WHERE team_id=? AND user_id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, equipeId);
            ps.setLong(2, usuarioId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao remover membro", e);
        }
    }

    @Override
    public List<Long> listarMembros(Long equipeId) {
        String sql = "SELECT user_id FROM team_members WHERE team_id=?";
        List<Long> members = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, equipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) members.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar membros", e);
        }
        return members;
    }

    private Equipe mapper(ResultSet rs) throws SQLException {
        Equipe t = new Equipe();
        t.setId(rs.getLong("id"));
        t.setNome(rs.getString("name"));
        t.setDescricao(rs.getString("description"));
        return t;
    }
}


