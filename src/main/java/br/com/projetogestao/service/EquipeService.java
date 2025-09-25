package br.com.projetogestao.service;

import br.com.projetogestao.dao.EquipeDao;
import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Equipe;
import br.com.projetogestao.model.Usuario;

import java.util.List;
import java.util.Objects;

public class EquipeService {
    private final EquipeDao equipeDao;
    private final UsuarioDao usuarioDao;

    public EquipeService(EquipeDao equipeDao, UsuarioDao usuarioDao) {
        this.equipeDao = equipeDao;
        this.usuarioDao = usuarioDao;
    }

    public List<Equipe> listarTodas() { return equipeDao.listarTodas(); }
    public Equipe salvar(Equipe equipe) { Objects.requireNonNull(equipe); equipeDao.salvar(equipe); return equipe; }
    public void excluir(Long id) { equipeDao.excluir(id); }

    public List<Usuario> listarUsuarios() { return usuarioDao.listarTodos(); }
    public List<Long> listarMembros(Long equipeId) { return equipeDao.listarMembros(equipeId); }
    public void definirMembros(Long equipeId, List<Long> novosMembros) {
        List<Long> atuais = equipeDao.listarMembros(equipeId);
        for (Long u : atuais) if (!novosMembros.contains(u)) equipeDao.removerMembro(equipeId, u);
        for (Long u : novosMembros) if (!atuais.contains(u)) equipeDao.adicionarMembro(equipeId, u);
    }
}


