package br.com.projetogestao.ui;

import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.UsuarioService;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class UsuariosPanel extends JPanel {
    private final UsuarioService usuarioService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField campoNome = new JTextField(20);
    private final JFormattedTextField campoCpf = criarCpfField();
    private final JTextField campoEmail = new JTextField(20);
    private final JTextField campoCargo = new JTextField(16);
    private final JTextField campoLogin = new JTextField(16);
    private final JPasswordField campoSenha = new JPasswordField(16);
    private final JFormattedTextField campoDataNascimento = criarDataField();
    private final JComboBox<Perfil> comboPerfil = new JComboBox<>(Perfil.values());

    public UsuariosPanel(UsuarioService usuarioService) {
        super(new BorderLayout());
        this.usuarioService = usuarioService;
        this.tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Email", "Cargo", "Login", "Perfil"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        this.table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
        ajustarColunas();
        adicionarFiltroBusca();

        JPanel form = criarFormulario();
        add(form, BorderLayout.EAST);

        recarregarTabela();
    }

    private void ajustarColunas() {
        var cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(1).setPreferredWidth(160);
        cm.getColumn(2).setPreferredWidth(110);
        cm.getColumn(3).setPreferredWidth(160);
        cm.getColumn(4).setPreferredWidth(100);
        cm.getColumn(5).setPreferredWidth(100);
        cm.getColumn(6).setPreferredWidth(100);
    }

    private void adicionarFiltroBusca() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JTextField filtro = new JTextField(20);
        JPanel barra = new JPanel();
        barra.add(new JLabel("Filtrar:"));
        barra.add(filtro);
        add(barra, BorderLayout.NORTH);
        filtro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void apply() { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(filtro.getText()))); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { apply(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { apply(); }
        });
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

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Cadastro/Edicao"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; p.add(campoNome, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; p.add(campoCpf, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; p.add(campoEmail, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1; p.add(campoCargo, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Data Nascimento (DD/MM/AAAA):"), gbc);
        gbc.gridx = 1; p.add(campoDataNascimento, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1; p.add(campoLogin, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; p.add(campoSenha, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel("Perfil:"), gbc);
        gbc.gridx = 1; p.add(comboPerfil, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JPanel botoes = new JPanel();
        JButton btnNovo = new JButton("Novo"); btnNovo.addActionListener(e -> novoUsuario());
        JButton btnSalvar = new JButton("Salvar"); btnSalvar.addActionListener(e -> salvarOuAtualizar());
        JButton btnExcluir = new JButton("Excluir"); btnExcluir.addActionListener(e -> excluirSelecionado());
        botoes.add(btnNovo); botoes.add(btnSalvar); botoes.add(btnExcluir);
        p.add(botoes, gbc);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarSelecionadoNoFormulario();
        });

        return p;
    }

    private void recarregarTabela() {
        tableModel.setRowCount(0);
        List<Usuario> usuarios = usuarioService.listarTodos();
        for (Usuario u : usuarios) {
            tableModel.addRow(new Object[]{u.getId(), u.getNomeCompleto(), u.getCpf(), u.getEmail(), u.getCargo(), u.getLogin(), u.getPerfil()});
        }
    }

    private void limparFormulario() {
        table.clearSelection();
        campoNome.setText("");
        campoCpf.setText("");
        campoEmail.setText("");
        campoCargo.setText("");
        campoLogin.setText("");
        campoSenha.setText("");
        comboPerfil.setSelectedItem(Perfil.COLABORADOR);
    }

    private Long getIdSelecionado() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object val = tableModel.getValueAt(row, 0);
        return (val instanceof Number) ? ((Number) val).longValue() : null;
    }

    private void carregarSelecionadoNoFormulario() {
        Long id = getIdSelecionado();
        if (id == null) return;
        usuarioService.buscarPorId(id).ifPresent(u -> {
            campoNome.setText(u.getNomeCompleto());
            campoCpf.setText(u.getCpf());
            campoEmail.setText(u.getEmail());
            campoCargo.setText(u.getCargo());
            campoLogin.setText(u.getLogin());
            campoSenha.setText("");
            comboPerfil.setSelectedItem(u.getPerfil());
            
            if (u.getDataNascimento() != null) {
                java.time.LocalDate data = u.getDataNascimento();
                String dataFormatada = String.format("%02d/%02d/%04d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
                campoDataNascimento.setText(dataFormatada);
            } else {
                campoDataNascimento.setText("dd/MM/yyyy");
            }
        });
    }

    private void salvarOuAtualizar() {
        Long id = getIdSelecionado();
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String cargo = campoCargo.getText();
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());
        Perfil perfil = (Perfil) comboPerfil.getSelectedItem();
        String dataNascStr = campoDataNascimento.getText().trim();
        java.time.LocalDate dataNascimento = null;
        
        if (!dataNascStr.equals("dd/MM/yyyy")) {
            try {
                String[] partes = dataNascStr.split("/");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int ano = Integer.parseInt(partes[2]);
                dataNascimento = java.time.LocalDate.of(ano, mes, dia);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        try {
            if (id == null) {
                usuarioService.salvarNovo(nome, cpf, email, cargo, login, senha, perfil, dataNascimento);
            } else {
                usuarioService.atualizarExistente(id, nome, cpf, email, cargo, login, senha.isEmpty() ? null : senha, perfil, dataNascimento);
            }
            recarregarTabela();
            limparFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) return;
        int opt = JOptionPane.showConfirmDialog(this, "Excluir usuario selecionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                usuarioService.excluir(id);
                recarregarTabela();
                limparFormulario();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void novoUsuario() {
        CadastroUsuarioDialog dialog = new CadastroUsuarioDialog(
            (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
            usuarioService,
            () -> {
                // Notificar todas as abas sobre o novo usuário
                UsuarioNotificationManager.getInstance().notifyUsuarioCadastrado();
            }
        );
        dialog.setVisible(true);
        
        if (dialog.isSucesso()) {
            recarregarTabela();
        }
    }
}


