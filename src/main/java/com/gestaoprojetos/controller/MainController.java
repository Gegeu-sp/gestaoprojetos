package com.gestaoprojetos.controller;

import com.gestaoprojetos.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {
    @FXML private Label lblUsuarioLogado;
    @FXML private Label lblTotalProjetos;
    @FXML private Label lblProjetosAndamento;
    @FXML private Label lblProjetosRisco;
    @FXML private TableView<Projeto> tabelaProjetos;
    @FXML private TableColumn<Projeto, String> colunaNome;
    @FXML private TableColumn<Projeto, String> colunaStatus;
    @FXML private TableColumn<Projeto, String> colunaGerente;
    @FXML private TableColumn<Projeto, String> colunaPrevisao;
    
    private Usuario usuarioLogado;
    
    public MainController(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
    
    @FXML
    public void initialize() {
        lblUsuarioLogado.setText("Bem-vindo, " + usuarioLogado.getNomeCompleto() + " (" + usuarioLogado.getPerfil() + ")");
        
        // Configura a tabela
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaGerente.setCellValueFactory(new PropertyValueFactory<>("gerente"));
        colunaPrevisao.setCellValueFactory(new PropertyValueFactory<>("dataTerminoPrevista"));
        
        // Aqui você carregaria os dados do banco
        carregarDados();
    }
    
    private void carregarDados() {
        // Implementar carregamento de dados do banco
        lblTotalProjetos.setText("5");
        lblProjetosAndamento.setText("3");
        lblProjetosRisco.setText("1");
    }
    
    @FXML
    private void abrirCadastroUsuarios() throws IOException {
        abrirTela("/fxml/cadastro_usuario.fxml", "Cadastro de Usuários");
    }
    
    @FXML
    private void abrirCadastroProjetos() throws IOException {
        abrirTela("/fxml/cadastro_projeto.fxml", "Cadastro de Projetos");
    }
    
    @FXML
    private void abrirCadastroEquipes() throws IOException {
        abrirTela("/fxml/cadastro_equipe.fxml", "Cadastro de Equipes");
    }
    
    @FXML
    private void abrirCadastroTarefas() throws IOException {
        abrirTela("/fxml/cadastro_tarefa.fxml", "Cadastro de Tarefas");
    }
    
    @FXML
    private void abrirDashboard() throws IOException {
        abrirTela("/fxml/dashboard.fxml", "Dashboard");
    }
    
    @FXML
    private void abrirRelatorios() throws IOException {
        abrirTela("/fxml/relatorio.fxml", "Relatórios");
    }
    
    private void abrirTela(String fxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
    
    @FXML
    private void sair() {
        System.exit(0);
    }
}