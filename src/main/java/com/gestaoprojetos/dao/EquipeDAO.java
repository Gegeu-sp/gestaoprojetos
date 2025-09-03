package com.gestaoprojetos.dao;

import com.gestaoprojetos.model.Equipe;
import com.gestaoprojetos.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {
    private Connection connection;
    private UsuarioDAO usuarioDAO;

    public EquipeDAO(Connection connection) {
        this.connection = connection;
        this.usuarioDAO = new UsuarioDAO(connection);
    }

    public void inserir(Equipe equipe) throws SQLException {
        String sql = "INSERT INTO Equipe (nome, descricao) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipe.setId(rs.getInt(1));
                }
            }
        }
        // Inserir membros da equipe
        inserirMembros(equipe);
    }

    public Equipe buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Equipe WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);
                    equipe.setMembros(listarMembros(id));
                    return equipe;
                }
            }
        }
        return null;
    }

    public List<Equipe> listarTodos() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Equipe equipe = mapResultSetToEquipe(rs);
                equipe.setMembros(listarMembros(equipe.getId()));
                equipes.add(equipe);
            }
        }
        return equipes;
    }

    public void atualizar(Equipe equipe) throws SQLException {
        String sql = "UPDATE Equipe SET nome = ?, descricao = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setInt(3, equipe.getId());

            stmt.executeUpdate();
        }

        // Remover membros da equipe Todos
        removerTodosMembros(equipe.getId());
        inserirMembros(equipe);
    }

    Public void excluir(int id) throws SQLException {
        removerTodosMembros(id);

        String sql = "DELETE FROM Equipe WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void inserirMembros(Equipe equipe) throws SQLException {
        String sql = "INSERT INTO Equipe_Membros (equipe_id, usuario_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Usuario membro : equipe.getMembros()) {
                stmt.setInt(1, equipe.getId());
                stmt.setInt(2, membro.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<Usuario> listarMembros(int equipeId) throws SQLException {
        String sql = "SELECT u.* FROM Usuario u " +
                     "INNER JOIN Equipe_Membro em O u.id = em.usuario_id " +
                     "WHERE em.equipe_id = ?";
        List<Usuario> membros = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Membros.add(usuarioDAO.mapResultSetToUsuario(rs));
                }
            }
        }
        return membros;
    }

    private void removerTodosMembros(int equipeId) throws SQLException {
        String sql = "DELETE FROM Equipe_Membros WHERE equipe_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipeId);
            stmt.executeUpdate();
        }
    }

    private Equipe mapResultSetToEquipe(ResultSet rs) throws SQLException {
        Equipe equipe = new Equipe();
        equipe.setId(rs.getInt("id"));
        equipe.setNome(rs.getString("nome"));
        equipe.setDescricao(rs.getString("descricao"));
        return equipe;
    }
}

