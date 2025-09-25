package br.com.projetogestao.service;

import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioDao usuarioDao;

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public List<Usuario> listarTodos() {
        return usuarioDao.listarTodos();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDao.buscarPorId(id);
    }

    public void salvarNovo(String nome, String cpf, String email, String cargo, String login, String senha, Perfil perfil, java.time.LocalDate dataNascimento) {
        validarCampos(nome, cpf, email, cargo, login, senha, perfil);
        Usuario u = new Usuario();
        u.setNomeCompleto(nome.trim());
        u.setCpf(cpf.trim());
        u.setEmail(email.trim());
        u.setCargo(cargo.trim());
        u.setLogin(login.trim());
        u.setSenhaHash(BCrypt.hashpw(senha, BCrypt.gensalt()));
        u.setPerfil(perfil);
        u.setDataNascimento(dataNascimento);
        usuarioDao.salvar(u);
    }

    public void atualizarExistente(Long id, String nome, String cpf, String email, String cargo, String login, String novaSenhaOuNull, Perfil perfil, java.time.LocalDate dataNascimento) {
        Objects.requireNonNull(id, "id obrigatorio");
        validarCampos(nome, cpf, email, cargo, login, perfil);
        Usuario u = usuarioDao.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        u.setNomeCompleto(nome.trim());
        u.setCpf(cpf.trim());
        u.setEmail(email.trim());
        u.setCargo(cargo.trim());
        u.setLogin(login.trim());
        if (novaSenhaOuNull != null && !novaSenhaOuNull.isEmpty()) {
            u.setSenhaHash(BCrypt.hashpw(novaSenhaOuNull, BCrypt.gensalt()));
        }
        u.setPerfil(perfil);
        u.setDataNascimento(dataNascimento);
        usuarioDao.salvar(u);
    }

    public void excluir(Long id) {
        Objects.requireNonNull(id, "id obrigatorio");
        usuarioDao.excluir(id);
    }

    private void validarCampos(String nome, String cpf, String email, String cargo, String login, Perfil perfil) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatorio");
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF obrigatorio");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email obrigatorio");
        if (cargo == null || cargo.isBlank()) throw new IllegalArgumentException("Cargo obrigatorio");
        if (login == null || login.isBlank()) throw new IllegalArgumentException("Login obrigatorio");
        if (perfil == null) throw new IllegalArgumentException("Perfil obrigatorio");
    }

    private void validarCampos(String nome, String cpf, String email, String cargo, String login, String senha, Perfil perfil) {
        validarCampos(nome, cpf, email, cargo, login, perfil);
        if (senha == null || senha.length() < 6) throw new IllegalArgumentException("Senha deve ter ao menos 6 caracteres");
    }
}


