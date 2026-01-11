package com.app.biblio.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table (name = "book")
public class Book {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() {
        return id;
    }
    private String titre;
    
    public void setTitre(String name) {
        this.titre = name;
    }
    public String getTitre() {
        return titre;
    }
    
    
}
