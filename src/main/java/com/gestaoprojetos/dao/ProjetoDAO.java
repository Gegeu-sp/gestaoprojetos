package com.gestaoprojetos.dao;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {
    private Connection connection;
    
    public ProjetoDAO(Connection connection) {
        this.connection = connection;
    }
    // Novo Projeto no Banco de Dados
    public void inserir(Projeto projeto) throws SQLException {
        String sql = "INSERT INTO Projeto (nome, descricao, data_inicio, data_termino_prevista, status, gerente_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrevista()));
            stmt.setString(5, projeto.getStatus().name());
            stmt.setInt(6, projeto.getGerente().getId());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    projeto.setId(rs.getInt(1));
                }
            }
        }
    }
    // Buscar Projeto por ID
    public Projeto buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome FROM Projeto p " +
                     "LEFT JOIN Usuario u ON p.gerente_id = u.id WHERE p.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProjeto(rs);
                }
            }
        }
        return null;
    }
    // Listar todos os Projetos
    public List<Projeto> listarTodos() throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome FROM Projeto p " +
                     "LEFT JOIN Usuario u ON p.gerente_id = u.id ORDER BY p.nome";
        List<Projeto> projetos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                projetos.add(mapResultSetToProjeto(rs));
            }
        }
        return projetos;
    }
    // Listar Projetos por Gerente
    public List<Projeto> listarPorGerente(int gerenteId) throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome FROM Projeto p " +
                     "LEFT JOIN Usuario u ON p.gerente_id = u.id WHERE p.gerente_id = ? ORDER BY p.nome";
        List<Projeto> projetos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, gerenteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projetos.add(mapResultSetToProjeto(rs));
                }
            }
        }
        return projetos;
    }
    // Listar Projetos com Risco de Atraso
    public List<Projeto> listarComRiscoAtraso() throws SQLException {
        String sql = "SELECT p.*, u.nome_completo as gerente_nome FROM Projeto p " +
                     "LEFT JOIN Usuario u ON p.gerente_id = u.id " +
                     "WHERE p.status = 'EM_ANDAMENTO' AND p.data_termino_prevista < CURDATE() " +
                     "ORDER BY p.data_termino_prevista";
        List<Projeto> projetos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                projetos.add(mapResultSetToProjeto(rs));
            }
        }
        return projetos;
    }
    // Atualizar Projeto
    public void atualizar(Projeto projeto) throws SQLException {
        String sql = "UPDATE Projeto SET nome = ?, descricao = ?, data_inicio = ?, " +
                     "data_termino_prevista = ?, data_termino_real = ?, status = ?, gerente_id = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrevista()));
            
            if (projeto.getDataTerminoReal() != null) {
                stmt.setDate(5, Date.valueOf(projeto.getDataTerminoReal()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, projeto.getStatus().name());
            stmt.setInt(7, projeto.getGerente().getId());
            stmt.setInt(8, projeto.getId());
            
            stmt.executeUpdate();
        }
    }
    // Excluir Projeto
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Projeto WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    // Mapear ResultSet para Projeto
    private Projeto mapResultSetToProjeto(ResultSet rs) throws SQLException {
        Projeto projeto = new Projeto();
        projeto.setId(rs.getInt("id"));
        projeto.setNome(rs.getString("nome"));
        projeto.setDescricao(rs.getString("descricao"));
        projeto.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        projeto.setDataTerminoPrevista(rs.getDate("data_termino_prevista").toLocalDate());
        
        Date dataTerminoReal = rs.getDate("data_termino_real");
        if (dataTerminoReal != null) {
            projeto.setDataTerminoReal(dataTerminoReal.toLocalDate());
        }
        
        projeto.setStatus(Projeto.Status.valueOf(rs.getString("status")));
        
        // Cria um objeto Usuario para o gerente
        Usuario gerente = new Usuario();
        gerente.setId(rs.getInt("gerente_id"));
        gerente.setNomeCompleto(rs.getString("gerente_nome"));
        projeto.setGerente(gerente);
        
        return projeto;
    }
}