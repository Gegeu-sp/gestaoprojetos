package br.com.projetogestao.dao;

import br.com.projetogestao.model.Tarefa;
import br.com.projetogestao.model.StatusTarefa;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaDaoImpl implements TarefaDao {
    private final DataSource dataSource;

    public TarefaDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Tarefa tarefa) {
        if (tarefa.getId() == null) {
            String sql = "INSERT INTO tasks (title, description, project_id, assignee_id, status, start_date_planned, end_date_planned, start_date_actual, end_date_actual) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, tarefa.getTitulo());
                ps.setString(2, tarefa.getDescricao());
                ps.setLong(3, tarefa.getProjetoId());
                if (tarefa.getResponsavelId() != null) ps.setLong(4, tarefa.getResponsavelId()); else ps.setNull(4, java.sql.Types.BIGINT);
                ps.setString(5, statusToDb(tarefa.getStatus()));
                ps.setDate(6, tarefa.getDataInicioPrevista() != null ? Date.valueOf(tarefa.getDataInicioPrevista()) : null);
                ps.setDate(7, tarefa.getDataTerminoPrevista() != null ? Date.valueOf(tarefa.getDataTerminoPrevista()) : null);
                ps.setDate(8, tarefa.getDataInicioReal() != null ? Date.valueOf(tarefa.getDataInicioReal()) : null);
                ps.setDate(9, tarefa.getDataTerminoReal() != null ? Date.valueOf(tarefa.getDataTerminoReal()) : null);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) tarefa.setId(keys.getLong(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao inserir tarefa", e);
            }
        } else {
            String sql = "UPDATE tasks SET title=?, description=?, project_id=?, assignee_id=?, status=?, start_date_planned=?, end_date_planned=?, start_date_actual=?, end_date_actual=? WHERE id=?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tarefa.getTitulo());
                ps.setString(2, tarefa.getDescricao());
                ps.setLong(3, tarefa.getProjetoId());
                if (tarefa.getResponsavelId() != null) ps.setLong(4, tarefa.getResponsavelId()); else ps.setNull(4, java.sql.Types.BIGINT);
                ps.setString(5, statusToDb(tarefa.getStatus()));
                ps.setDate(6, tarefa.getDataInicioPrevista() != null ? Date.valueOf(tarefa.getDataInicioPrevista()) : null);
                ps.setDate(7, tarefa.getDataTerminoPrevista() != null ? Date.valueOf(tarefa.getDataTerminoPrevista()) : null);
                ps.setDate(8, tarefa.getDataInicioReal() != null ? Date.valueOf(tarefa.getDataInicioReal()) : null);
                ps.setDate(9, tarefa.getDataTerminoReal() != null ? Date.valueOf(tarefa.getDataTerminoReal()) : null);
                ps.setLong(10, tarefa.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao atualizar tarefa", e);
            }
        }
    }

    @Override
    public Optional<Tarefa> buscarPorId(Long id) {
        String sql = "SELECT id, title, description, project_id, assignee_id, status, start_date_planned, end_date_planned, start_date_actual, end_date_actual FROM tasks WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar tarefa", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Tarefa> buscarPorProjeto(Long projetoId) {
        String sql = "SELECT id, title, description, project_id, assignee_id, status, start_date_planned, end_date_planned, start_date_actual, end_date_actual FROM tasks WHERE project_id=? ORDER BY id DESC";
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar tarefas por projeto", e);
        }
        return list;
    }

    @Override
    public List<Tarefa> buscarPorResponsavel(Long usuarioId) {
        String sql = "SELECT id, title, description, project_id, assignee_id, status, start_date_planned, end_date_planned, start_date_actual, end_date_actual FROM tasks WHERE assignee_id=? ORDER BY id DESC";
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar tarefas por responsavel", e);
        }
        return list;
    }

    @Override
    public List<Tarefa> listarTodas() {
        String sql = "SELECT id, title, description, project_id, assignee_id, status, start_date_planned, end_date_planned, start_date_actual, end_date_actual FROM tasks ORDER BY id DESC";
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar todas as tarefas", e);
        }
        return list;
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir tarefa", e);
        }
    }

    private Tarefa mapper(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getLong("id"));
        t.setTitulo(rs.getString("title"));
        t.setDescricao(rs.getString("description"));
        t.setProjetoId(rs.getLong("project_id"));
        long assignee = rs.getLong("assignee_id");
        t.setResponsavelId(rs.wasNull() ? null : assignee);
        t.setStatus(statusFromDb(rs.getString("status")));
        Date sdp = rs.getDate("start_date_planned");
        Date edp = rs.getDate("end_date_planned");
        Date sda = rs.getDate("start_date_actual");
        Date eda = rs.getDate("end_date_actual");
        t.setDataInicioPrevista(sdp != null ? sdp.toLocalDate() : null);
        t.setDataTerminoPrevista(edp != null ? edp.toLocalDate() : null);
        t.setDataInicioReal(sda != null ? sda.toLocalDate() : null);
        t.setDataTerminoReal(eda != null ? eda.toLocalDate() : null);
        return t;
    }

    private String statusToDb(StatusTarefa status) {
        if (status == null) return "PENDING";
        switch (status) {
            case PENDENTE: return "PENDING";
            case EM_EXECUCAO: return "IN_PROGRESS";
            case CONCLUIDA: return "COMPLETED";
            default: return "PENDING";
        }
    }

    private StatusTarefa statusFromDb(String db) {
        if (db == null) return StatusTarefa.PENDENTE;
        switch (db) {
            case "PENDING": return StatusTarefa.PENDENTE;
            case "IN_PROGRESS": return StatusTarefa.EM_EXECUCAO;
            case "COMPLETED": return StatusTarefa.CONCLUIDA;
            default: return StatusTarefa.PENDENTE;
        }
    }
}


