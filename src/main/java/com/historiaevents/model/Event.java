package com.historiaevents.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String nome;
    private LocalDate data; // Usar apenas um campo de data
    private String descricao;

    // Construtor
    public Event(String nome, LocalDate data, String descricao) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
    }

    // Construtor com String
    public Event(String nome, String data, String descricao) {
        this.nome = nome;
        this.data = LocalDate.parse(data, DATE_FORMATTER);
        this.descricao = descricao;
    }

    // Métodos getter
    public String getNome() {
        return nome;
    }

    public LocalDate getData() {
        return data; // Ajuste o método para retornar apenas a data
    }

    public String getDescricao() {
        return descricao;
    }
}
