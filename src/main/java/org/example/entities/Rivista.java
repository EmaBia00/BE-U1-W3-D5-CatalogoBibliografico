package org.example.entities;

import javax.persistence.Entity;
import org.example.enumerate.Periodicita;

@Entity
public class Rivista extends ElementoCatalogo {
    private Periodicita periodicita;

    // Getters e Setters
    public Periodicita getPeriodicita() {
        return periodicita;
    }

    public void setPeriodicita(Periodicita periodicita) {
        this.periodicita = periodicita;
    }
}
