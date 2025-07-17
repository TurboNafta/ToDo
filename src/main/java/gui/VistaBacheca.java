package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Calendar;

/**
 * Classe GUI per la VistaBacheca, che ci permette di entrare nella bacheca selezionata e di vedere i to do contenuti all'interno
 */
public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox<String> comboBoxOrdina;
    private JButton tornaAllaHomeButton;
    private JButton creaToDoButton;
    private JButton buttonCerca;
    private JPanel todoPanelris;
    private JComboBox<String> ricercaComboBox;
    private JLabel OrdinaLabel;

    //JFrame e Controller
    public final JFrame frameVista;
    public final JFrame frameChiamante;
    private Controller controller;
    private String utenteLoggato;
    private Bacheca bacheca;

    /**
     * Costruttore della GUI VistaBacheca
     */
    public VistaBacheca(Bacheca bacheca, Controller controller, JFrame frame, String utenteLoggato) {
        this.frameChiamante = frame;
        this.controller = controller;
        this.utenteLoggato = utenteLoggato;
        this.bacheca = bacheca;

        frameVista = new JFrame("Selezione ToDo");
        frameVista.setContentPane(principale);
        todoPanelris.setLayout(new BoxLayout(todoPanelris, BoxLayout.X_AXIS));
        frameVista.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameVista.setSize(900, 600);
        frameVista.setLocationRelativeTo(null);
        todoPanelris.setLayout(new BoxLayout(todoPanelris, BoxLayout.Y_AXIS));
        frameVista.setVisible(true);

        // --- NUOVO: ComboBox criteri di ricerca ---
        String[] criteri = {CriteriRicerca.TITOLO, CriteriRicerca.SCADENZA_OGGI, CriteriRicerca.IN_SCADENZA_ENTRO};
        ricercaComboBox = new JComboBox<>(criteri);
        textField1 = new JTextField(20); // unica barra di ricerca

        // Pulisce e ricostruisce il pannello ricerca
        ricerca.removeAll();
        ricerca.setLayout(new FlowLayout(FlowLayout.LEFT));
        ricerca.add(new JLabel("Criterio:"));
        ricerca.add(ricercaComboBox);
        ricerca.add(textField1);

        buttonCerca = new JButton("Cerca");
        ricerca.add(buttonCerca);
        ricerca.add(new JLabel("Ordina per:"));
        ricerca.add(comboBoxOrdina);

        //combobox filtro ordina
        comboBoxOrdina.removeAllItems();
        comboBoxOrdina.addItem(CriteriOrdinamento.NESSUN_ORDINAMENTO);
        comboBoxOrdina.addItem(CriteriOrdinamento.TITOLO_AZ);
        comboBoxOrdina.addItem(CriteriOrdinamento.DATA_SCADENZA);
        comboBoxOrdina.addItem(CriteriOrdinamento.POSIZIONE);
        comboBoxOrdina.addItem(CriteriOrdinamento.STATO_COMPLETAMENTO);

        // Abilita/disabilita la barra di ricerca in base al criterio
        ricercaComboBox.addActionListener(e -> {
            String criterioRicerca = (String) ricercaComboBox.getSelectedItem();
            if (CriteriRicerca.TITOLO.equals(criterioRicerca) || CriteriRicerca.IN_SCADENZA_ENTRO.equals(criterioRicerca)) {
                textField1.setEnabled(true);
            } else {
                textField1.setText("");
                textField1.setEnabled(false);
            }
        });
        // Default: Titolo selezionato
        ricercaComboBox.setSelectedIndex(0);
        textField1.setEnabled(true);

        //ordinamento live sulla lista generale
        comboBoxOrdina.addActionListener(e -> aggiornaListaToDo());

        /**
         * Pulsante che ci permette di Creare un to do
         */
        creaToDoButton.addActionListener(e -> { //
            CreaToDo quartaGui = new CreaToDo(controller, frameChiamante, bacheca, utenteLoggato);
            quartaGui.getFrameCreaToDo().setVisible(true);
            frameVista.setVisible(false);
        });

        /**
         * Pulsante che ci permette di Cercare i to do a seconda dei criteri selezionati
         */
        buttonCerca.addActionListener(e -> { // Convertito in lambda

            String criterioOrdine = (String) comboBoxOrdina.getSelectedItem();
            String criterioRicerca = (String) ricercaComboBox.getSelectedItem();
            String testo = textField1.getText().trim();
            List<ToDo> risultati = new ArrayList<>();

            //RICERCA PER TITOLO
            try {
                if (CriteriRicerca.TITOLO.equals(criterioRicerca)) {
                    risultati = controller.getToDoPerBachecaUtente(bacheca, testo);
                } else if (CriteriRicerca.SCADENZA_OGGI.equals(criterioRicerca)) {
                    risultati = controller.getToDoInScadenzaOggi(bacheca);
                } else if (CriteriRicerca.IN_SCADENZA_ENTRO.equals(criterioRicerca)) {
                    risultati = controller.getToDoInScadenzaEntro(bacheca, testo);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frameVista, ex.getMessage(), "Errore ricerca", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //ORDINAMENTO
            if (criterioOrdine != null) {
                switch (criterioOrdine) {
                    case CriteriOrdinamento.TITOLO_AZ:
                        risultati.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                        break;
                    case CriteriOrdinamento.DATA_SCADENZA:
                        risultati.sort(Comparator.comparing(ToDo::getDatascadenza));
                        break;
                    case CriteriOrdinamento.POSIZIONE:
                        risultati.sort(Comparator.comparing(t -> ConvertiInNum(t.getPosizione())));
                        break;
                    case CriteriOrdinamento.STATO_COMPLETAMENTO:
                        risultati.sort(Comparator.comparing(ToDo::getStato));//fare qualcosa x mettere prima i nn completati
                        break;
                    default:
                        break;
                }
            }
            mostraListaToDo(risultati);
        });

        /**
         * Pulsante per tornare alla home
         */
        tornaAllaHomeButton.addActionListener(e -> { // Convertito in lambda
            // Crea una nuova SelezioneBacheca aggiornata
            SelezioneBacheca selez = new SelezioneBacheca(controller, null, utenteLoggato);
            selez.getFrameBacheca().setVisible(true);
            frameVista.dispose();
        });
        // Mostra all'avvio
        aggiornaListaToDo();
    }

    /**
     * Metodo che ci mostra la lista di to, do ordinati o meno.
     */
    private void aggiornaListaToDo() {
        try {
            // Prima ricarica i To do dal database
            List<ToDo> todoFromDB = controller.getToDoByBachecaAndUtente(bacheca.getId(), utenteLoggato);
            bacheca.setTodo(todoFromDB);

            // Crea una nuova lista per l'ordinamento
            List<ToDo> lista = new ArrayList<>(bacheca.getTodo());

            // Applica i criteri di ordinamento
            String criterio = (String) comboBoxOrdina.getSelectedItem();
            if (criterio != null) {
                switch (criterio) {
                    case CriteriOrdinamento.TITOLO_AZ:
                        lista.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                        break;
                    case CriteriOrdinamento.DATA_SCADENZA:
                        lista.sort(Comparator.comparing(ToDo::getDatascadenza));
                        break;
                    case CriteriOrdinamento.POSIZIONE:
                        lista.sort(Comparator.comparing(t -> ConvertiInNum(t.getPosizione())));
                        break;
                    case CriteriOrdinamento.STATO_COMPLETAMENTO:
                        lista.sort(Comparator.comparing(ToDo::getStato));
                        break;
                    case CriteriOrdinamento.NESSUN_ORDINAMENTO:
                    default:
                        break;
                }
            }

            // Mostra la lista ordinata
            mostraListaToDo(lista);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frameVista,
                    "Errore durante il caricamento dei ToDo: " + e.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Metodo che ci converte la posizione in un numero
     */
    private int ConvertiInNum(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException _) {
            return 0;
        }
    }

    /**
     * Metodo che ci permette di vedere la lista dei to do
     */
    private void mostraListaToDo(List<ToDo> lista) { // Usare List per buona pratica
        todoPanelris.removeAll();
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(Color.WHITE);

        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("Nessun ToDo presente."));
        } else {
            for (ToDo t : lista) {
                cardsPanel.add(createToDoCard(t)); // Delega la creazione della card a un altro metodo
                cardsPanel.add(Box.createHorizontalStrut(20));
            }
        }

        todoPanelris.add(cardsPanel);
        todoPanelris.revalidate();
        todoPanelris.repaint();
    }

    private JPanel createToDoCard(ToDo t) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90), 2, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Imposta colore sfondo della card
        setCardBackgroundColor(card, t.getColoresfondo());

        // Aggiungi label titolo
        JLabel labelTitolo = new JLabel(t.getTitolo());
        Calendar oggi = Calendar.getInstance();
        labelTitolo.setForeground(t.getDatascadenza().before(oggi) ? Color.RED : Color.BLACK);
        card.add(labelTitolo);

        // Aggiungi dettagli To do
        addToDoDetailsToCard(card, t);

        // Aggiungi pulsanti azione se l'utente è l'autore
        if (t.getAutore() != null && t.getAutore().getUsername().equals(utenteLoggato)) {
            card.add(createModificaButton(t));
            card.add(Box.createVerticalStrut(5));
            card.add(createEliminaButton(t));
            card.add(Box.createVerticalStrut(5));
            card.add(createSpostaButton(t));
        }

        // Aggiungi pulsante per vedere condivisioni
        card.add(Box.createVerticalStrut(5));
        card.add(createCondivisiConButton(t));

        return card;
    }

    /**
     * Metodo per impostare il colore dello sfondo delle bacheche
     */
    private void setCardBackgroundColor(JPanel card, String colore) {
        if (colore != null) {
            Color backgroundColor;
            switch (colore.toLowerCase()) {
                case "rosso":
                    backgroundColor = new Color(255, 153, 153);
                    break;
                case "giallo":
                    backgroundColor = new Color(255, 255, 153);
                    break;
                case "blu":
                    backgroundColor = new Color(153, 204, 255);
                    break;
                case "verde":
                    backgroundColor = new Color(153, 255, 153);
                    break;
                case "arancione":
                    backgroundColor = new Color(255, 204, 153);
                    break;
                case "rosa":
                    backgroundColor = new Color(255, 204, 229);
                    break;
                case "viola":
                    backgroundColor = new Color(204, 153, 255);
                    break;
                case "celeste":
                    backgroundColor = new Color(204, 255, 255);
                    break;
                case "marrone":
                    backgroundColor = new Color(210, 180, 140);
                    break;
                default:
                    backgroundColor = Color.LIGHT_GRAY;
            }
            card.setBackground(backgroundColor);
            card.setOpaque(true);
        } else {
            card.setBackground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Metodo che aggiunge le informazioni della bacheca al Panel
     */
    private void addToDoDetailsToCard(JPanel card, ToDo t) {
        card.add(new JLabel("Titolo: " + t.getTitolo()));
        card.add(new JLabel("Descrizione: " + t.getDescrizione()));
        card.add(new JLabel("URL: " + t.getUrl()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = t.getDatascadenza() != null ? sdf.format(t.getDatascadenza().getTime()) : "N/D";
        card.add(new JLabel("Data Scadenza: " + dataString));
        card.add(new JLabel("Immagine: " + t.getImage()));
        card.add(new JLabel("Stato: " + t.getStatoString()));
    }

    /**
     * Metodo per creare il pulsante per modificare il to do
     */
    private JButton createModificaButton(ToDo t) {
        JButton modificaButton = new JButton("Modifica");
        modificaButton.setBackground(new Color(255, 200, 80));
        modificaButton.setForeground(Color.BLACK);
        modificaButton.setFocusPainted(false);
        modificaButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        modificaButton.addActionListener(ev -> {
            ModificaToDo modificaGui = new ModificaToDo(controller, frameVista, bacheca, utenteLoggato, t);
            modificaGui.getFrameModificaToDo().setVisible(true);
            frameVista.setVisible(false);
        });
        return modificaButton;
    }

    /**
     * Metodo per creare il pulsante per eliminare il to do
     */
    private JButton createEliminaButton(ToDo t) {
        JButton eliminaButton = new JButton("Elimina");
        eliminaButton.setBackground(new Color(255, 80, 80));
        eliminaButton.setForeground(Color.BLACK);
        eliminaButton.setFocusPainted(false);
        eliminaButton.addActionListener(ev -> {
            int conferma = JOptionPane.showConfirmDialog(frameVista, "Vuoi eliminare questo ToDo?", "Conferma", JOptionPane.YES_NO_OPTION);
            if (conferma == JOptionPane.YES_OPTION) {
                controller.eliminaToDo(bacheca, t);
                aggiornaListaToDo();
            }
        });
        return eliminaButton;
    }

    /**
     * Metodo per creare il pulsante per spostare il to do in un'altra bacheca
     */
    private JButton createSpostaButton(ToDo t) {
        JButton spostaButton = new JButton("Sposta in un'altra Bacheca");
        styleButton(spostaButton);
        spostaButton.addActionListener(ev -> gestisciSpostamento(t));
        return spostaButton;
    }

    /**
     * Metodo per stile del bottone
     */
    private void styleButton(JButton button) {
        button.setBackground(new Color(80, 150, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
    }

    /**
     * Metodo che ci permette di scegliere dove spostare quel to do, a seconda del titolo e della descrizione della
     * nuova bacheca
     */
    private void gestisciSpostamento(ToDo t) {
        Utente utente = controller.getUtenteByUsername(utenteLoggato);
        if (utente == null) return;

        utente.setBacheca(controller.getBachecaList("", utenteLoggato));

        List<Bacheca> bacheche = utente.getBacheca();
        System.out.println("DEBUG: Bacheche disponibili per l'utente:");
        for (Bacheca b : bacheche) {
            System.out.println(" - Titolo: '" + b.getTitolo() + "' (id=" + b.getId() + ")");
        }
        String[] titoliBacheca = getTitoliBacheche(bacheche);
        if (titoliBacheca.length == 0) {
            mostraMessaggioNessunaBacheca();
            return;
        }

        String titolo = richiediTitoloBacheca(titoliBacheca);
        // DEBUG: stampa il titolo selezionato
        System.out.println("DEBUG: Titolo bacheca selezionato: '" + titolo + "'");

        if (titolo != null) {
            gestisciSelezioneBacheca(t, bacheche, titolo);
        }
    }

    /**
     * Metodo che ci permette di recuperare l'utente loggato
     */
    private Utente verificaUtente() {
        Utente utente = controller.getUtenteByUsername(utenteLoggato);
        if (utente == null) {
            JOptionPane.showMessageDialog(frameVista,
                    "Errore nel recupero dell'utente",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
        return utente;
    }

    /**
     * Metodo che ci recupera i titoli delle bacheche
     */
    private String[] getTitoliBacheche(List<Bacheca> bacheche) {
        return bacheche.stream()
                .filter(b -> b.getId() != bacheca.getId())
                .map(b -> b.getTitolo().toString())
                .distinct()
                .toArray(String[]::new);
    }

    /**
     * Ci restituisce il messaggio che nessuna bacheca è disponibile
     */
    private void mostraMessaggioNessunaBacheca() {
        JOptionPane.showMessageDialog(frameVista,
                "Non ci sono altre bacheche disponibili.",
                "Nessuna bacheca",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Metodo che ci chiede in quale bacheca fare lo spostamento, per titolo
     */
    private String richiediTitoloBacheca(String[] titoliBacheca) {
        return (String) JOptionPane.showInputDialog(
                frameVista,
                "Scegli bacheca di destinazione: ",
                "Sposta To Do",
                JOptionPane.PLAIN_MESSAGE,
                null,
                titoliBacheca,
                titoliBacheca[0]
        );
    }

    /**
     * Metodo che ci verifica i dati inseriti e completa lo spostamento
     */
    private void gestisciSelezioneBacheca(ToDo t, List<Bacheca> bacheche, String titoloDestinazione) {
        if (titoloDestinazione == null) return;
        List<Bacheca> bachecheCorrispondenti = new ArrayList<>();
        for (Bacheca b : bacheche) {
            String titoloBacheca = b.getTitolo() != null ? b.getTitolo().toString() : null;
            if (titoloBacheca != null &&
                    titoloBacheca.trim().equalsIgnoreCase(titoloDestinazione.trim())) {
                bachecheCorrispondenti.add(b);
            }
        }
        Bacheca bachecaDestinazione = null;
        if (bachecheCorrispondenti.size() == 1) {
            bachecaDestinazione = bachecheCorrispondenti.get(0);
        } else if (bachecheCorrispondenti.size() > 1) {
            String[] descrizioni = new String[bachecheCorrispondenti.size()];
            for (int i = 0; i < bachecheCorrispondenti.size(); i++) {
                descrizioni[i] = bachecheCorrispondenti.get(i).getDescrizione();
            }
            String descScelta = richiediDescrizioneBacheca(descrizioni); // Da implementare!
            for (Bacheca b : bachecheCorrispondenti) {
                if (b.getDescrizione().equals(descScelta)) {
                    bachecaDestinazione = b;
                    break;
                }
            }
        }
        if (bachecaDestinazione == null) {
            // Mostra messaggio errore
            return;
        }
        Bacheca bachecaOrigine = t.getBacheca();
        controller.spostaToDoInAltraBacheca(t, bachecaOrigine, bachecaDestinazione);
        aggiornaListaToDo();
    }

    /**
     * Metodo che prende le descrizioni di tutte le bacheche
     */
    private String[] getDescrizioniBacheca(List<Bacheca> bacheche, String titolo) {
        return bacheche.stream()
                .filter(b -> b.getTitolo().toString().equals(titolo))
                .map(Bacheca::getDescrizione)
                .toArray(String[]::new);
    }

    /**
     * Metodo che ci da errore nessuna bacheca trovata con quel titolo
     */
    private void mostraMessaggioNessunaBachecaTrovata() {
        JOptionPane.showMessageDialog(frameVista,
                "Nessuna bacheca trovata con questo titolo",
                "Errore",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Metodo che ci permette di inserire la descrizione della bacheca a cui si vuole effettuare lo spostamento
     */
    private String richiediDescrizioneBacheca(String[] descrizioni) {
        return (String) JOptionPane.showInputDialog(
                frameVista,
                "Scegli la descrizione della bacheca:",
                "Sposta To Do - Descrizione",
                JOptionPane.PLAIN_MESSAGE,
                null,
                descrizioni,
                descrizioni[0]
        );
    }

    /**
     * Metodo che ci completa lo spostamento e ci restituisce un messaggio come avviso
     */
    private void completaSpostamento(ToDo t, String titolo, String descrizione) {
        Bacheca bachecaDestinazione = controller.getBachecaPerTitoloEDescrizione(
                utenteLoggato,
                titolo,
                descrizione
        );

        if (bachecaDestinazione != null ) {
            controller.spostaToDoInAltraBacheca(t, bacheca, bachecaDestinazione);
           try {
               bacheca.setTodo(controller.getToDoByBacheca(bacheca.getId()));
           } catch (SQLException e){
               e.printStackTrace();
           }
            aggiornaListaToDo();
            mostraMessaggioSuccesso();
        }
    }

    /**
     * Mostra il messaggio dopo aver effettuato lo spostamento con successo
     */
    private void mostraMessaggioSuccesso() {
        JOptionPane.showMessageDialog(
                frameVista,
                "ToDo spostato con successo",
                "Operazione completata",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Pulsante che ci permette di vedere le condivisioni con altri utenti
     */
    private JButton createCondivisiConButton(ToDo t) {
        JButton condivisiButton = new JButton("Condiviso con...");
        condivisiButton.setBackground(new Color(120, 200, 255));
        condivisiButton.setForeground(Color.BLACK);
        condivisiButton.setFocusPainted(false);
        condivisiButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        condivisiButton.addActionListener(ev -> {
            StringBuilder sb = new StringBuilder();
            List<Utente> utentiCondivisi = new ArrayList<>(); // Inizializza una lista vuota

            try {
                utentiCondivisi = controller.getToDoDAO().getUtentiCondivisiByToDoId(t.getTodoId());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frameVista,
                        "Errore durante il recupero delle condivisioni dal database: " + ex.getMessage(),
                        "Errore Database",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (utentiCondivisi.isEmpty()) {
                JOptionPane.showMessageDialog(frameVista,
                        "Non condiviso con nessuno.",
                        "Nessuna Condivisione",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Utente u : utentiCondivisi) {
                sb.append(u.getUsername()).append("\n");
            }

            String messaggio = sb.length() > 0 ? sb.toString() : "Non condiviso con nessuno.";

            JOptionPane.showMessageDialog(frameVista,
                    messaggio,
                    "Utenti con cui è condiviso",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        return condivisiButton;
    }
}

 final class CriteriRicerca {
    private CriteriRicerca() {}
    public static final String TITOLO = "Titolo";
    public static final String SCADENZA_OGGI = "Scadenza oggi";
    public static final String IN_SCADENZA_ENTRO = "In scadenza entro";
}

final class CriteriOrdinamento {
    private CriteriOrdinamento() {}
    public static final String NESSUN_ORDINAMENTO = "Nessun Ordinamento";
    public static final String TITOLO_AZ = "Titolo A-Z";
    public static final String DATA_SCADENZA = "Data di Scadenza";
    public static final String POSIZIONE = "Posizione";
    public static final String STATO_COMPLETAMENTO = "Stato Completamento";
}


