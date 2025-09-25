package br.com.projetogestao.ui;

import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.AuthService;


import javax.swing.*;
import static javax.swing.BorderFactory.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final boolean preview;

    private JTextField campoLogin;
    private JPasswordField campoSenha;

    public LoginFrame(AuthService authService) {
        this(authService, false);
    }

    public LoginFrame(AuthService authService, boolean preview) {
        super("Gestao de Projetos - Login");
        this.authService = authService;
        this.preview = preview;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // Definir cores e fontes
        Color primaryColor = new Color(70, 130, 180); // Steel Blue
        Color backgroundColor = new Color(245, 245, 250);
        Color buttonColor = new Color(41, 128, 185); // Azul mais escuro
        Font titleFont = new Font("Arial", Font.BOLD, 22);
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        
        // Configurar painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Painel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(primaryColor);
        titlePanel.setBorder(createEmptyBorder(25, 20, 25, 20));
        JLabel title = new JLabel("Sistema de Gestão de Projetos");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        titlePanel.add(title);
        
        // Painel de formulário
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(createEmptyBorder(30, 50, 30, 50));
        
        // Subtítulo
        JLabel subtitle = new JLabel("Acesso ao Sistema");
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
        subtitle.setForeground(primaryColor);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(25));
        
        // Painel para Login
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        loginPanel.setBackground(backgroundColor);
        loginPanel.setMaximumSize(new Dimension(400, 60));
        
        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setFont(labelFont);
        loginLabel.setPreferredSize(new Dimension(80, 25));
        loginPanel.add(loginLabel);
        
        campoLogin = new JTextField(20);
        campoLogin.setFont(labelFont);
        campoLogin.setPreferredSize(new Dimension(250, 35));
        campoLogin.setBorder(createCompoundBorder(
            createLineBorder(new Color(200, 200, 200)),
            createEmptyBorder(8, 8, 8, 8)
        ));
        loginPanel.add(campoLogin);
        
        formPanel.add(loginPanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Painel para Senha
        JPanel senhaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        senhaPanel.setBackground(backgroundColor);
        senhaPanel.setMaximumSize(new Dimension(400, 60));
        
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(labelFont);
        senhaLabel.setPreferredSize(new Dimension(80, 25));
        senhaPanel.add(senhaLabel);
        
        campoSenha = new JPasswordField(20);
        campoSenha.setFont(labelFont);
        campoSenha.setPreferredSize(new Dimension(250, 35));
        campoSenha.setBorder(createCompoundBorder(
            createLineBorder(new Color(200, 200, 200)),
            createEmptyBorder(8, 8, 8, 8)
        ));
        senhaPanel.add(campoSenha);
        
        formPanel.add(senhaPanel);
        formPanel.add(Box.createVerticalStrut(25));
        
        // Botão de login
        JButton loginBtn = new JButton("Entrar");
        loginBtn.setFont(buttonFont);
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setBackground(buttonColor);
        loginBtn.setBorder(createEmptyBorder(12, 30, 12, 30));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.addActionListener(e -> onLogin());
        
        formPanel.add(loginBtn);
        
        // Adicionar painéis ao painel principal
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Adicionar painel principal à janela
        add(mainPanel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void onLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());
        
        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, informe login e senha.", 
                "Campos Obrigatórios", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Optional<Usuario> usuario = authService.autenticar(login, senha);
            if (usuario.isPresent()) {
                dispose();
                MainFrame mainFrame = new MainFrame(usuario.get(), preview);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            } else {
                String mensagemErro = preview ? 
                    "Credenciais inválidas.\n\nModo Preview - Use:\nLogin: admin\nSenha: admin123" :
                    "Credenciais inválidas.\n\nCredenciais padrão:\n• admin / admin123 (Administrador)\n• argeu / senha123 (Gerente)\n• luciana / senha123 (Colaborador)\n• adriano / senha123 (Colaborador)";
                
                JOptionPane.showMessageDialog(this, 
                    mensagemErro, 
                    "Erro de Autenticação", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            String mensagemErro = preview ? 
                "Erro interno no modo preview." :
                "Erro ao conectar com o banco de dados.\nVerifique se o MySQL está rodando e configurado corretamente.";
            
            JOptionPane.showMessageDialog(this, 
                mensagemErro + "\n\nDetalhes: " + e.getMessage(), 
                "Erro do Sistema", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}


