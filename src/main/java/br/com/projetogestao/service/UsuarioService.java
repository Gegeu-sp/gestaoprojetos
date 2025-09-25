package br.com.projetogestao.service;

import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Serviço responsável pela lógica de negócio relacionada aos usuários.
 * 
 * Esta classe implementa as regras de negócio para:
 * - Criação e atualização de usuários
 * - Validação de dados pessoais (CPF, email, etc.)
 * - Criptografia segura de senhas usando BCrypt
 * - Gestão de perfis de usuário
 * - Operações CRUD completas
 * 
 * <p><strong>Aspectos de Segurança:</strong></p>
 * <ul>
 *   <li>Senhas são criptografadas usando BCrypt com salt automático</li>
 *   <li>Validação rigorosa de CPF com algoritmo de dígitos verificadores</li>
 *   <li>Validação de formato de email</li>
 *   <li>Senhas devem ter no mínimo 6 caracteres</li>
 * </ul>
 * 
 * @author Sistema de Gestão de Projetos
 * @version 1.0
 * @since 2024
 */
public class UsuarioService {
    
    /** DAO para operações de persistência de usuários */
    private final UsuarioDao usuarioDao;
    
    /** Padrão regex para validação de email */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    /** Comprimento mínimo exigido para senhas */
    private static final int SENHA_MIN_LENGTH = 6;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias.
     * 
     * @param usuarioDao DAO para operações de usuários (não pode ser null)
     * @throws NullPointerException se o parâmetro for null
     */
    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = Objects.requireNonNull(usuarioDao, "UsuarioDao não pode ser null");
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     * 
     * @return Lista de todos os usuários, pode estar vazia mas nunca null
     */
    public List<Usuario> listarTodos() {
        return usuarioDao.listarTodos();
    }

