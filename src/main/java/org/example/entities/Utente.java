package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private LocalDate dataNascita;

    @Column(nullable = false, unique = true)
    private String numeroTessera;

    @ManyToMany(mappedBy = "utenti")
    private Set<Prestito> prestiti = new HashSet<>();

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNumeroTessera() {
        return numeroTessera;
    }

    public void setNumeroTessera(String numeroTessera) {
        this.numeroTessera = numeroTessera;
    }

    public Set<Prestito> getPrestiti() {
        return prestiti;
    }

    public void setPrestiti(Set<Prestito> prestiti) {
        this.prestiti = prestiti;
    }
}
