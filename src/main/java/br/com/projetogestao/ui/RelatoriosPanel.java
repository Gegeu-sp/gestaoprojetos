package br.com.projetogestao.ui;

import br.com.projetogestao.dao.ProjetoDao;
import br.com.projetogestao.dao.TarefaDao;
import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.StatusProjeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.print.PrinterException;

public class RelatoriosPanel extends JPanel implements UsuarioListener {
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DefaultTableModel modeloAndamento, modeloDesempenho, modeloRisco;
    private JTable tabelaAndamento, tabelaDesempenho, tabelaRisco;
    private ProjetoDao projetoDao;
    private TarefaDao tarefaDao;
    private UsuarioDao usuarioDao;
    
    public RelatoriosPanel(ProjetoDao projetoDao, TarefaDao tarefaDao, UsuarioDao usuarioDao) {
        super(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        
        // Armazenar referências dos DAOs para uso posterior
        this.projetoDao = projetoDao;
        this.tarefaDao = tarefaDao;
        this.usuarioDao = usuarioDao;
        
        // Painel superior com título e botões
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.setBackground(new Color(245, 245, 250));
        
        JLabel tituloLabel = new JLabel("Painel de Controle e Relatórios", JLabel.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        painelSuperior.add(tituloLabel, BorderLayout.CENTER);
        
        // Painel de botões para exportação
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(new Color(245, 245, 250));
        
        JButton botaoExportarPDF = new JButton(" Exportar PDF");
        botaoExportarPDF.setFont(new Font("Arial", Font.BOLD, 14));
        botaoExportarPDF.setBackground(new Color(0, 123, 255));
        botaoExportarPDF.setForeground(Color.BLACK);
        botaoExportarPDF.setBorder(BorderFactory.createRaisedBevelBorder());
        botaoExportarPDF.setPreferredSize(new Dimension(150, 35));
        botaoExportarPDF.setOpaque(true);
        botaoExportarPDF.addActionListener(e -> exportarParaPDF());
        
        JButton botaoImprimir = new JButton(" Imprimir");
        botaoImprimir.setFont(new Font("Arial", Font.BOLD, 14));
        botaoImprimir.setBackground(new Color(40, 167, 69));
        botaoImprimir.setForeground(Color.BLACK);
        botaoImprimir.setBorder(BorderFactory.createRaisedBevelBorder());
        botaoImprimir.setPreferredSize(new Dimension(130, 35));
        botaoImprimir.setOpaque(true);
        botaoImprimir.addActionListener(e -> imprimirRelatorio());
        
        JButton botaoAtualizar = new JButton("Atualizar");
        botaoAtualizar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoAtualizar.setBackground(new Color(255, 193, 7));
        botaoAtualizar.setForeground(Color.BLACK);
        botaoAtualizar.setBorder(BorderFactory.createRaisedBevelBorder());
        botaoAtualizar.setPreferredSize(new Dimension(130, 35));
        botaoAtualizar.setOpaque(true);
        botaoAtualizar.addActionListener(e -> atualizarDados(projetoDao, tarefaDao, usuarioDao));
        
        painelBotoes.add(botaoExportarPDF);
        painelBotoes.add(botaoImprimir);
        painelBotoes.add(botaoAtualizar);
        painelSuperior.add(painelBotoes, BorderLayout.EAST);
        
        add(painelSuperior, BorderLayout.NORTH);
        
        // Painel principal com layout de grade para os 3 relatórios
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // 1. Painel de Andamento de Projetos
        JPanel painelAndamento = criarPainelRelatorio("Resumo de Andamento dos Projetos", new Color(230, 240, 255));
        modeloAndamento = new DefaultTableModel(
            new Object[]{"Projeto", "Status", "Data Início", "Previsão Término"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
        tabelaAndamento = criarTabelaEstilizada(modeloAndamento);
        JScrollPane scrollAndamento = new JScrollPane(tabelaAndamento);
        scrollAndamento.setBorder(BorderFactory.createEmptyBorder());
        painelAndamento.add(scrollAndamento, BorderLayout.CENTER);
        
        // 2. Painel de Desempenho de Colaboradores
        JPanel painelDesempenho = criarPainelRelatorio("Desempenho de Colaboradores", new Color(230, 255, 230));
        modeloDesempenho = new DefaultTableModel(
            new Object[]{"Colaborador", "Tarefas Atribuídas", "Tarefas Concluídas", "Taxa de Sucesso (%)"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
        tabelaDesempenho = criarTabelaEstilizada(modeloDesempenho);
        JScrollPane scrollDesempenho = new JScrollPane(tabelaDesempenho);
        scrollDesempenho.setBorder(BorderFactory.createEmptyBorder());
        painelDesempenho.add(scrollDesempenho, BorderLayout.CENTER);
        
        // 3. Painel de Projetos em Risco
        JPanel painelRisco = criarPainelRelatorio("Projetos em Risco", new Color(255, 230, 230));
        modeloRisco = new DefaultTableModel(
            new Object[]{"Projeto", "Data Prevista", "Status Atual", "Situação"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
        tabelaRisco = criarTabelaEstilizada(modeloRisco);
        JScrollPane scrollRisco = new JScrollPane(tabelaRisco);
        scrollRisco.setBorder(BorderFactory.createEmptyBorder());
        painelRisco.add(scrollRisco, BorderLayout.CENTER);
        
        // Adicionar os painéis ao painel principal
        mainPanel.add(painelAndamento);
        mainPanel.add(painelDesempenho);
        mainPanel.add(painelRisco);
        add(mainPanel, BorderLayout.CENTER);
        
        // Carregar dados iniciais
        carregarDados(projetoDao, tarefaDao, usuarioDao);
        
        // Registrar para receber notificações de novos usuários
        UsuarioNotificationManager.getInstance().addListener(this);
    }
    
    private JPanel criarPainelRelatorio(String titulo, Color corFundo) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corFundo.darker(), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        painel.setBackground(corFundo);
        
        JLabel labelTitulo = new JLabel(titulo, JLabel.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painel.add(labelTitulo, BorderLayout.NORTH);
        
        return painel;
    }
    
    private JTable criarTabelaEstilizada(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.setShowGrid(true);
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(new Color(240, 240, 240));
        tabela.setSelectionBackground(new Color(210, 210, 255));
        return tabela;
    }
    
    private void exportarParaPDF() {
        JFileChooser selecionadorArquivo = new JFileChooser();
        selecionadorArquivo.setDialogTitle("Salvar Relatório");
        selecionadorArquivo.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto (*.txt)", "txt"));
        selecionadorArquivo.setSelectedFile(new java.io.File("relatorio_projetos_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".txt"));
        
        if (selecionadorArquivo.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String caminhoArquivo = selecionadorArquivo.getSelectedFile().getAbsolutePath();
                if (!caminhoArquivo.toLowerCase().endsWith(".txt")) {
                    caminhoArquivo += ".txt";
                }
                
                StringBuilder conteudo = new StringBuilder();
                conteudo.append("RELATÓRIO DE GESTÃO DE PROJETOS\n");
                conteudo.append("Data: ").append(LocalDate.now().format(FORMATADOR_DATA)).append("\n");
                conteudo.append("=".repeat(60)).append("\n\n");
                
                // Adicionar dados das tabelas com formatação melhorada
                conteudo.append("ANDAMENTO DOS PROJETOS:\n");
                conteudo.append("-".repeat(40)).append("\n");
                if (modeloAndamento.getRowCount() > 0) {
                    // Cabeçalhos
                    for (int j = 0; j < modeloAndamento.getColumnCount(); j++) {
                        conteudo.append(String.format("%-20s", modeloAndamento.getColumnName(j)));
                    }
                    conteudo.append("\n");
                    
                    // Dados
                    for (int i = 0; i < modeloAndamento.getRowCount(); i++) {
                        for (int j = 0; j < modeloAndamento.getColumnCount(); j++) {
                            Object valor = modeloAndamento.getValueAt(i, j);
                            conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                        }
                        conteudo.append("\n");
                    }
                } else {
                    conteudo.append("Nenhum projeto encontrado.\n");
                }
                
                conteudo.append("\nDESEMPENHO DE COLABORADORES:\n");
                conteudo.append("-".repeat(40)).append("\n");
                if (modeloDesempenho.getRowCount() > 0) {
                    // Cabeçalhos
                    for (int j = 0; j < modeloDesempenho.getColumnCount(); j++) {
                        conteudo.append(String.format("%-20s", modeloDesempenho.getColumnName(j)));
                    }
                    conteudo.append("\n");
                    
                    // Dados
                    for (int i = 0; i < modeloDesempenho.getRowCount(); i++) {
                        for (int j = 0; j < modeloDesempenho.getColumnCount(); j++) {
                            Object valor = modeloDesempenho.getValueAt(i, j);
                            conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                        }
                        conteudo.append("\n");
                    }
                } else {
                    conteudo.append("Nenhum colaborador encontrado.\n");
                }
                
                conteudo.append("\nPROJETOS EM RISCO:\n");
                conteudo.append("-".repeat(40)).append("\n");
                if (modeloRisco.getRowCount() > 0) {
                    // Cabeçalhos
                    for (int j = 0; j < modeloRisco.getColumnCount(); j++) {
                        conteudo.append(String.format("%-20s", modeloRisco.getColumnName(j)));
                    }
                    conteudo.append("\n");
                    
                    // Dados
                    for (int i = 0; i < modeloRisco.getRowCount(); i++) {
                        for (int j = 0; j < modeloRisco.getColumnCount(); j++) {
                            Object valor = modeloRisco.getValueAt(i, j);
                            conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                        }
                        conteudo.append("\n");
                    }
                } else {
                    conteudo.append("Nenhum projeto em risco encontrado.\n");
                }
                
                // Salvar como arquivo de texto
                try (FileOutputStream fos = new FileOutputStream(caminhoArquivo)) {
                    fos.write(conteudo.toString().getBytes("UTF-8"));
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Relatório exportado com sucesso!\nArquivo: " + caminhoArquivo,
                    "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao exportar relatório: " + ex.getMessage(),
                    "Erro de Exportação", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Para debug
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro inesperado ao exportar relatório: " + ex.getMessage(),
                    "Erro de Exportação", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Para debug
            }
        }
    }
    
    private void imprimirRelatorio() {
        try {
            JTextArea areaTexto = new JTextArea();
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 10));
            
            StringBuilder conteudo = new StringBuilder();
            conteudo.append("RELATÓRIO DE GESTÃO DE PROJETOS\n");
            conteudo.append("Data: ").append(LocalDate.now().format(FORMATADOR_DATA)).append("\n");
            conteudo.append("=".repeat(60)).append("\n\n");
            
            // Adicionar dados das tabelas com formatação melhorada
            conteudo.append("ANDAMENTO DOS PROJETOS:\n");
            conteudo.append("-".repeat(40)).append("\n");
            if (modeloAndamento.getRowCount() > 0) {
                // Cabeçalhos
                for (int j = 0; j < modeloAndamento.getColumnCount(); j++) {
                    conteudo.append(String.format("%-20s", modeloAndamento.getColumnName(j)));
                }
                conteudo.append("\n");
                
                // Dados
                for (int i = 0; i < modeloAndamento.getRowCount(); i++) {
                    for (int j = 0; j < modeloAndamento.getColumnCount(); j++) {
                        Object valor = modeloAndamento.getValueAt(i, j);
                        conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                    }
                    conteudo.append("\n");
                }
            } else {
                conteudo.append("Nenhum projeto encontrado.\n");
            }
            
            conteudo.append("\nDESEMPENHO DE COLABORADORES:\n");
            conteudo.append("-".repeat(40)).append("\n");
            if (modeloDesempenho.getRowCount() > 0) {
                // Cabeçalhos
                for (int j = 0; j < modeloDesempenho.getColumnCount(); j++) {
                    conteudo.append(String.format("%-20s", modeloDesempenho.getColumnName(j)));
                }
                conteudo.append("\n");
                
                // Dados
                for (int i = 0; i < modeloDesempenho.getRowCount(); i++) {
                    for (int j = 0; j < modeloDesempenho.getColumnCount(); j++) {
                        Object valor = modeloDesempenho.getValueAt(i, j);
                        conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                    }
                    conteudo.append("\n");
                }
            } else {
                conteudo.append("Nenhum colaborador encontrado.\n");
            }
            
            conteudo.append("\nPROJETOS EM RISCO:\n");
            conteudo.append("-".repeat(40)).append("\n");
            if (modeloRisco.getRowCount() > 0) {
                // Cabeçalhos
                for (int j = 0; j < modeloRisco.getColumnCount(); j++) {
                    conteudo.append(String.format("%-20s", modeloRisco.getColumnName(j)));
                }
                conteudo.append("\n");
                
                // Dados
                for (int i = 0; i < modeloRisco.getRowCount(); i++) {
                    for (int j = 0; j < modeloRisco.getColumnCount(); j++) {
                        Object valor = modeloRisco.getValueAt(i, j);
                        conteudo.append(String.format("%-20s", valor != null ? valor.toString() : "N/A"));
                    }
                    conteudo.append("\n");
                }
            } else {
                conteudo.append("Nenhum projeto em risco encontrado.\n");
            }
            
            areaTexto.setText(conteudo.toString());
            
            // Verificar se a impressão é suportada
            if (areaTexto.print()) {
                JOptionPane.showMessageDialog(this, "Relatório enviado para impressão!", 
                    "Impressão", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Impressão cancelada pelo usuário.", 
                    "Impressão", JOptionPane.WARNING_MESSAGE);
            }
                
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao imprimir: " + ex.getMessage(),
                "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Para debug
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro inesperado ao imprimir: " + ex.getMessage(),
                "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Para debug
        }
    }
    
    private void atualizarDados(ProjetoDao projetoDao, TarefaDao tarefaDao, UsuarioDao usuarioDao) {
        // Limpar dados existentes
        modeloAndamento.setRowCount(0);
        modeloDesempenho.setRowCount(0);
        modeloRisco.setRowCount(0);
        
        // Recarregar dados
        carregarDados(projetoDao, tarefaDao, usuarioDao);
        
        JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!", 
            "Atualização", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void carregarDados(ProjetoDao projetoDao, TarefaDao tarefaDao, UsuarioDao usuarioDao) {
        try {
            // Carregar dados de projetos
            List<Projeto> projetos = projetoDao.listarTodos();
            for (Projeto p : projetos) {
                String dataInicio = p.getDataInicio() != null ? p.getDataInicio().format(FORMATADOR_DATA) : "Não informado";
                String dataTermino = p.getDataTerminoPrevista() != null ? p.getDataTerminoPrevista().format(FORMATADOR_DATA) : "Não informado";
                modeloAndamento.addRow(new Object[]{p.getNome(), p.getStatus(), dataInicio, dataTermino});
            }

            // Desempenho por colaborador
            java.util.Map<Long, int[]> porUsuario = new java.util.HashMap<>();
            List<br.com.projetogestao.model.Usuario> usuarios = usuarioDao.listarTodos();
            
            // Inicializar contadores para todos os usuários
            for (br.com.projetogestao.model.Usuario u : usuarios) {
                porUsuario.put(u.getId(), new int[]{0, 0});
            }
            
            // Contar tarefas por usuário
            for (Projeto p : projetos) {
                List<br.com.projetogestao.model.Tarefa> tarefas = tarefaDao.buscarPorProjeto(p.getId());
                for (br.com.projetogestao.model.Tarefa t : tarefas) {
                    if (t.getResponsavelId() != null) {
                        int[] arr = porUsuario.computeIfAbsent(t.getResponsavelId(), k -> new int[]{0, 0});
                        arr[0]++; // Total de tarefas atribuídas
                        if (t.getStatus() == br.com.projetogestao.model.StatusTarefa.CONCLUIDA) {
                            arr[1]++; // Tarefas concluídas
                        }
                    }
                }
            }
            
            // Adicionar dados de desempenho à tabela
            for (var entry : porUsuario.entrySet()) {
                String nome = "Usuário " + entry.getKey();
                var optionalUsuario = usuarioDao.buscarPorId(entry.getKey());
                if (optionalUsuario.isPresent()) {
                    br.com.projetogestao.model.Usuario usuario = optionalUsuario.get();
                    nome = usuario.getNomeCompleto() != null ? usuario.getNomeCompleto() : "Usuário " + entry.getKey();
                }
                
                int atribuidas = entry.getValue()[0];
                int concluidas = entry.getValue()[1];
                double taxa = atribuidas > 0 ? (concluidas * 100.0 / atribuidas) : 0.0;
                
                modeloDesempenho.addRow(new Object[]{nome, atribuidas, concluidas, String.format("%.1f%%", taxa)});
            }

            // Projetos com risco de atraso
            LocalDate hoje = LocalDate.now();
            for (Projeto p : projetos) {
                boolean atrasado = p.getDataTerminoPrevista() != null &&
                                    hoje.isAfter(p.getDataTerminoPrevista()) &&
                                    p.getStatus() != StatusProjeto.CONCLUIDO;
                
                String dataTermino = p.getDataTerminoPrevista() != null ? 
                    p.getDataTerminoPrevista().format(FORMATADOR_DATA) : "Não informado";
                String situacao = atrasado ? "⚠️ EM ATRASO" : "✅ NO PRAZO";
                
                modeloRisco.addRow(new Object[]{p.getNome(), dataTermino, p.getStatus(), situacao});
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados: " + ex.getMessage(),
                "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Para debug
        }
    }
    
    /**
     * Implementação do UsuarioListener - chamado quando um novo usuário é cadastrado
     */
    @Override
    public void onUsuarioCadastrado() {
        // Atualizar os dados dos relatórios automaticamente
        atualizarDados(projetoDao, tarefaDao, usuarioDao);
    }
}


