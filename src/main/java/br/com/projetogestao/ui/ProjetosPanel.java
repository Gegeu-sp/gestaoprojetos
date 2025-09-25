package br.com.projetogestao.ui;

import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.StatusProjeto;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.ProjetoService;
import br.com.projetogestao.ui.UsuarioListener;
import br.com.projetogestao.ui.UsuarioNotificationManager;

import javax.swing.BorderFactory;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProjetosPanel extends JPanel implements UsuarioListener {
    private final ProjetoService projetoService;
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField campoNome = new JTextField(18);
    private final JTextField campoDescricao = new JTextField(18);
    private final JFormattedTextField campoDataInicio = criarDataField();
    private final JFormattedTextField campoDataPrevista = criarDataField();
    private final JFormattedTextField campoDataReal = criarDataField();
    private final JComboBox<StatusProjeto> comboStatus = new JComboBox<>(StatusProjeto.values());
    private final JComboBox<Usuario> comboGerente;
    private List<Usuario> gerentesCache;

    public ProjetosPanel(ProjetoService projetoService) {
        super(new BorderLayout());
        this.projetoService = projetoService;
        this.tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Inicio", "Prevista", "Status", "Gerente"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        this.tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        ajustarColunas();
        adicionarFiltro();

        gerentesCache = projetoService.listarGerentes();
        comboGerente = new JComboBox<>(gerentesCache.toArray(new Usuario[0]));

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
        cm.getColumn(1).setPreferredWidth(160);
        cm.getColumn(2).setPreferredWidth(110);
        cm.getColumn(3).setPreferredWidth(110);
        cm.getColumn(4).setPreferredWidth(90);
        cm.getColumn(5).setPreferredWidth(140);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cm.getColumn(2).setCellRenderer(center);
        cm.getColumn(3).setCellRenderer(center);
        cm.getColumn(4).setCellRenderer(center);
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

    private JFormattedTextField criarDataField(){
        try {
            MaskFormatter mf = new MaskFormatter("dd/MM/yyyy");
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Projeto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Nome:"), gbc); gbc.gridx=1; p.add(campoNome, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Descricao:"), gbc); gbc.gridx=1; p.add(campoDescricao, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Data inicio (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoDataInicio, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Data prevista (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoDataPrevista, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Data real (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoDataReal, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Status:"), gbc); gbc.gridx=1; p.add(comboStatus, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Gerente:"), gbc); gbc.gridx=1; p.add(comboGerente, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
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
        for (Projeto p : projetoService.listarTodos()) {
            String gerente = getGerenteNome(p.getGerenteId());
            String dataInicio = p.getDataInicio() != null ? p.getDataInicio().format(FORMATADOR_DATA) : "";
            String dataPrevista = p.getDataTerminoPrevista() != null ? p.getDataTerminoPrevista().format(FORMATADOR_DATA) : "";
            tableModel.addRow(new Object[]{p.getId(), p.getNome(), dataInicio, dataPrevista, p.getStatus(), gerente});
        }
    }

    private String getGerenteNome(Long gerenteId) {
        if (gerenteId == null) return "";
        for (Usuario u : gerentesCache) {
            if (u.getId() != null && u.getId().equals(gerenteId)) return u.getNomeCompleto();
        }
        return String.valueOf(gerenteId);
    }

    private Long getIdSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null;
        Object v = tableModel.getValueAt(row, 0);
        return v instanceof Number ? ((Number) v).longValue() : null;
    }

    private void carregarSelecionado() {
        Long id = getIdSelecionado();
        if (id == null) return;
        projetoService.buscarPorId(id).ifPresent(p -> {
            campoNome.setText(p.getNome());
            campoDescricao.setText(p.getDescricao());
            campoDataInicio.setText(p.getDataInicio() == null ? "" : p.getDataInicio().format(FORMATADOR_DATA));
            campoDataPrevista.setText(p.getDataTerminoPrevista() == null ? "" : p.getDataTerminoPrevista().format(FORMATADOR_DATA));
            campoDataReal.setText(p.getDataTerminoReal() == null ? "" : p.getDataTerminoReal().format(FORMATADOR_DATA));
            comboStatus.setSelectedItem(p.getStatus());
            // selecionar gerente pelo id
            for (int i=0;i<comboGerente.getItemCount();i++) { if (comboGerente.getItemAt(i).getId().equals(p.getGerenteId())) { comboGerente.setSelectedIndex(i); break; } }
        });
    }

    private void salvarOuAtualizar() {
        try {
            Long id = getIdSelecionado();
            Usuario gerente = (Usuario) comboGerente.getSelectedItem();
            if (id == null) {
                projetoService.salvarNovo(
                        campoNome.getText(),
                        campoDescricao.getText(),
                        campoDataInicio.getText(),
                        campoDataPrevista.getText(),
                        (StatusProjeto) comboStatus.getSelectedItem(),
                        gerente == null ? null : gerente.getId()
                );
            } else {
                projetoService.atualizarExistente(
                        id,
                        campoNome.getText(),
                        campoDescricao.getText(),
                        campoDataInicio.getText(),
                        campoDataPrevista.getText(),
                        campoDataReal.getText(),
                        (StatusProjeto) comboStatus.getSelectedItem(),
                        gerente == null ? null : gerente.getId()
                );
            }
            recarregarTabela();
            limpar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        Long id = getIdSelecionado(); if (id == null) return;
        int opt = JOptionPane.showConfirmDialog(this, "Excluir projeto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) { projetoService.excluir(id); recarregarTabela(); limpar(); }
    }

    private void limpar() {
        tabela.clearSelection();
        campoNome.setText(""); campoDescricao.setText("");
        campoDataInicio.setText(""); campoDataPrevista.setText(""); campoDataReal.setText("");
        comboStatus.setSelectedItem(StatusProjeto.PLANEJADO);
        if (comboGerente.getItemCount() > 0) comboGerente.setSelectedIndex(0);
    }
    
    @Override
    public void onUsuarioCadastrado() {
        // Atualizar lista de gerentes quando um novo usuário é cadastrado
        gerentesCache = projetoService.listarGerentes();
        Usuario gerenteSelecionado = (Usuario) comboGerente.getSelectedItem();
        
        comboGerente.removeAllItems();
        for (Usuario gerente : gerentesCache) {
            comboGerente.addItem(gerente);
        }
        
        // Tentar manter a seleção anterior se ainda existir
        if (gerenteSelecionado != null) {
            for (Usuario gerente : gerentesCache) {
                if (gerente.getId().equals(gerenteSelecionado.getId())) {
                    comboGerente.setSelectedItem(gerente);
                    break;
                }
            }
        }
    }
}


