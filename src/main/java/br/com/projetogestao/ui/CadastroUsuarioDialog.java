package br.com.projetogestao.ui;

import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.service.UsuarioService;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog para cadastro de novos usuários
 */
public class CadastroUsuarioDialog extends JDialog {
    private final UsuarioService usuarioService;
    private boolean sucesso = false;
    private UsuarioListener usuarioListener;
    
    // Campos do formulário
    private JTextField campoNome;
    private JFormattedTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoCargo;
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmaSenha;
    private JFormattedTextField campoDataNascimento;
    private JComboBox<Perfil> comboPerfil;
    
    public CadastroUsuarioDialog(JFrame parent, UsuarioService usuarioService) {
        super(parent, "Cadastro de Usuário", true);
        this.usuarioService = usuarioService;
        initComponents();
    }
    
    public CadastroUsuarioDialog(JFrame parent, UsuarioService usuarioService, UsuarioListener listener) {
        super(parent, "Cadastro de Usuário", true);
        this.usuarioService = usuarioService;
        this.usuarioListener = listener;
        initComponents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Cores e fontes
        Color primaryColor = new Color(70, 130, 180);
        Color backgroundColor = new Color(245, 245, 250);
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font titleFont = new Font("Arial", Font.BOLD, 16);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(primaryColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JLabel titleLabel = new JLabel("Cadastro de Novo Usuário");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Nome Completo
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblNome = new JLabel("Nome Completo:");
        lblNome.setFont(labelFont);
        formPanel.add(lblNome, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoNome = new JTextField(20);
        formPanel.add(campoNome, gbc);
        row++;
        
        // CPF
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCpf = new JLabel("CPF:");
        lblCpf.setFont(labelFont);
        formPanel.add(lblCpf, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoCpf = criarCpfField();
        formPanel.add(campoCpf, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        formPanel.add(lblEmail, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoEmail = new JTextField(20);
        formPanel.add(campoEmail, gbc);
        row++;
        
        // Cargo
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCargo = new JLabel("Cargo:");
        lblCargo.setFont(labelFont);
        formPanel.add(lblCargo, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoCargo = new JTextField(20);
        formPanel.add(campoCargo, gbc);
        row++;
        
        // Login
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(labelFont);
        formPanel.add(lblLogin, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoLogin = new JTextField(20);
        formPanel.add(campoLogin, gbc);
        row++;
        
        // Senha
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(labelFont);
        formPanel.add(lblSenha, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoSenha = new JPasswordField(20);
        formPanel.add(campoSenha, gbc);
        row++;
        
        // Confirmar Senha
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblConfirmaSenha = new JLabel("Confirmar Senha:");
        lblConfirmaSenha.setFont(labelFont);
        formPanel.add(lblConfirmaSenha, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoConfirmaSenha = new JPasswordField(20);
        formPanel.add(campoConfirmaSenha, gbc);
        row++;
        
        // Data de Nascimento
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblDataNascimento = new JLabel("Data de Nascimento:");
        lblDataNascimento.setFont(labelFont);
        formPanel.add(lblDataNascimento, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoDataNascimento = criarDataField();
        formPanel.add(campoDataNascimento, gbc);
        row++;
        
        // Perfil
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        JLabel lblPerfil = new JLabel("Perfil:");
        lblPerfil.setFont(labelFont);
        formPanel.add(lblPerfil, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        comboPerfil = new JComboBox<>(Perfil.values());
        comboPerfil.setSelectedItem(Perfil.COLABORADOR); // Padrão
        formPanel.add(comboPerfil, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(backgroundColor);
        
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 35));
        btnSalvar.setBackground(new Color(41, 128, 185));
        btnSalvar.setForeground(Color.BLACK);
        btnSalvar.setFocusPainted(false);
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        
        // Montar layout
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Definir botão padrão
        getRootPane().setDefaultButton(btnSalvar);
    }
    
    private JFormattedTextField criarCpfField() {
        try {
            MaskFormatter mf = new MaskFormatter("###.###.###-##");
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }
    
    private JFormattedTextField criarDataField() {
        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(mf);
            field.setEditable(true);
            return field;
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }
    
    private void salvarUsuario() {
        try {
            // Validar campos obrigatórios
            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim();
            String email = campoEmail.getText().trim();
            String cargo = campoCargo.getText().trim();
            String login = campoLogin.getText().trim();
            String senha = new String(campoSenha.getPassword());
            String confirmaSenha = new String(campoConfirmaSenha.getPassword());
            String dataTexto = campoDataNascimento.getText().trim();
            Perfil perfil = (Perfil) comboPerfil.getSelectedItem();
            
            // Validações básicas
            if (nome.isEmpty()) {
                mostrarErro("Nome completo é obrigatório.");
                campoNome.requestFocus();
                return;
            }
            
            if (cpf.isEmpty() || cpf.contains("_")) {
                mostrarErro("CPF é obrigatório e deve estar completo.");
                campoCpf.requestFocus();
                return;
            }
            
            if (email.isEmpty()) {
                mostrarErro("Email é obrigatório.");
                campoEmail.requestFocus();
                return;
            }
            
            if (cargo.isEmpty()) {
                mostrarErro("Cargo é obrigatório.");
                campoCargo.requestFocus();
                return;
            }
            
            if (login.isEmpty()) {
                mostrarErro("Login é obrigatório.");
                campoLogin.requestFocus();
                return;
            }
            
            if (senha.isEmpty()) {
                mostrarErro("Senha é obrigatória.");
                campoSenha.requestFocus();
                return;
            }
            
            if (senha.length() < 6) {
                mostrarErro("Senha deve ter pelo menos 6 caracteres.");
                campoSenha.requestFocus();
                return;
            }
            
            if (!senha.equals(confirmaSenha)) {
                mostrarErro("Senhas não conferem.");
                campoConfirmaSenha.requestFocus();
                return;
            }
            
            // Validar e converter data
            LocalDate dataNascimento = null;
            if (!dataTexto.isEmpty() && !dataTexto.contains("_")) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dataNascimento = LocalDate.parse(dataTexto, formatter);
                } catch (DateTimeParseException e) {
                    mostrarErro("Data de nascimento inválida. Use o formato DD/MM/AAAA.");
                    campoDataNascimento.requestFocus();
                    return;
                }
            }
            
            // Salvar usuário
            usuarioService.salvarNovo(nome, cpf, email, cargo, login, senha, perfil, dataNascimento);
            
            sucesso = true;
            JOptionPane.showMessageDialog(this, 
                "Usuário cadastrado com sucesso!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Notificar listener se existir
            if (usuarioListener != null) {
                usuarioListener.onUsuarioCadastrado();
            }
            
            dispose();
            
        } catch (Exception e) {
            mostrarErro("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }
    
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isSucesso() {
        return sucesso;
    }
}