package com.gestaoprojetos.model;

import java.time.LocalDate;

public class Tarefa {
    private int id;
    private String titulo;
    private String descricao;
    private Projeto projeto;
    private Usuario responsavel;
    private Status status;
    private LocalDate dataInicioPrevista;
    private LocalDate dataInicioReal;
    private LocalDate dataFimPrevista;
    private LocalDate dataFimReal;
    
    public enum Status {
        PENDENTE, EM_EXECUCAO, CONCLUIDA
    }
    
    // Construtores
    public Tarefa() {}
    
    public Tarefa(String titulo, String descricao, Projeto projeto, Usuario responsavel, 
                 LocalDate dataInicioPrevista, LocalDate dataFimPrevista) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.status = Status.PENDENTE;
        this.dataInicioPrevista = dataInicioPrevista;
        this.dataFimPrevista = dataFimPrevista;
    }
    
    // Getters e Setters
    public int getId() { 
        return id; 
        }
    public void setId(int id) { 
        this.id = id; 
        }

    public String getTitulo() { 
        return titulo; 
        }
    public void setTitulo(String titulo) { 
        this.titulo = titulo; 
        }

    public String getDescricao() { 
        return descricao; 
        }
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
        }

    public Projeto getProjeto() { 
        return projeto; 
        }
    public void setProjeto(Projeto projeto) { 
        this.projeto = projeto; 
        }

    public Usuario getResponsavel() { 
        return responsavel; 
        }
    public void setResponsavel(Usuario responsavel) { 
        this.responsavel = responsavel; 
        }

    public Status getStatus() { 
        return status; 
        }
    public void setStatus(Status status) { 
        this.status = status; 
        }

    public LocalDate getDataInicioPrevista() { 
        return dataInicioPrevista; 
        }
    public void setDataInicioPrevista(LocalDate dataInicioPrevista) { 
        this.dataInicioPrevista = dataInicioPrevista; 
        }

    public LocalDate getDataInicioReal() { 
        return dataInicioReal; 
        }
    public void setDataInicioReal(LocalDate dataInicioReal) { 
        this.dataInicioReal = dataInicioReal; 
        }

    public LocalDate getDataFimPrevista() { 
        return dataFimPrevista; 
        }
    public void setDataFimPrevista(LocalDate dataFimPrevista) { 
        this.dataFimPrevista = dataFimPrevista; 
        }

    public LocalDate getDataFimReal() { 
        return dataFimReal; 
        }
    public void setDataFimReal(LocalDate dataFimReal) { 
        this.dataFimReal = dataFimReal; 
        }

    @Override
    public String toString() {
        return titulo;
    }
}