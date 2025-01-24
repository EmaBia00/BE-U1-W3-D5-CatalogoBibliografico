package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.example.dao.ElementoCatalogoDAO;
import org.example.entities.Libro;
import org.example.entities.Prestito;
import org.example.entities.Utente;

import java.time.LocalDate;
import java.util.List;

/**
 * ---------------------------AVVISO----------------------------------
 * NELLA CARTELLA CON IL PERCORSO: "src/main/diagrammaER" E' CONTENUTA
 * LA FOTO DEL DIAGRAMMA ER E UN FILE CON LA SPIEGAZIONE DEL DIAGRAMMA
 *
 * PS Ho dovuto cambiare un po la gestione delle richieste perchè in esecuzione del codice avevo un sacco di errori
 * adesso sembra funzionare ma non so se va bene fatto in questo modo
 */
public class Main {
    public static void main(String[] args) {
        // Creazione dell'EntityManagerFactory e dell'EntityManager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CatalogoBibliografico");
        EntityManager em = emf.createEntityManager();

        try {
            // DAO per gestire gli elementi del catalogo
            ElementoCatalogoDAO catalogoDAO = new ElementoCatalogoDAO(em);

            // 1. Aggiunta di un elemento del catalogo
            Libro libro = new Libro();
            libro.setCodiceISBN("1234567890");
            libro.setTitolo("La Storia Infinita");
            libro.setAnnoPubblicazione(2021);
            libro.setNumeroPagine(350);
            libro.setAutore("Mario Rossi");
            libro.setGenere("Manuale");
            if (!catalogoDAO.esisteElementoConISBN("1234567890")) {
                em.persist(libro);
                System.out.println("Elemento aggiunto al catalogo con il titolo: " + libro.getTitolo());
            } else {
                System.out.println("Elemento con ISBN 1234567890 esiste già nel catalogo.");
            }

            // 2. Rimozione di un elemento del catalogo dato un codice ISBN
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }
            String codiceISBNDaRimuovere = "1234567890";
            catalogoDAO.rimuoviElementoConPrestiti(codiceISBNDaRimuovere);
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            System.out.println("Elemento rimosso con ISBN: " + codiceISBNDaRimuovere);

            // 3. Ricerca per ISBN
            String isbnRicerca = "1234567890";
            try {
                System.out.println("Ricerca per ISBN (" + isbnRicerca + "): " +
                        catalogoDAO.ricercaPerISBN(isbnRicerca));
            } catch (Exception e) {
                System.out.println("Elemento non trovato per ISBN: " + isbnRicerca);
            }

            // 4. Ricerca per anno di pubblicazione
            int annoPubblicazione = 2021;
            System.out.println("Ricerca per anno di pubblicazione (" + annoPubblicazione + "):");
            catalogoDAO.ricercaPerAnno(annoPubblicazione).forEach(e -> System.out.println(e.getTitolo()));

            // 5. Ricerca per autore
            String autoreRicerca = "Mario Rossi";
            System.out.println("Ricerca per autore (" + autoreRicerca + "):");
            catalogoDAO.ricercaPerAutore(autoreRicerca).forEach(l -> System.out.println(l.getTitolo()));

            // 6. Ricerca per titolo o parte di esso
            String parteTitolo = "Storia";
            System.out.println("Ricerca per titolo contenente (" + parteTitolo + "):");
            catalogoDAO.ricercaPerTitolo(parteTitolo).forEach(e -> System.out.println(e.getTitolo()));

            // Creazione di un utente e un prestito per i punti successivi
            if (!catalogoDAO.esisteUtenteConNumeroTessera("UT12345")) {
                Utente utente = new Utente();
                utente.setNome("Emanuele");
                utente.setCognome("Bianchi");
                utente.setDataNascita(LocalDate.of(2000, 10, 25));
                utente.setNumeroTessera("UT12345");
                em.persist(utente);
                System.out.println("Utente aggiunto con numero tessera: " + utente.getNumeroTessera());
                Prestito prestito = new Prestito();
                prestito.setElementoPrestato(libro);
                prestito.setDataInizioPrestito(LocalDate.now());
                prestito.setDataRestituzionePrevista(LocalDate.now().plusDays(30));
                prestito.getUtenti().add(utente);
                utente.getPrestiti().add(prestito);
                em.persist(prestito);

                // 7. Ricerca degli elementi attualmente in prestito dato un numero di tessera utente
                System.out.println("Ricerca elementi in prestito per tessera (UT12345):");
                catalogoDAO.ricercaPrestitiPerUtente(utente).forEach(p -> System.out.println(p.getElementoPrestato().getTitolo()));
            } else {
                System.out.println("Utente con numero tessera UT12345 esiste già nel database.");
            }

            // 8. Ricerca di tutti i prestiti scaduti e non ancora restituiti
            System.out.println("Ricerca prestiti scaduti e non restituiti:");
            List<Prestito> prestitiScaduti = catalogoDAO.ricercaPrestitiScaduti();
            if (prestitiScaduti.isEmpty()) {
                System.out.println("Non ci sono prestiti scaduti e non restituiti.");
            } else {
                prestitiScaduti.forEach(p -> System.out.println("Trovato: " + p.getElementoPrestato().getTitolo()));
            }

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.out.println("Transazione annullata a causa di errore.");
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
