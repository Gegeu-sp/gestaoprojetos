package br.com.projetogestao.ui;

import br.com.projetogestao.model.Equipe;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.EquipeService;
// The import is actually used since EquipesPanel implements UsuarioListener
import br.com.projetogestao.ui.UsuarioListener;
import br.com.projetogestao.ui.UsuarioNotificationManager;

import javax.swing.BorderFactory;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class EquipesPanel extends JPanel implements UsuarioListener {
    private final EquipeService equipeService;
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JTextField campoNome = new JTextField(18);
    private final JTextField campoDescricao = new JTextField(18);
    private final DefaultListModel<Usuario> usuariosModel = new DefaultListModel<>();
    private final JList<Usuario> listaUsuarios = new JList<>(usuariosModel);

    public EquipesPanel(EquipeService equipeService) {
        super(new BorderLayout());
        this.equipeService = equipeService;
        this.tableModel = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        this.tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        ajustarColunas();
        adicionarFiltro();

        // usuarios list setup
        listaUsuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        for (Usuario u : equipeService.listarUsuarios()) usuariosModel.addElement(u);

        JPanel form = criarFormulario();
        add(form, BorderLayout.EAST);
        recarregarTabela();
        tabela.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) carregarSelecionado(); });
        
        // Registrar para receber notificações de novos usuários
        UsuarioNotificationManager.getInstance().addListener(this);
    }

    private void ajustarColunas(){
        var cm = tabela.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(1).setPreferredWidth(180);
    }

    private void adicionarFiltro(){
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tabela.setRowSorter(sorter);
        javax.swing.JTextField filtro = new javax.swing.JTextField(20);
        javax.swing.JPanel barra = new javax.swing.JPanel();
        barra.add(new javax.swing.JLabel("Filtrar:"));
        barra.add(filtro);
        add(barra, BorderLayout.NORTH);
        filtro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            private void apply(){ sorter.setRowFilter(RowFilter.regexFilter("(?i)"+java.util.regex.Pattern.quote(filtro.getText()))); }
            public void insertUpdate(javax.swing.event.DocumentEvent e){ apply(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ apply(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ apply(); }
        });
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Equipe"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        int y=0;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Nome:"), gbc); gbc.gridx=1; p.add(campoNome, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Descricao:"), gbc); gbc.gridx=1; p.add(campoDescricao, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; p.add(new JLabel("Membros (Ctrl+clique para multi):"), gbc); y++;
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1; gbc.weighty=1;
        p.add(new JScrollPane(listaUsuarios), gbc); y++;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel botoes = new JPanel();
        JButton btnNovo = new JButton("Novo"); btnNovo.addActionListener(e -> limpar());
        JButton btnSalvar = new JButton("Salvar"); btnSalvar.addActionListener(e -> salvarOuAtualizar());
        JButton btnExcluir = new JButton("Excluir"); btnExcluir.addActionListener(e -> excluir());
        botoes.add(btnNovo); botoes.add(btnSalvar); botoes.add(btnExcluir);
        p.add(botoes, gbc);
        return p;
    }

    private void recarregarTabela() {
        tableModel.setRowCount(0);
        for (Equipe eq : equipeService.listarTodas()) {
            tableModel.addRow(new Object[]{eq.getId(), eq.getNome()});
        }
    }

    private Long getIdSelecionado() {
        int row = tabela.getSelectedRow(); if (row < 0) return null;
        Object v = tableModel.getValueAt(row, 0);
        return v instanceof Number ? ((Number) v).longValue() : null;
    }

    private void carregarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) return;
        // carregar equipe corrente das linhas da tabela (somente nome)
        String nome = String.valueOf(tableModel.getValueAt(tabela.getSelectedRow(), 1));
        campoNome.setText(nome);
        
        // descricao nao esta na tabela; manter como esta
        // selecionar membros
        List<Long> membros = equipeService.listarMembros(id);
        List<Integer> indices = new ArrayList<>();
        for (int i=0;i<usuariosModel.size();i++) {
            Usuario u = usuariosModel.get(i);
            if (membros.contains(u.getId())) indices.add(i);
        }
        int[] idx = indices.stream().mapToInt(Integer::intValue).toArray();
        listaUsuarios.clearSelection();
        listaUsuarios.setSelectedIndices(idx);
    }

    private void salvarOuAtualizar() {
        try {
            Long id = getIdSelecionado();
            Equipe e = new Equipe();
            e.setId(id);
            e.setNome(campoNome.getText());
            e.setDescricao(campoDescricao.getText());
            equipeService.salvar(e);
            // definir membros
            Long equipeId = id != null ? id : getIdByNome(e.getNome());
            if (equipeId != null) {
                List<Long> selecionados = new ArrayList<>();
                for (Usuario u : listaUsuarios.getSelectedValuesList()) selecionados.add(u.getId());
                equipeService.definirMembros(equipeId, selecionados);
            }
            recarregarTabela(); limpar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getIdByNome(String nome) {
        for (int r=0; r<tableModel.getRowCount(); r++) {
            if (String.valueOf(tableModel.getValueAt(r,1)).equals(nome)) {
                Object v = tableModel.getValueAt(r,0); return v instanceof Number ? ((Number)v).longValue() : null;
            }
        }
        return null;
    }

    private void excluir() {
        Long id = getIdSelecionado(); if (id == null) return;
        int opt = JOptionPane.showConfirmDialog(this, "Excluir equipe?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) { equipeService.excluir(id); recarregarTabela(); limpar(); }
    }

    private void limpar() {
        tabela.clearSelection();
        campoNome.setText(""); campoDescricao.setText("");
        listaUsuarios.clearSelection();
    }
    
    @Override
    public void onUsuarioCadastrado() {
        // Recarregar a lista de usuários quando um novo usuário é cadastrado
        usuariosModel.clear();
        for (Usuario u : equipeService.listarUsuarios()) {
            usuariosModel.addElement(u);
        }
    }
}


