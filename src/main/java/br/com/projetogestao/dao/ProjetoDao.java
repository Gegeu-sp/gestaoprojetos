package br.com.projetogestao.dao;

import br.com.projetogestao.model.Projeto;

import java.util.List;
import java.util.Optional;

public interface ProjetoDao {
    void salvar(Projeto projeto);
    Optional<Projeto> buscarPorId(Long id);
    List<Projeto> listarTodos();
    void excluir(Long id);
}


