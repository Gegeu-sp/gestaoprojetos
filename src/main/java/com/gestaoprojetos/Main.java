package com.gestaoprojetos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.gestaoprojetos.controller.LoginController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {
    
    private Connection connection;
    
    @Override
    public void init() throws SQLException {
        // Inicializa a conexão com o banco de dados
        connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/gestao_projetos", 
            "root", 
            "senha"
        );
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        
        // Passa a conexão para o controller
        loader.setControllerFactory(param -> new LoginController(connection));
        
        Parent root = loader.load();
        
        primaryStage.setTitle("Sistema de Gestão de Projetos");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}