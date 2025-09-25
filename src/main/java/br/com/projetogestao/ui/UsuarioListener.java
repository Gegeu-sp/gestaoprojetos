package br.com.projetogestao.ui;

/**
 * Interface para notificar quando um usuário é cadastrado ou modificado
 */
public interface UsuarioListener {
    /**
     * Método chamado quando um usuário é cadastrado com sucesso
     */
    void onUsuarioCadastrado();
}