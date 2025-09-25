package br.com.projetogestao.model;

public enum Perfil {
    ADMINISTRADOR,
    GERENTE,
    COLABORADOR;

    public static Perfil fromString(String value) {
        if (value == null) return null;
        switch (value.trim().toUpperCase()) {
            case "ADMINISTRADOR":
            case "ADMINISTRATOR":
            case "ADMIN":
                return ADMINISTRADOR;
            case "GERENTE":
            case "MANAGER":
                return GERENTE;
            case "COLABORADOR":
            case "COLLABORATOR":
            default:
                return COLABORADOR;
        }
    }
}


