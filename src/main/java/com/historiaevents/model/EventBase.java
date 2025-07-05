package com.historiaevents.model;

import java.time.LocalDate;

public class EventBase {
    private int id;
    private String name;
    private String description;
    private LocalDate date;

    // Novos campos adicionados 👇
    private String imagePath;
    private String externalLink;

    public EventBase(int id, String name, String description, LocalDate date) {
        this(id, name, description, date, "", ""); // default vazio para retrocompatibilidade
    }

    public EventBase(int id, String name, String description, LocalDate date, String imagePath, String externalLink) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.imagePath = imagePath;
        this.externalLink = externalLink;
    }

    // Getters e Setters
    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getExternalLink() { return externalLink; }

    public void setExternalLink(String externalLink) { this.externalLink = externalLink; }
}