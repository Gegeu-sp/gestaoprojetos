package com.gestaoprojetos.service;

import com.gestaoprojetos.dao.UsuarioDAO;
import com.gestaoprojetos.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {
    private UsuarioDAO usuarioDAO;
    
    public AuthService(Connection connection) {
        this.usuarioDAO = new UsuarioDAO(connection);
    }
    
    public Usuario autenticar(String login, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }
    
    public String gerarHashSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }
    
    public boolean senhaForte(String senha) {
        // Pelo menos 8 caracteres, uma letra maiúscula, uma minúscula e um número
        return senha.length() >= 8 && 
               senha.matches(".*[A-Z].*") && 
               senha.matches(".*[a-z].*") && 
               senha.matches(".*\\d.*");
    }
}