package br.com.projetogestao.dao;

import br.com.projetogestao.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDao {
    Optional<Usuario> buscarPorLogin(String login);
    void salvar(Usuario usuario);
    long contarAdministradores();
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
    void excluir(Long id);
}


