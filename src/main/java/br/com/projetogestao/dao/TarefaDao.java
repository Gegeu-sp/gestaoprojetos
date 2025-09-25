package br.com.projetogestao.dao;

import br.com.projetogestao.model.Tarefa;

import java.util.List;
import java.util.Optional;

public interface TarefaDao {
    void salvar(Tarefa tarefa);
    Optional<Tarefa> buscarPorId(Long id);
    List<Tarefa> buscarPorProjeto(Long projetoId);
    List<Tarefa> buscarPorResponsavel(Long usuarioId);
    List<Tarefa> listarTodas();
    void excluir(Long id);
}


