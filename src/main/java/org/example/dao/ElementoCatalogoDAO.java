package org.example.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.example.entities.ElementoCatalogo;
import org.example.entities.Libro;
import org.example.entities.Prestito;
import org.example.entities.Utente;

import java.util.List;

public class ElementoCatalogoDAO {
    private EntityManager em;

    public ElementoCatalogoDAO(EntityManager em) {
        this.em = em;
    }

    // Metodo per salvare un elemento nel catalogo
    public void salvaElemento(ElementoCatalogo elemento) {
        em.getTransaction().begin();
        em.persist(elemento);
        em.getTransaction().commit();
    }

    // Metodo per rimuovere un elemento dal catalogo usando il codice ISBN
    public void rimuoviElemento(String codiceISBN) {
        em.getTransaction().begin();
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE e.codiceISBN = :isbn", ElementoCatalogo.class);
        query.setParameter("isbn", codiceISBN);
        ElementoCatalogo elemento = query.getSingleResult();
        if (elemento != null) {
            em.remove(elemento);
        }
        em.getTransaction().commit();
    }

    // Metodo per cercare un elemento del catalogo tramite codice ISBN
    public ElementoCatalogo ricercaPerISBN(String codiceISBN) {
        return em.createQuery(
                        "SELECT e FROM ElementoCatalogo e WHERE e.codiceISBN = :isbn", ElementoCatalogo.class)
                .setParameter("isbn", codiceISBN)
                .getSingleResult();
    }

    // Metodo per cercare elementi per titolo o parte di esso
    public List<ElementoCatalogo> ricercaPerTitolo(String titolo) {
        return em.createQuery(
                        "SELECT e FROM ElementoCatalogo e WHERE e.titolo LIKE :titolo", ElementoCatalogo.class)
                .setParameter("titolo", "%" + titolo + "%")
                .getResultList();
    }

    // Metodo per cercare elementi per anno di pubblicazione
    public List<ElementoCatalogo> ricercaPerAnno(int anno) {
        return em.createQuery(
                        "SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno", ElementoCatalogo.class)
                .setParameter("anno", anno)
                .getResultList();
    }

    // Metodo per cercare libri per autore
    public List<Libro> ricercaPerAutore(String autore) {
        return em.createQuery(
                        "SELECT l FROM Libro l WHERE l.autore = :autore", Libro.class)
                .setParameter("autore", autore)
                .getResultList();
    }

    // Metodo per cercare prestiti associati a un utente specifico
    public List<Prestito> ricercaPrestitiPerUtente(Utente utente) {
        return em.createQuery(
                        "SELECT p FROM Prestito p JOIN p.utenti u WHERE u.id = :utenteId", Prestito.class)
                .setParameter("utenteId", utente.getId())
                .getResultList();
    }

    // Metodo per cercare prestiti attivi (non restituiti)
    public List<Prestito> ricercaPrestitiAttivi() {
        return em.createQuery(
                        "SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL", Prestito.class)
                .getResultList();
    }

    // Metodo per cercare prestiti scaduti e non restituiti
    public List<Prestito> ricercaPrestitiScaduti() {
        return em.createQuery(
                        "SELECT p FROM Prestito p WHERE p.dataRestituzionePrevista < CURRENT_DATE AND p.dataRestituzioneEffettiva IS NULL",
                        Prestito.class)
                .getResultList();
    }
}
