package br.com.projetogestao.ui;

import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.StatusTarefa;
import br.com.projetogestao.model.Tarefa;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.TarefaService;
import br.com.projetogestao.dao.ProjetoDao;
import br.com.projetogestao.dao.UsuarioDao;

import javax.swing.BorderFactory;
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
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

public class PainelTarefas extends JPanel implements UsuarioListener {
    private java.util.Map<Long,String> cacheUsuarios = new java.util.HashMap<>();
    private final TarefaService servicoTarefa;
    private final UsuarioDao usuarioDao;
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final JComboBox<Projeto> comboProjeto;
    private final JComboBox<Usuario> comboResponsavel;
    private final JComboBox<StatusTarefa> comboStatus = new JComboBox<>(StatusTarefa.values());
    private final JTextField campoTitulo = new JTextField(18);
    private final JTextField campoDescricao = new JTextField(18);
    private final JFormattedTextField campoInicioPrevisto = criarCampoData();
    private final JFormattedTextField campoFimPrevisto = criarCampoData();
    private final JFormattedTextField campoInicioReal = criarCampoData();
    private final JFormattedTextField campoFimReal = criarCampoData();

    public PainelTarefas(TarefaService tarefaService, ProjetoDao projetoDao, UsuarioDao usuarioDao) {
        super(new BorderLayout());
        this.servicoTarefa = tarefaService;
        this.usuarioDao = usuarioDao;
        this.modeloTabela = new DefaultTableModel(new Object[]{"ID","Título","Projeto","Responsável","Status","Início Previsto","Fim Previsto"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        this.tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        ajustarColunas();
        adicionarFiltro();

        List<Projeto> projetos = projetoDao.listarTodos();
        comboProjeto = new JComboBox<>(projetos.toArray(new Projeto[0]));
        List<Usuario> usuarios = usuarioDao.listarTodos();
        for (Usuario u : usuarios) cacheUsuarios.put(u.getId(), u.toString());
        comboResponsavel = new JComboBox<>(usuarios.toArray(new Usuario[0]));

        JPanel formulario = criarFormulario();
        add(formulario, BorderLayout.EAST);

        recarregarTabela();
        tabela.getSelectionModel().addListSelectionListener(e->{ if(!e.getValueIsAdjusting()) carregarSelecionado();});
        comboProjeto.addActionListener(e -> recarregarTabela());
        
        // Registrar para receber notificações de novos usuários
        UsuarioNotificationManager.getInstance().addListener(this);
    }

    private void ajustarColunas(){
        var cm = tabela.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(1).setPreferredWidth(180);
        cm.getColumn(2).setPreferredWidth(160);
        cm.getColumn(3).setPreferredWidth(160);
        cm.getColumn(4).setPreferredWidth(100);
    }

    private void adicionarFiltro(){
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabela);
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

    private JFormattedTextField criarCampoData(){
        try {
            javax.swing.text.MaskFormatter mascara = new javax.swing.text.MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(mascara);
            campo.setToolTipText("Formato: dd/MM/yyyy");
            return campo;
        } catch (java.text.ParseException e) {
            return new JFormattedTextField();
        }
    }

    private String obterNomeResponsavel(Long id) {
        if (id == null) return "";
        return cacheUsuarios.getOrDefault(id, String.valueOf(id));
    }

    private JPanel criarFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Tarefa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets=new Insets(4,4,4,4); gbc.anchor=GridBagConstraints.WEST;
        int y=0;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Titulo:"), gbc); gbc.gridx=1; p.add(campoTitulo, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Descricao:"), gbc); gbc.gridx=1; p.add(campoDescricao, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Projeto:"), gbc); gbc.gridx=1; p.add(comboProjeto, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Responsavel:"), gbc); gbc.gridx=1; p.add(comboResponsavel, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Status:"), gbc); gbc.gridx=1; p.add(comboStatus, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Inicio Prev (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoInicioPrevisto, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Fim Prev (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoFimPrevisto, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Inicio Real (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoInicioReal, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Fim Real (DD/MM/AAAA):"), gbc); gbc.gridx=1; p.add(campoFimReal, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel botoes = new JPanel();
        JButton btnNovo = new JButton("Novo"); btnNovo.addActionListener(e->limpar());
        JButton btnSalvar = new JButton("Salvar"); btnSalvar.addActionListener(e->salvarOuAtualizar());
        JButton btnExcluir = new JButton("Excluir"); btnExcluir.addActionListener(e->excluir());
        botoes.add(btnNovo); botoes.add(btnSalvar); botoes.add(btnExcluir);
        p.add(botoes, gbc);
        return p;
    }

    private void recarregarTabela(){
        modeloTabela.setRowCount(0);
        List<Tarefa> tarefas = servicoTarefa.listarTodas();
        for (Tarefa t : tarefas) {
            String nomeResponsavel = obterNomeResponsavel(t.getResponsavelId());
            String dataInicioPrev = t.getDataInicioPrevista() != null ? 
                t.getDataInicioPrevista().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            String dataFimPrev = t.getDataTerminoPrevista() != null ? 
                t.getDataTerminoPrevista().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            // Buscar nome do projeto pelo ID
            String nomeProjeto = "Projeto " + t.getProjetoId(); // Simplificado por enquanto
            modeloTabela.addRow(new Object[]{t.getId(), t.getTitulo(), nomeProjeto, 
                nomeResponsavel, t.getStatus(), dataInicioPrev, dataFimPrev});
        }
    }

    private Long getIdSelecionado(){
        int row = tabela.getSelectedRow(); if (row < 0) return null;
        Object v = modeloTabela.getValueAt(row, 0); return v instanceof Number ? ((Number)v).longValue() : null;
    }

    private void carregarSelecionado(){
        Long id = getIdSelecionado(); if (id == null) return;
        // Simplificado: campos sao preenchidos pelo form quando salva; leitura completa exigiria buscar por id
    }

    private void salvarOuAtualizar(){
        try {
            if (campoTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O título é obrigatório!", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Tarefa tarefa = new Tarefa();
            Long id = getIdSelecionado();
            if (id != null) tarefa.setId(id);
            tarefa.setTitulo(campoTitulo.getText().trim());
            tarefa.setDescricao(campoDescricao.getText().trim());
            Projeto projetoSelecionado = (Projeto) comboProjeto.getSelectedItem();
            tarefa.setProjetoId(projetoSelecionado.getId());
            tarefa.setResponsavelId(((Usuario) comboResponsavel.getSelectedItem()).getId());
            tarefa.setStatus((StatusTarefa) comboStatus.getSelectedItem());
            
            // Converter datas do formato dd/MM/yyyy
            if (!campoInicioPrevisto.getText().trim().isEmpty() && !campoInicioPrevisto.getText().contains("_")) {
                String[] partes = campoInicioPrevisto.getText().split("/");
                if (partes.length == 3) {
                    tarefa.setDataInicioPrevista(LocalDate.of(Integer.parseInt(partes[2]), Integer.parseInt(partes[1]), Integer.parseInt(partes[0])));
                }
            }
            
            if (!campoFimPrevisto.getText().trim().isEmpty() && !campoFimPrevisto.getText().contains("_")) {
                String[] partes = campoFimPrevisto.getText().split("/");
                if (partes.length == 3) {
                    tarefa.setDataTerminoPrevista(LocalDate.of(Integer.parseInt(partes[2]), Integer.parseInt(partes[1]), Integer.parseInt(partes[0])));
                }
            }
            
            if (id == null) {
                servicoTarefa.salvarNovo(tarefa.getTitulo(), tarefa.getDescricao(), 
                    tarefa.getProjetoId(), tarefa.getResponsavelId(), tarefa.getStatus(),
                    tarefa.getDataInicioPrevista(), tarefa.getDataTerminoPrevista(),
                    tarefa.getDataInicioReal(), tarefa.getDataTerminoReal());
                JOptionPane.showMessageDialog(this, "✅ Tarefa criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                servicoTarefa.atualizarExistente(id, tarefa.getTitulo(), tarefa.getDescricao(),
                    tarefa.getProjetoId(), tarefa.getResponsavelId(), tarefa.getStatus(),
                    tarefa.getDataInicioPrevista(), tarefa.getDataTerminoPrevista(),
                    tarefa.getDataInicioReal(), tarefa.getDataTerminoReal());
                JOptionPane.showMessageDialog(this, "✅ Tarefa atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limpar();
            recarregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir(){
        Long id = getIdSelecionado();
        if (id != null && JOptionPane.showConfirmDialog(this, "🗑️ Confirma a exclusão desta tarefa?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            servicoTarefa.excluir(id);
            JOptionPane.showMessageDialog(this, "✅ Tarefa excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limpar(); 
            recarregarTabela();
        }
    }

    private void limpar(){
        campoTitulo.setText("");
        campoDescricao.setText("");
        comboStatus.setSelectedIndex(0);
        campoInicioPrevisto.setText("");
        campoFimPrevisto.setText("");
        campoInicioReal.setText("");
        campoFimReal.setText("");
        tabela.clearSelection();
    }
    
    @Override
    public void onUsuarioCadastrado() {
        // Atualizar lista de usuários quando um novo usuário é cadastrado
        List<Usuario> usuarios = usuarioDao.listarTodos();
        Usuario responsavelSelecionado = (Usuario) comboResponsavel.getSelectedItem();
        
        // Atualizar cache de usuários
        cacheUsuarios.clear();
        for (Usuario u : usuarios) {
            cacheUsuarios.put(u.getId(), u.toString());
        }
        
        // Atualizar combo de responsáveis
        comboResponsavel.removeAllItems();
        for (Usuario usuario : usuarios) {
            comboResponsavel.addItem(usuario);
        }
        
        // Tentar manter a seleção anterior se ainda existir
        if (responsavelSelecionado != null) {
            for (Usuario usuario : usuarios) {
                if (usuario.getId().equals(responsavelSelecionado.getId())) {
                    comboResponsavel.setSelectedItem(usuario);
                    break;
                }
            }
        }
        
        // Recarregar tabela para atualizar nomes dos responsáveis
        recarregarTabela();
    }
}


