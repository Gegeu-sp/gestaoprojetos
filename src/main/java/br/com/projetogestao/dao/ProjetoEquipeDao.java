package br.com.projetogestao.dao;

import java.util.List;

public interface ProjetoEquipeDao {
    void atribuirEquipeAoProjeto(Long projetoId, Long equipeId);
    void removerEquipeDoProjeto(Long projetoId, Long equipeId);
    List<Long> listarEquipesPorProjeto(Long projetoId);
    List<Long> listarProjetosPorEquipe(Long equipeId);
}


