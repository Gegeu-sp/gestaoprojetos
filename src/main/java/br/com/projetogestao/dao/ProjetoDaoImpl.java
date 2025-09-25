package br.com.projetogestao.dao;

import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.StatusProjeto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetoDaoImpl implements ProjetoDao {
    private final DataSource dataSource;

    public ProjetoDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Projeto projeto) {
        if (projeto.getId() == null) {
            String sql = "INSERT INTO projects (name, description, start_date, end_date_planned, end_date_actual, status, manager_id) VALUES (?,?,?,?,?,?,?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, projeto.getNome());
                ps.setString(2, projeto.getDescricao());
                ps.setDate(3, Date.valueOf(projeto.getDataInicio()));
                ps.setDate(4, projeto.getDataTerminoPrevista() != null ? Date.valueOf(projeto.getDataTerminoPrevista()) : null);
                ps.setDate(5, projeto.getDataTerminoReal() != null ? Date.valueOf(projeto.getDataTerminoReal()) : null);
                ps.setString(6, translateStatusToDb(projeto.getStatus()));
                ps.setLong(7, projeto.getGerenteId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) projeto.setId(keys.getLong(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao inserir projeto", e);
            }
        } else {
            String sql = "UPDATE projects SET name=?, description=?, start_date=?, end_date_planned=?, end_date_actual=?, status=?, manager_id=? WHERE id=?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, projeto.getNome());
                ps.setString(2, projeto.getDescricao());
                ps.setDate(3, Date.valueOf(projeto.getDataInicio()));
                ps.setDate(4, projeto.getDataTerminoPrevista() != null ? Date.valueOf(projeto.getDataTerminoPrevista()) : null);
                ps.setDate(5, projeto.getDataTerminoReal() != null ? Date.valueOf(projeto.getDataTerminoReal()) : null);
                ps.setString(6, translateStatusToDb(projeto.getStatus()));
                ps.setLong(7, projeto.getGerenteId());
                ps.setLong(8, projeto.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Falha ao atualizar projeto", e);
            }
        }
    }

    @Override
    public Optional<Projeto> buscarPorId(Long id) {
        String sql = "SELECT id, name, description, start_date, end_date_planned, end_date_actual, status, manager_id FROM projects WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar projeto", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Projeto> listarTodos() {
        String sql = "SELECT id, name, description, start_date, end_date_planned, end_date_actual, status, manager_id FROM projects ORDER BY start_date DESC";
        List<Projeto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar projetos", e);
        }
        return list;
    }

    @Override
    public void excluir(Long id) {
        String sql = "DELETE FROM projects WHERE id=?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir projeto", e);
        }
    }

    private Projeto mapper(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("name"));
        p.setDescricao(rs.getString("description"));
        Date sd = rs.getDate("start_date");
        Date edp = rs.getDate("end_date_planned");
        Date eda = rs.getDate("end_date_actual");
        p.setDataInicio(sd != null ? sd.toLocalDate() : LocalDate.now());
        p.setDataTerminoPrevista(edp != null ? edp.toLocalDate() : null);
        p.setDataTerminoReal(eda != null ? eda.toLocalDate() : null);
        p.setStatus(translateStatusFromDb(rs.getString("status")));
        p.setGerenteId(rs.getLong("manager_id"));
        return p;
    }

    private String translateStatusToDb(StatusProjeto status) {
        if (status == null) return "PLANNED";
        switch (status) {
            case PLANEJADO: return "PLANNED";
            case EM_ANDAMENTO: return "IN_PROGRESS";
            case CONCLUIDO: return "COMPLETED";
            case CANCELADO: return "CANCELED";
            default: return "PLANNED";
        }
    }

    private StatusProjeto translateStatusFromDb(String db) {
        if (db == null) return StatusProjeto.PLANEJADO;
        switch (db) {
            case "PLANNED": return StatusProjeto.PLANEJADO;
            case "IN_PROGRESS": return StatusProjeto.EM_ANDAMENTO;
            case "COMPLETED": return StatusProjeto.CONCLUIDO;
            case "CANCELED": return StatusProjeto.CANCELADO;
            default: return StatusProjeto.PLANEJADO;
        }
    }
}


