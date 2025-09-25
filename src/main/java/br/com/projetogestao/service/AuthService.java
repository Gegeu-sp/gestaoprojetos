package br.com.projetogestao.service;

import br.com.projetogestao.dao.UsuarioDao;
import br.com.projetogestao.model.Perfil;
import br.com.projetogestao.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UsuarioDao usuarioDao;

    public AuthService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        Optional<Usuario> opt = usuarioDao.buscarPorLogin(login);
        if (opt.isPresent()) {
            Usuario usuario = opt.get();
            if (BCrypt.checkpw(senha, usuario.getSenhaHash())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public void garantirAdminPadrao() {
        if (usuarioDao.contarAdministradores() == 0) {
            Usuario admin = new Usuario();
            admin.setNomeCompleto("Administrador");
            admin.setCpf("000.000.000-00");
            admin.setEmail("admin@example.com");
            admin.setCargo("Administrador");
            admin.setLogin("admin");
            admin.setSenhaHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setPerfil(Perfil.ADMINISTRADOR);
            admin.setDataNascimento(java.time.LocalDate.of(1980, 1, 1));
            usuarioDao.salvar(admin);
        }
    }
}
