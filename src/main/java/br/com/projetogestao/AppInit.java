package br.com.projetogestao;

import br.com.projetogestao.service.AuthService;

public class AppInit {
    private final AuthService authService;
    private final boolean preview;

    public AppInit(AuthService authService, boolean preview) {
        this.authService = authService;
        this.preview = preview;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isPreview() {
        return preview;
    }
}


