package interfaces;

import model.Bacheca;

/**
 * Interfaccia per la gestione delle operazioni sulle bacheche di un utente.
 */
public interface InterfacciaUtente {
    /**
     * Aggiunge una bacheca all'utente.
     * @param b bacheca da aggiungere
     */
    void aggiungiBacheca(Bacheca b);

    /**
     * Elimina una bacheca dall'utente.
     * @param b bacheca da eliminare
     */
    void eliminaBacheca(Bacheca b);
}