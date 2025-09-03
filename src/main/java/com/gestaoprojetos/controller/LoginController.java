package com.gestaoprojetos.controller;

import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagem;
    
    private AuthService authService;
    
    public LoginController(Connection connection) {
        this.authService = new AuthService(connection);
    }
    
    @FXML
    private void handleLogin() {
        try {
            // Valida campos vazios
            if (txtLogin.getText().isEmpty() || txtSenha.getText().isEmpty()) {
                lblMensagem.setText("Por favor, preencha todos os campos!");
                return;
            }
            
            // Tenta autenticar o usuário
            Usuario usuario = authService.autenticar(txtLogin.getText(), txtSenha.getText());
            
            if (usuario != null) {
                abrirTelaPrincipal(usuario);
            } else {
                lblMensagem.setText("Login ou senha inválidos!");
            }
        } catch (SQLException e) {
            lblMensagem.setText("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }
    
    private void abrirTelaPrincipal(Usuario usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(c -> new MainController(usuario));
        Parent root = loader.load();
        
        Stage stage = (Stage) txtLogin.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 700));
        stage.setTitle("Sistema de Gestão de Projetos - " + usuario.getNomeCompleto());
        stage.setResizable(true);
        stage.show();
    }
}