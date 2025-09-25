package br.com.projetogestao;

import br.com.projetogestao.config.AppProperties;
import br.com.projetogestao.config.DataSourceProvider;
import br.com.projetogestao.dao.UsuarioDaoImpl;
import br.com.projetogestao.db.ExampleSeeder;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import br.com.projetogestao.service.AuthService;
import br.com.projetogestao.service.AuthServiceFactory;
import br.com.projetogestao.ui.LoginFrame;
import br.com.projetogestao.ui.MainFrame;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principal do Sistema de Gestão de Projetos
 * Responsável por inicializar a aplicação e configurar o ambiente
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    /**
     * Enum para definir o modo de operação da aplicação
     * Evita o uso de "magic booleans" nos construtores
     */
    public enum ModoOperacao {
        NORMAL, PREVIEW
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel do sistema
        configurarLookAndFeel();
        
        // Verificar se deve executar em modo preview
        boolean preview = args.length > 0 && "preview".equals(args[0]);
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (preview) {
                    // Modo preview - sem conexão com banco de dados
                    iniciarModoPreviewDireto();
                } else {
                    // Modo normal - com conexão ao banco de dados
                    iniciarAplicacaoNormal();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erro ao inicializar a aplicação", e);
                System.err.println("Erro ao inicializar a aplicação: " + e.getMessage());
                System.exit(1);
            }
        });
    }
    
    private static void configurarLookAndFeel() {
        try {
            // Usar o Look and Feel do sistema operacional
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.log(Level.WARNING, "Não foi possível configurar o Look and Feel do sistema", e);
        }
    }
    
    /**
     * Inicia a aplicação em modo normal com conexão ao banco de dados
     */
    private static void iniciarAplicacaoNormal() {
        logger.info("Iniciando aplicação em modo normal");
        
        try {
            // Conectar ao banco de dados
            DataSource dataSource = DataSourceProvider.getInstance().getDataSource();
            logger.info("Conexão com banco de dados estabelecida com sucesso");
            
            // Criar serviço de autenticação e garantir admin padrão
            AuthService authService = new AuthService(new UsuarioDaoImpl(dataSource));
            authService.garantirAdminPadrao();
            
            // Executar seeder de dados de exemplo se configurado
            if (AppProperties.getInstance().isSeedExampleEnabled()) {
                logger.info("Executando seeder de dados de exemplo");
                ExampleSeeder seeder = new ExampleSeeder(dataSource);
                seeder.seed();
                logger.info("Dados de exemplo inseridos com sucesso");
            }
            
            // Exibir tela de login
            LoginFrame loginFrame = new LoginFrame(authService);
            exibirJanela(loginFrame);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao conectar com o banco de dados", e);
            oferecerModoPreviewAlternativo(e.getMessage());
        }
    }
    
    /**
     * Inicia automaticamente em modo preview após uma falha de conexão
     */
    private static void oferecerModoPreviewAlternativo(String erroMsg) {
        logger.warning("Não foi possível conectar ao banco de dados. Erro: " + erroMsg);
        logger.info("Iniciando automaticamente em modo preview (sem dados reais)");
        
        // Usar AuthService preview automaticamente
        AuthService authServicePreview = AuthServiceFactory.preview();
        LoginFrame loginFrame = new LoginFrame(authServicePreview, true);
        exibirJanela(loginFrame);
    }
    
    /**
     * Inicia a aplicação diretamente na tela principal, sem login
     * Usado quando o argumento 'preview' é passado na inicialização
     */
    private static void iniciarModoPreviewDireto() {
        logger.info("Iniciando aplicação diretamente em modo preview via argumento");
        Usuario usuarioPreview = criarUsuarioPreview();
        MainFrame mainFrame = new MainFrame(usuarioPreview, true);
        exibirJanela(mainFrame);
    }
    
    /**
     * Cria um usuário de exemplo para o modo preview
     */
    private static Usuario criarUsuarioPreview() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Usuário Preview");
        usuario.setLogin("preview");
        usuario.setPerfil(Perfil.ADMINISTRADOR);
        usuario.setCpf("000.000.000-00");
        return usuario;
    }
    
    /**
     * Método utilitário para centralizar a exibição de janelas
     * Evita duplicação de código
     */
    private static void exibirJanela(JFrame frame) {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}