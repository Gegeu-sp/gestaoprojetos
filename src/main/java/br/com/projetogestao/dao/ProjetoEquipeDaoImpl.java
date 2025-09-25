package br.com.projetogestao.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjetoEquipeDaoImpl implements ProjetoEquipeDao {
    private final DataSource dataSource;

    public ProjetoEquipeDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void atribuirEquipeAoProjeto(Long projetoId, Long equipeId) {
        String sql = "INSERT IGNORE INTO project_teams (project_id, team_id) VALUES (?,?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projetoId);
            ps.setLong(2, equipeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atribuir equipe ao projeto", e);
        }
    }

    @Override
    public void removerEquipeDoProjeto(Long projetoId, Long equipeId) {
        String sql = "DELETE FROM project_teams WHERE project_id=? AND team_id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projetoId);
            ps.setLong(2, equipeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao remover equipe do projeto", e);
        }
    }

    @Override
    public List<Long> listarEquipesPorProjeto(Long projetoId) {
        String sql = "SELECT team_id FROM project_teams WHERE project_id=?";
        List<Long> teams = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) teams.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar equipes do projeto", e);
        }
        return teams;
    }

    @Override
    public List<Long> listarProjetosPorEquipe(Long equipeId) {
        String sql = "SELECT project_id FROM project_teams WHERE team_id=?";
        List<Long> projects = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, equipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) projects.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar projetos da equipe", e);
        }
        return projects;
    }
}


