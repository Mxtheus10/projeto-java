package com.historiaevents.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.historiaevents.model.Person;

public class PersonController {
    private Connection connection;

    // Construtor que estabelece a conexão com o banco de dados
    public PersonController() {
        try {
            // Atualize com as suas credenciais
            String url = "jdbc:mysql://localhost:3306/projetocesar"; // URL do banco de dados
            String user = "root"; // Usuário do banco de dados
            String password = "JOAOcoca33"; // Senha do banco de dados
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar uma pessoa ao banco de dados
    public void addPerson(Person person) {
        String query = "INSERT INTO pessoas (nome, idade) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, person.getNome());
            statement.setInt(2, person.getIdade());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para remover uma pessoa pelo nome
    public void removePerson(String nome) {
        String query = "DELETE FROM pessoas WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obter todas as pessoas do banco de dados
    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM pessoas";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                int idade = resultSet.getInt("idade");
                persons.add(new Person(nome, idade));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }
}
