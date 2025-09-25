package br.com.projetogestao.service;

import br.com.projetogestao.dao.TarefaDao;
import br.com.projetogestao.dao.ProjetoDao;
import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Tarefa;
import br.com.projetogestao.model.StatusTarefa;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TarefaService {
    private final TarefaDao tarefaDao;
    private final ProjetoDao projetoDao;

    public TarefaService(TarefaDao tarefaDao, ProjetoDao projetoDao, UsuarioDao usuarioDao) {
        this.tarefaDao = tarefaDao; this.projetoDao = projetoDao;
    }

    public List<Tarefa> listarPorProjeto(Long projetoId) { return tarefaDao.buscarPorProjeto(projetoId); }
    public List<Tarefa> listarPorResponsavel(Long usuarioId) { return tarefaDao.buscarPorResponsavel(usuarioId); }
    public List<Tarefa> listarTodas() { return tarefaDao.listarTodas(); }
    public void excluir(Long id) { tarefaDao.excluir(id); }

    public void salvarNovo(String titulo, String descricao, Long projetoId, Long responsavelId, StatusTarefa status,
                           LocalDate inicioPrev, LocalDate fimPrev, LocalDate inicioReal, LocalDate fimReal) {
        validar(titulo, projetoId, status);
        Tarefa t = new Tarefa();
        t.setTitulo(titulo.trim()); t.setDescricao(descricao);
        t.setProjetoId(projetoId); t.setResponsavelId(responsavelId);
        t.setStatus(status);
        t.setDataInicioPrevista(inicioPrev); t.setDataTerminoPrevista(fimPrev);
        t.setDataInicioReal(inicioReal); t.setDataTerminoReal(fimReal);
        tarefaDao.salvar(t);
    }

    public void atualizarExistente(Long id, String titulo, String descricao, Long projetoId, Long responsavelId, StatusTarefa status,
                                   LocalDate inicioPrev, LocalDate fimPrev, LocalDate inicioReal, LocalDate fimReal) {
        Objects.requireNonNull(id);
        validar(titulo, projetoId, status);
        Tarefa t = tarefaDao.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Tarefa nao encontrada"));
        t.setTitulo(titulo.trim()); t.setDescricao(descricao);
        t.setProjetoId(projetoId); t.setResponsavelId(responsavelId);
        t.setStatus(status);
        t.setDataInicioPrevista(inicioPrev); t.setDataTerminoPrevista(fimPrev);
        t.setDataInicioReal(inicioReal); t.setDataTerminoReal(fimReal);
        tarefaDao.salvar(t);
    }

    private void validar(String titulo, Long projetoId, StatusTarefa status) {
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("Titulo obrigatorio");
        if (projetoId == null || projetoDao.buscarPorId(projetoId).isEmpty()) throw new IllegalArgumentException("Projeto invalido");
        if (status == null) throw new IllegalArgumentException("Status obrigatorio");
    }
}


