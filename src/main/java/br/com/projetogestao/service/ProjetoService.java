package br.com.projetogestao.service;

import br.com.projetogestao.dao.ProjetoDao;
import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Projeto;
import br.com.projetogestao.model.StatusProjeto;
import br.com.projetogestao.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjetoService {
    private final ProjetoDao projetoDao;
    private final UsuarioDao usuarioDao;

    public ProjetoService(ProjetoDao projetoDao, UsuarioDao usuarioDao) {
        this.projetoDao = projetoDao;
        this.usuarioDao = usuarioDao;
    }

    public List<Projeto> listarTodos() { return projetoDao.listarTodos(); }
    public Optional<Projeto> buscarPorId(Long id) { return projetoDao.buscarPorId(id); }
    public void excluir(Long id) { projetoDao.excluir(id); }

    public List<Usuario> listarGerentes() {
        return usuarioDao.listarTodos().stream().filter(u -> u.getPerfil() == Perfil.GERENTE || u.getPerfil() == Perfil.ADMINISTRADOR).collect(Collectors.toList());
    }

    public void salvarNovo(String nome, String descricao, String dataInicio, String dataPrevista, StatusProjeto status, Long gerenteId) {
        validar(nome, dataInicio, status, gerenteId);
        Projeto p = new Projeto();
        p.setNome(nome.trim());
        p.setDescricao(descricao == null ? null : descricao.trim());
        p.setDataInicio(parseDate(dataInicio));
        p.setDataTerminoPrevista(dataPrevista == null || dataPrevista.isBlank() ? null : parseDate(dataPrevista));
        p.setStatus(status);
        p.setGerenteId(gerenteId);
        projetoDao.salvar(p);
    }

    public void atualizarExistente(Long id, String nome, String descricao, String dataInicio, String dataPrevista, String dataReal, StatusProjeto status, Long gerenteId) {
        Objects.requireNonNull(id, "id obrigatorio");
        validar(nome, dataInicio, status, gerenteId);
        Projeto p = projetoDao.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Projeto nao encontrado"));
        p.setNome(nome.trim());
        p.setDescricao(descricao == null ? null : descricao.trim());
        p.setDataInicio(parseDate(dataInicio));
        p.setDataTerminoPrevista(dataPrevista == null || dataPrevista.isBlank() ? null : parseDate(dataPrevista));
        p.setDataTerminoReal(dataReal == null || dataReal.isBlank() ? null : parseDate(dataReal));
        p.setStatus(status);
        p.setGerenteId(gerenteId);
        projetoDao.salvar(p);
    }

    private void validar(String nome, String dataInicio, StatusProjeto status, Long gerenteId) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatorio");
        if (dataInicio == null || dataInicio.isBlank()) throw new IllegalArgumentException("Data de inicio obrigatoria");
        if (status == null) throw new IllegalArgumentException("Status obrigatorio");
        if (gerenteId == null) throw new IllegalArgumentException("Gerente obrigatorio");
        parseDate(dataInicio);
    }

    private LocalDate parseDate(String s) {
        try { return LocalDate.parse(s); } catch (DateTimeParseException e) { throw new IllegalArgumentException("Data invalida. Use AAAA-MM-DD"); }
    }
}


