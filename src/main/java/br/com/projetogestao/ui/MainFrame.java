package br.com.projetogestao.ui;

import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.dao.UsuarioDaoImpl;
import br.com.projetogestao.dao.ProjetoDaoImpl;
import br.com.projetogestao.dao.EquipeDaoImpl;
import br.com.projetogestao.dao.TarefaDaoImpl;
import br.com.projetogestao.config.DataSourceProvider;
import br.com.projetogestao.service.UsuarioService;
import br.com.projetogestao.service.ProjetoService;
import br.com.projetogestao.service.EquipeService;
import br.com.projetogestao.service.TarefaService;
import br.com.projetogestao.service.ProjetoEquipeService;
import br.com.projetogestao.ui.PainelTarefas;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import static javax.swing.BorderFactory.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class MainFrame extends JFrame {
    private final Usuario usuarioAtual;
    private final boolean preview;

    public MainFrame(Usuario usuarioAtual) { this(usuarioAtual, false); }

    public MainFrame(Usuario usuarioAtual, boolean preview) {
        super("Gestao de Projetos");
        this.usuarioAtual = usuarioAtual;
        this.preview = preview;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setTitle("Sistema de Gestão de Projetos");
        
        // Definir cores e fontes
        Color primaryColor = new Color(70, 130, 180); // Steel Blue
        Color backgroundColor = new Color(245, 245, 250);
        Font tabFont = new Font("Arial", Font.BOLD, 14);
        
        // Configurar painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Painel de cabeçalho
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Sistema de Gestão de Projetos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Usuário: " + (usuarioAtual != null ? usuarioAtual.getNomeCompleto() : "Preview"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        // Configurar TabbedPane com estilo moderno
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(tabFont);
        abas.setBackground(backgroundColor);
        abas.setBorder(createEmptyBorder());
        
        // Aba inicial sempre visivel - Dashboard
        JPanel homePanel = createDashboardPanel();
        abas.addTab("Início", homePanel);

        if (!preview && usuarioAtual != null && usuarioAtual.getPerfil() == Perfil.ADMINISTRADOR) {
            UsuarioService usuarioService = new UsuarioService(new UsuarioDaoImpl(DataSourceProvider.getInstance().getDataSource()));
            abas.addTab("Usuários", new UsuariosPanel(usuarioService));
        } else if (usuarioAtual != null && usuarioAtual.getPerfil() == Perfil.ADMINISTRADOR) {
            JPanel previewPanel = new JPanel(new BorderLayout());
            previewPanel.setBackground(backgroundColor);
            previewPanel.setBorder(createEmptyBorder(30, 30, 30, 30));
            
            JLabel infoLabel = new JLabel("(Preview) Gestão de Usuários - Desabilitado sem banco", JLabel.CENTER);
            infoLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            infoLabel.setForeground(new Color(150, 150, 150));
            previewPanel.add(infoLabel, BorderLayout.CENTER);
            
            abas.addTab("Usuários", previewPanel);
        }

        if (usuarioAtual != null && (usuarioAtual.getPerfil() == Perfil.ADMINISTRADOR || usuarioAtual.getPerfil() == Perfil.GERENTE)) {
            if (!preview) {
                var ds = DataSourceProvider.getInstance().getDataSource();
                ProjetoService projetoService = new ProjetoService(new ProjetoDaoImpl(ds), new UsuarioDaoImpl(ds));
                abas.addTab("Projetos", new ProjetosPanel(projetoService));

                EquipeService equipeService = new EquipeService(new EquipeDaoImpl(ds), new UsuarioDaoImpl(ds));
                abas.addTab("Equipes", new EquipesPanel(equipeService));

                TarefaService tarefaService = new TarefaService(new TarefaDaoImpl(ds), new ProjetoDaoImpl(ds), new UsuarioDaoImpl(ds));
                abas.addTab("📋 Tarefas", new PainelTarefas(tarefaService, new ProjetoDaoImpl(ds), new UsuarioDaoImpl(ds)));

                ProjetoEquipeService projetoEquipeService = new ProjetoEquipeService(new br.com.projetogestao.dao.ProjetoEquipeDaoImpl(ds));
                abas.addTab("Alocações", new AlocacoesPanel(projetoEquipeService, new ProjetoDaoImpl(ds), new EquipeDaoImpl(ds)));
            } else {
                abas.addTab("Projetos", createStyledPlaceholder("(Preview) Gestão de Projetos - Desabilitado sem banco", backgroundColor));
                abas.addTab("Equipes", createStyledPlaceholder("(Preview) Gestão de Equipes - Desabilitado sem banco", backgroundColor));
                abas.addTab("Alocações", createStyledPlaceholder("(Preview) Gestão de Alocações - Desabilitado sem banco", backgroundColor));
            }
            if (!preview) {
                var ds = DataSourceProvider.getInstance().getDataSource();
                abas.addTab("Relatórios", new RelatoriosPanel(new ProjetoDaoImpl(ds), new TarefaDaoImpl(ds), new UsuarioDaoImpl(ds)));
            } else {
                abas.addTab("Relatórios", createStyledPlaceholder("(Preview) Relatórios e Dashboards - Desabilitado sem banco", backgroundColor));
            }
        }

        if (preview) {
            abas.addTab("Tarefas", createStyledPlaceholder("(Preview) Gestão de Tarefas", backgroundColor));
        }

        // Painel de conteúdo principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(abas, BorderLayout.CENTER);
        
        // Painel de status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.setBorder(createCompoundBorder(
            createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            createEmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel statusLabel = new JLabel("© 2025 Sistema de Gestão de Projetos - v1.0 - UAM");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Adicionar todos os painéis ao painel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Adicionar painel principal à janela
        add(mainPanel);
    }
    
    private JPanel createStyledPlaceholder(String texto, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(createEmptyBorder(30, 30, 30, 30));
        
        JLabel label = new JLabel(texto, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 16));
        label.setForeground(new Color(150, 150, 150));
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }

    // Painel de Dashboard
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(248, 249, 250));
        dashboard.setBorder(createEmptyBorder(20, 20, 20, 20));
        
        // Título do Dashboard
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(248, 249, 250));
        titlePanel.setBorder(createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Dashboard - Sistema de Gestão de Projetos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Painel de estatísticas
        JPanel statsPanel = new JPanel(new java.awt.GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(new Color(248, 249, 250));
        
        // Cards de estatísticas com dados reais
        String totalProjetos = preview ? "5" : obterTotalProjetos();
        String tarefasAtivas = preview ? "12" : obterTarefasAtivas();
        String usuariosAtivos = preview ? "8" : obterUsuariosAtivos();
        String relatoriosDisponiveis = preview ? "Disponíveis" : "Disponíveis";
        
        JPanel projectsCard = createStatCard(" Projetos", "Total: " + totalProjetos, new Color(0, 123, 255));
        JPanel tasksCard = createStatCard(" Tarefas", "Ativas: " + tarefasAtivas, new Color(40, 167, 69));
        JPanel usersCard = createStatCard(" Usuários", "Ativos: " + usuariosAtivos, new Color(255, 193, 7));
        JPanel reportsCard = createStatCard(" Relatórios", relatoriosDisponiveis, new Color(220, 53, 69));
        
        statsPanel.add(projectsCard);
        statsPanel.add(tasksCard);
        statsPanel.add(usersCard);
        statsPanel.add(reportsCard);
        
        // Painel de ações rápidas
        JPanel actionsPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        actionsPanel.setBackground(new Color(248, 249, 250));
        actionsPanel.setBorder(createTitledBorder(createLineBorder(new Color(206, 212, 218)), "Ações Rápidas"));
        
        javax.swing.JButton newProjectBtn = new javax.swing.JButton("Novo Projeto");
        newProjectBtn.setFont(new Font("Arial", Font.BOLD, 12));
        newProjectBtn.setBackground(new Color(0, 123, 255));
        newProjectBtn.setForeground(Color.BLACK);
        newProjectBtn.setOpaque(true);
        newProjectBtn.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        
        javax.swing.JButton viewReportsBtn = new javax.swing.JButton("Ver Relatórios");
        viewReportsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        viewReportsBtn.setBackground(new Color(40, 167, 69));
        viewReportsBtn.setForeground(Color.BLACK);
        viewReportsBtn.setOpaque(true);
        viewReportsBtn.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        
        actionsPanel.add(newProjectBtn);
        actionsPanel.add(viewReportsBtn);
        
        // Montagem do dashboard
        dashboard.add(titlePanel, BorderLayout.NORTH);
        dashboard.add(statsPanel, BorderLayout.CENTER);
        dashboard.add(actionsPanel, BorderLayout.SOUTH);
        
        return dashboard;
    }
    
    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(108, 117, 125));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Métodos para obter dados reais do banco de dados
    private String obterTotalProjetos() {
        try {
            if (preview) return "5";
            ProjetoService projetoService = new ProjetoService(
                new ProjetoDaoImpl(DataSourceProvider.getInstance().getDataSource()),
                new UsuarioDaoImpl(DataSourceProvider.getInstance().getDataSource())
            );
            return String.valueOf(projetoService.listarTodos().size());
        } catch (Exception e) {
            return "Erro";
        }
    }
    
    private String obterTarefasAtivas() {
        try {
            if (preview) return "12";
            TarefaService tarefaService = new TarefaService(
                new TarefaDaoImpl(DataSourceProvider.getInstance().getDataSource()),
                new ProjetoDaoImpl(DataSourceProvider.getInstance().getDataSource()),
                new UsuarioDaoImpl(DataSourceProvider.getInstance().getDataSource())
            );
            long tarefasAtivas = tarefaService.listarTodas().stream()
                .filter(t -> t.getStatus() != br.com.projetogestao.model.StatusTarefa.CONCLUIDA)
                .count();
            return String.valueOf(tarefasAtivas);
        } catch (Exception e) {
            return "Erro";
        }
    }
    
    private String obterUsuariosAtivos() {
        try {
            if (preview) return "8";
            UsuarioService usuarioService = new UsuarioService(
                new UsuarioDaoImpl(DataSourceProvider.getInstance().getDataSource())
            );
            return String.valueOf(usuarioService.listarTodos().size());
        } catch (Exception e) {
            return "Erro";
        }
    }
}




