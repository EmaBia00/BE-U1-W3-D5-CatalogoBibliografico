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

    // Metodo per controllare se un elemento con lo stesso codiceISBN esiste già prima di salvarlo
    public boolean esisteElementoConISBN(String codiceISBN) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM ElementoCatalogo e WHERE e.codiceISBN = :isbn", Long.class);
        query.setParameter("isbn", codiceISBN);
        return query.getSingleResult() > 0;
    }

    // Metodo per rimuovere un elemento dal catalogo usando il codice ISBN
    public void rimuoviElementoConPrestiti(String codiceISBN) {
        // Trova l'elemento
        TypedQuery<ElementoCatalogo> query = em.createQuery(
                "SELECT e FROM ElementoCatalogo e WHERE e.codiceISBN = :isbn", ElementoCatalogo.class);
        query.setParameter("isbn", codiceISBN);
        ElementoCatalogo elemento = query.getSingleResult();

        if (elemento != null) {
            em.createQuery("DELETE FROM Prestito p WHERE p.elementoPrestato.id = :elementoId")
                    .setParameter("elementoId", elemento.getId())
                    .executeUpdate();
            em.remove(elemento);
        }
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

    // Metodo per cercare prestiti scaduti e non restituiti
    public List<Prestito> ricercaPrestitiScaduti() {
        return em.createQuery(
                        "SELECT p FROM Prestito p WHERE p.dataRestituzionePrevista < CURRENT_DATE AND p.dataRestituzioneEffettiva IS NULL",
                        Prestito.class)
                .getResultList();
    }

    // Metodo per verificare se un utente esiste già nel database
    public boolean esisteUtenteConNumeroTessera(String numeroTessera) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Utente u WHERE u.numeroTessera = :numeroTessera", Long.class);
        query.setParameter("numeroTessera", numeroTessera);
        return query.getSingleResult() > 0;
    }
}
