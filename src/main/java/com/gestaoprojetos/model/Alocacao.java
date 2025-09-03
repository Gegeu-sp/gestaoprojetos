package com.gestaoprojetos.model;

public class Alocacao {
    private int id;
    private Projeto projeto;
    private Equipe equipe;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    
    // Construtores
    public Alocacao() {}
    
    public Alocacao(Projeto projeto, Equipe equipe, LocalDate dataInicio, LocalDate dataTermino) {
        this.projeto = projeto;
        this.equipe = equipe;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    
    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
    
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDate getDataTermino() { return dataTermino; }
    public void setDataTermino(LocalDate dataTermino) { this.dataTermino = dataTermino; }
}