    /**
     * Busca um usuário específico pelo seu ID.
     * 
     * @param id ID do usuário a ser buscado
     * @return Optional contendo o usuário se encontrado, Optional.empty() caso contrário
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDao.buscarPorId(id);
    }

    /**
     * Cria um novo usuário no sistema.
     * 
     * <p><strong>Validações realizadas:</strong></p>
     * <ul>
     *   <li>Todos os campos obrigatórios preenchidos</li>
     *   <li>CPF válido (algoritmo de dígitos verificadores)</li>
     *   <li>Email em formato válido</li>
     *   <li>Senha com no mínimo 6 caracteres</li>
     *   <li>Perfil válido</li>
     * </ul>
     * 
     * <p><strong>Segurança:</strong> A senha é automaticamente criptografada 
     * usando BCrypt com salt gerado automaticamente.</p>
     * 
     * @param nome Nome completo do usuário (obrigatório, será trimado)
     * @param cpf CPF do usuário no formato XXX.XXX.XXX-XX (obrigatório)
     * @param email Email válido do usuário (obrigatório)
     * @param cargo Cargo/função do usuário (obrigatório)
     * @param login Login único do usuário (obrigatório)
     * @param senha Senha em texto plano (obrigatória, min. 6 caracteres)
     * @param perfil Perfil de acesso do usuário (obrigatório)
     * @param dataNascimento Data de nascimento (opcional)
     * 
     * @throws IllegalArgumentException se algum dado obrigatório for inválido
     */
    public void salvarNovo(String nome, String cpf, String email, String cargo, 
                          String login, String senha, Perfil perfil, LocalDate dataNascimento) {
        // Validação completa dos campos obrigatórios
        validarCamposCompletos(nome, cpf, email, cargo, login, senha, perfil);
        
        // Criação e configuração do novo usuário
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(nome.trim());
        usuario.setCpf(formatarCpf(cpf.trim()));
        usuario.setEmail(email.trim().toLowerCase()); // Email em minúsculas para consistência
        usuario.setCargo(cargo.trim());
        usuario.setLogin(login.trim());
        
        // Criptografia segura da senha usando BCrypt
        usuario.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt()));
        
        usuario.setPerfil(perfil);
        usuario.setDataNascimento(dataNascimento);
        
        // Persistência no banco de dados
        usuarioDao.salvar(usuario);
    }

    /**
     * Atualiza um usuário existente no sistema.
     * 
     * Busca o usuário pelo ID e atualiza todos os campos fornecidos.
     * A senha só é alterada se uma nova senha for fornecida (não null/vazia).
     * 
     * @param id ID do usuário a ser atualizado (obrigatório)
     * @param nome Nome completo (obrigatório)
     * @param cpf CPF válido (obrigatório)
     * @param email Email válido (obrigatório)
     * @param cargo Cargo/função (obrigatório)
     * @param login Login único (obrigatório)
     * @param novaSenhaOuNull Nova senha ou null para manter a atual
     * @param perfil Perfil de acesso (obrigatório)
     * @param dataNascimento Data de nascimento (opcional)
     * 
     * @throws IllegalArgumentException se o usuário não for encontrado ou dados inválidos
     * @throws NullPointerException se o ID for null
     */
    public void atualizarExistente(Long id, String nome, String cpf, String email, 
                                  String cargo, String login, String novaSenhaOuNull, 
                                  Perfil perfil, LocalDate dataNascimento) {
        // Validação do ID obrigatório
        Objects.requireNonNull(id, "ID do usuário é obrigatório");
        
        // Validação dos campos básicos (sem senha)
        validarCamposBasicos(nome, cpf, email, cargo, login, perfil);
        
        // Se nova senha fornecida, validar também
        if (novaSenhaOuNull != null && !novaSenhaOuNull.isEmpty()) {
            validarSenha(novaSenhaOuNull);
        }
        
        // Busca o usuário existente
        Usuario usuario = usuarioDao.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        
        // Atualização dos campos
        usuario.setNomeCompleto(nome.trim());
        usuario.setCpf(formatarCpf(cpf.trim()));
        usuario.setEmail(email.trim().toLowerCase());
        usuario.setCargo(cargo.trim());
        usuario.setLogin(login.trim());
        
        // Atualiza senha apenas se nova senha foi fornecida
        if (novaSenhaOuNull != null && !novaSenhaOuNull.isEmpty()) {
            usuario.setSenhaHash(BCrypt.hashpw(novaSenhaOuNull, BCrypt.gensalt()));
        }
        
        usuario.setPerfil(perfil);
        usuario.setDataNascimento(dataNascimento);
        
        // Persistência das alterações
        usuarioDao.salvar(usuario);
    }

    /**
     * Remove um usuário do sistema.
     * 
     * @param id ID do usuário a ser removido (obrigatório)
     * @throws NullPointerException se o ID for null
     * @throws IllegalArgumentException se o usuário não existir
     */
    public void excluir(Long id) {
        Objects.requireNonNull(id, "ID do usuário é obrigatório");
        usuarioDao.excluir(id);
    }

    /**
     * Valida todos os campos obrigatórios para criação de usuário.
     * 
     * @param nome Nome completo
     * @param cpf CPF do usuário
     * @param email Email do usuário
     * @param cargo Cargo/função
     * @param login Login único
     * @param senha Senha em texto plano
     * @param perfil Perfil de acesso
     * 
     * @throws IllegalArgumentException se algum campo for inválido
     */
    private void validarCamposCompletos(String nome, String cpf, String email, 
                                       String cargo, String login, String senha, Perfil perfil) {
        validarCamposBasicos(nome, cpf, email, cargo, login, perfil);
        validarSenha(senha);
    }

    /**
     * Valida os campos básicos do usuário (sem senha).
     * 
     * @param nome Nome completo
     * @param cpf CPF do usuário
     * @param email Email do usuário
     * @param cargo Cargo/função
     * @param login Login único
     * @param perfil Perfil de acesso
     * 
     * @throws IllegalArgumentException se algum campo for inválido
     */
    private void validarCamposBasicos(String nome, String cpf, String email, 
                                     String cargo, String login, Perfil perfil) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }
        
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        
        // Validação específica do CPF
        if (!validarCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        // Validação do formato do email
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Email em formato inválido");
        }
        
        if (cargo == null || cargo.isBlank()) {
            throw new IllegalArgumentException("Cargo é obrigatório");
        }
        
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login é obrigatório");
        }
        
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil de usuário é obrigatório");
        }
    }

    /**
     * Valida a senha do usuário.
     * 
     * @param senha Senha em texto plano
     * @throws IllegalArgumentException se a senha for inválida
     */
    private void validarSenha(String senha) {
        if (senha == null || senha.length() < SENHA_MIN_LENGTH) {
            throw new IllegalArgumentException(
                "Senha deve ter no mínimo " + SENHA_MIN_LENGTH + " caracteres"
            );
        }
    }

    /**
     * Valida um CPF usando o algoritmo de dígitos verificadores.
     * 
     * <p>Implementa a validação completa do CPF brasileiro:</p>
     * <ul>
     *   <li>Remove formatação (pontos e hífen)</li>
     *   <li>Verifica se tem 11 dígitos</li>
     *   <li>Verifica se não são todos iguais (111.111.111-11, etc.)</li>
     *   <li>Calcula e valida os dois dígitos verificadores</li>
     * </ul>
     * 
     * @param cpf CPF a ser validado (com ou sem formatação)
     * @return true se o CPF for válido, false caso contrário
     */
    private boolean validarCpf(String cpf) {
        // Remove formatação
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpfLimpo.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calcula primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;
        
        // Calcula segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;
        
        // Verifica se os dígitos calculados conferem
        return Character.getNumericValue(cpfLimpo.charAt(9)) == primeiroDigito &&
               Character.getNumericValue(cpfLimpo.charAt(10)) == segundoDigito;
    }

    /**
     * Formata um CPF para o padrão XXX.XXX.XXX-XX.
     * 
     * @param cpf CPF sem formatação (apenas números)
     * @return CPF formatado
     */
    private String formatarCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() == 11) {
            return cpfLimpo.substring(0, 3) + "." +
                   cpfLimpo.substring(3, 6) + "." +
                   cpfLimpo.substring(6, 9) + "-" +
                   cpfLimpo.substring(9, 11);
        }
        return cpf; // Retorna original se não conseguir formatar
    }
}
