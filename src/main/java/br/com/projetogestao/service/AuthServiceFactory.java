package br.com.projetogestao.service;

import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;

import java.util.Optional;

public final class AuthServiceFactory {
    private AuthServiceFactory() {}

    public static AuthService preview() {
        // UsuarioDao fake somente para preview de UI (sem DB)
        UsuarioDao fakeDao = new UsuarioDao() {
            @Override public Optional<Usuario> buscarPorLogin(String login) {
                if ("admin".equals(login)) {
                    Usuario u = new Usuario();
                    u.setId(1L);
                    u.setLogin("admin");
                    u.setNomeCompleto("Administrador (Preview)");
                    u.setPerfil(Perfil.ADMINISTRADOR);
                    // senha em AuthService.autenticar usa hash; aqui vamos burlar retornando hash vazio e validar no service
                    u.setSenhaHash("$2a$10$abcdefghijklmnopqrstuv");
                    return Optional.of(u);
                }
                return Optional.empty();
            }
            @Override public void salvar(Usuario usuario) {}
            @Override public long contarAdministradores() { return 1; }
            @Override public Optional<Usuario> buscarPorId(Long id) { return Optional.empty(); }
            @Override public java.util.List<Usuario> listarTodos() { return java.util.Collections.emptyList(); }
            @Override public void excluir(Long id) {}
        };

        return new AuthService(fakeDao) {
            @Override
            public Optional<Usuario> autenticar(String login, String senha) {
                if ("admin".equals(login) && "admin123".equals(senha)) {
                    return fakeDao.buscarPorLogin("admin");
                }
                return Optional.empty();
            }
            @Override
            public void garantirAdminPadrao() {
                // nada em preview
            }
        };
    }
}


