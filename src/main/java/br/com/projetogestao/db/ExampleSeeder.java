package br.com.projetogestao.db;

import br.com.projetogestao.model.Perfil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExampleSeeder {
    private final DataSource dataSource;

    public ExampleSeeder(DataSource dataSource) { this.dataSource = dataSource; }

    public void seed() {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Long argeuId = ensureUser(conn, "Argeu Gerente", "111.111.111-11", "argeu@example.com", "Gerente", "argeu", Perfil.GERENTE, "senha123");
                Long lucianaId = ensureUser(conn, "Luciana", "222.222.222-22", "luciana@example.com", "Colaborador", "luciana", Perfil.COLABORADOR, "senha123");
                Long adrianoId = ensureUser(conn, "Adriano", "333.333.333-33", "adriano@example.com", "Colaborador", "adriano", Perfil.COLABORADOR, "senha123");

                Long equipeId = ensureTeam(conn, "Equipe Alpha", "Equipe exemplo para testes");
                ensureTeamMember(conn, equipeId, argeuId);
                ensureTeamMember(conn, equipeId, lucianaId);
                ensureTeamMember(conn, equipeId, adrianoId);

                Long projetoId = ensureProject(conn, "Projeto Exemplo", "Projeto para demonstracao", new java.util.Date(), addDays(new java.util.Date(), 30), "IN_PROGRESS", argeuId);

                ensureProjectTeam(conn, projetoId, equipeId);

                ensureTask(conn, "Planejamento", "Planejar escopo e cronograma", projetoId, argeuId, "IN_PROGRESS");
                ensureTask(conn, "Desenvolvimento", "Desenvolver funcionalidades basicas", projetoId, lucianaId, "PENDING");
                ensureTask(conn, "Testes", "Testar funcionalidades", projetoId, adrianoId, "PENDING");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao semear dados de exemplo", e);
        }
    }

    private Long ensureUser(Connection conn, String nome, String cpf, String email, String cargo, String login, Perfil perfil, String senha) throws SQLException {
        return ensureUser(conn, nome, cpf, email, cargo, login, perfil, senha, java.time.LocalDate.of(1990, 1, 1));
    }
    
    private Long ensureUser(Connection conn, String nome, String cpf, String email, String cargo, String login, Perfil perfil, String senha, java.time.LocalDate dataNascimento) throws SQLException {
        Long id = selectLong(conn, "SELECT id FROM users WHERE login=?", login);
        String role = perfilToDb(perfil);
        String pwd = org.mindrot.jbcrypt.BCrypt.hashpw(senha, org.mindrot.jbcrypt.BCrypt.gensalt());
        if (id == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (full_name, cpf, email, position, login, password_hash, role, birth_date) VALUES (?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome); ps.setString(2, cpf); ps.setString(3, email); ps.setString(4, cargo);
                ps.setString(5, login); ps.setString(6, pwd); ps.setString(7, role);
                ps.setObject(8, dataNascimento);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET full_name=?, cpf=?, email=?, position=?, password_hash=?, role=?, birth_date=? WHERE id=?")) {
                ps.setString(1, nome); ps.setString(2, cpf); ps.setString(3, email); ps.setString(4, cargo);
                ps.setString(5, pwd); ps.setString(6, role); 
                ps.setObject(7, dataNascimento);
                ps.setLong(8, id);
                ps.executeUpdate();
            }
        }
        return selectLong(conn, "SELECT id FROM users WHERE login=?", login);
    }

    private Long ensureTeam(Connection conn, String nome, String descricao) throws SQLException {
        Long id = selectLong(conn, "SELECT id FROM teams WHERE name=?", nome);
        if (id == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO teams (name, description) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome); ps.setString(2, descricao);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
            }
        }
        return selectLong(conn, "SELECT id FROM teams WHERE name=?", nome);
    }

    private void ensureTeamMember(Connection conn, Long teamId, Long userId) throws SQLException {
        Long exists = selectLong(conn, "SELECT 1 FROM team_members WHERE team_id=? AND user_id=?", teamId, userId);
        if (exists == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO team_members (team_id, user_id) VALUES (?,?)")) {
                ps.setLong(1, teamId); ps.setLong(2, userId); ps.executeUpdate();
            }
        }
    }

    private Long ensureProject(Connection conn, String nome, String descricao, java.util.Date inicio, java.util.Date fimPrev, String statusDb, Long gerenteId) throws SQLException {
        Long id = selectLong(conn, "SELECT id FROM projects WHERE name=?", nome);
        if (id == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO projects (name, description, start_date, end_date_planned, status, manager_id) VALUES (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome); ps.setString(2, descricao);
                ps.setDate(3, new Date(inicio.getTime()));
                ps.setDate(4, new Date(fimPrev.getTime()));
                ps.setString(5, statusDb);
                ps.setLong(6, gerenteId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getLong(1); }
            }
        }
        return selectLong(conn, "SELECT id FROM projects WHERE name=?", nome);
    }

    private void ensureProjectTeam(Connection conn, Long projetoId, Long equipeId) throws SQLException {
        Long exists = selectLong(conn, "SELECT 1 FROM project_teams WHERE project_id=? AND team_id=?", projetoId, equipeId);
        if (exists == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO project_teams (project_id, team_id) VALUES (?,?)")) {
                ps.setLong(1, projetoId); ps.setLong(2, equipeId); ps.executeUpdate();
            }
        }
    }

    private void ensureTask(Connection conn, String titulo, String descricao, Long projetoId, Long responsavelId, String statusDb) throws SQLException {
        Long exists = selectLong(conn, "SELECT id FROM tasks WHERE project_id=? AND title=?", projetoId, titulo);
        if (exists == null) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tasks (title, description, project_id, assignee_id, status) VALUES (?,?,?,?,?)")) {
                ps.setString(1, titulo); ps.setString(2, descricao);
                ps.setLong(3, projetoId); ps.setLong(4, responsavelId);
                ps.setString(5, statusDb);
                ps.executeUpdate();
            }
        }
    }

    private Long selectLong(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object v = rs.getObject(1);
                    return v == null ? null : ((Number) v).longValue();
                }
                return null;
            }
        }
    }

    private String perfilToDb(Perfil perfil) {
        switch (perfil) {
            case ADMINISTRADOR: return "ADMINISTRATOR";
            case GERENTE: return "MANAGER";
            case COLABORADOR: default: return "COLLABORATOR";
        }
    }

    private java.util.Date addDays(java.util.Date date, int days) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.DATE, days);
        return cal.getTime();
    }
}


