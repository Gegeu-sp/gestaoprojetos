package br.com.projetogestao.dao;

import br.com.projetogestao.model.Equipe;

import java.util.List;
import java.util.Optional;

public interface EquipeDao {
    void salvar(Equipe equipe);
    Optional<Equipe> buscarPorId(Long id);
    List<Equipe> listarTodas();
    void excluir(Long id);
    void adicionarMembro(Long equipeId, Long usuarioId);
    void removerMembro(Long equipeId, Long usuarioId);
    List<Long> listarMembros(Long equipeId);
}


