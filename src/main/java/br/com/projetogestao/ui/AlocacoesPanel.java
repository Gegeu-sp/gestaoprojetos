package br.com.projetogestao.ui;

import br.com.projetogestao.dao.ProjetoDao;
import br.com.projetogestao.dao.EquipeDao;
import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.Equipe;
import br.com.projetogestao.service.ProjetoEquipeService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class AlocacoesPanel extends JPanel {
    private final ProjetoEquipeService projetoEquipeService;
    private final JComboBox<Projeto> comboProjeto;
    private final JComboBox<Equipe> comboEquipe;
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final java.util.Map<Long,String> mapaEquipeNome = new java.util.HashMap<>();

    public AlocacoesPanel(ProjetoEquipeService projetoEquipeService, ProjetoDao projetoDao, EquipeDao equipeDao) {
        super(new BorderLayout());
        this.projetoEquipeService = projetoEquipeService;
        List<Projeto> projetos = projetoDao.listarTodos();
        List<Equipe> equipes = equipeDao.listarTodas();
        comboProjeto = new JComboBox<>(projetos.toArray(new Projeto[0]));
        comboEquipe = new JComboBox<>(equipes.toArray(new Equipe[0]));
        for (Equipe e : equipes) if (e.getId()!=null) mapaEquipeNome.put(e.getId(), e.getNome());

        tableModel = new DefaultTableModel(new Object[]{"Equipes alocadas"}, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel form = criarFormulario();
        add(form, BorderLayout.EAST);
        recarregarTabela();
    }

    private JPanel criarFormulario(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Alocacao"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4); gbc.anchor = GridBagConstraints.WEST;
        int y=0;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Projeto:"), gbc); gbc.gridx=1; p.add(comboProjeto, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; p.add(new JLabel("Equipe:"), gbc); gbc.gridx=1; p.add(comboEquipe, gbc); y++;
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel botoes = new JPanel();
        JButton btnVincular = new JButton("Vincular"); btnVincular.addActionListener(e->vincular());
        JButton btnDesvincular = new JButton("Desvincular"); btnDesvincular.addActionListener(e->desvincular());
        botoes.add(btnVincular); botoes.add(btnDesvincular);
        p.add(botoes, gbc);
        return p;
    }

    private void recarregarTabela(){
        tableModel.setRowCount(0);
        Projeto p = (Projeto) (comboProjeto.getItemCount()>0 ? comboProjeto.getSelectedItem() : null);
        if (p == null) return;
        for (Long equipeId : projetoEquipeService.listarEquipesPorProjeto(p.getId())){
            String nome = mapaEquipeNome.getOrDefault(equipeId, String.valueOf(equipeId));
            tableModel.addRow(new Object[]{nome});
        }
    }

    private void vincular(){
        Projeto p = (Projeto) comboProjeto.getSelectedItem();
        Equipe e = (Equipe) comboEquipe.getSelectedItem();
        if (p==null || e==null) return;
        projetoEquipeService.vincular(p.getId(), e.getId());
        recarregarTabela();
    }

    private void desvincular(){
        int row = tabela.getSelectedRow();
        Projeto p = (Projeto) comboProjeto.getSelectedItem();
        if (row<0 || p==null) return;
        Object v = tableModel.getValueAt(row, 0);
        Long equipeId = v instanceof Number ? ((Number)v).longValue() : null;
        if (equipeId == null) return;
        projetoEquipeService.desvincular(p.getId(), equipeId);
        recarregarTabela();
    }
}


