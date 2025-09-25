package br.com.projetogestao.service;

import br.com.projetogestao.dao.ProjetoEquipeDao;

import java.util.List;

public class ProjetoEquipeService {
    private final ProjetoEquipeDao projetoEquipeDao;
    public ProjetoEquipeService(ProjetoEquipeDao projetoEquipeDao) { this.projetoEquipeDao = projetoEquipeDao; }

    public List<Long> listarEquipesPorProjeto(Long projetoId) { return projetoEquipeDao.listarEquipesPorProjeto(projetoId); }
    public List<Long> listarProjetosPorEquipe(Long equipeId) { return projetoEquipeDao.listarProjetosPorEquipe(equipeId); }
    public void vincular(Long projetoId, Long equipeId) { projetoEquipeDao.atribuirEquipeAoProjeto(projetoId, equipeId); }
    public void desvincular(Long projetoId, Long equipeId) { projetoEquipeDao.removerEquipeDoProjeto(projetoId, equipeId); }
}


