package com.historiaevents.model;

public class Person {
    private String nome;
    private int idade;

    // Construtor
    public Person(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    // Método toString para exibir informações da pessoa
    @Override
    public String toString() {
        return "Nome: " + nome + ", Idade: " + idade;
    }

    // Método para validar a idade (opcional)
    public boolean isValidAge() {
        return idade >= 0; // A idade deve ser um valor não negativo
    }
}
