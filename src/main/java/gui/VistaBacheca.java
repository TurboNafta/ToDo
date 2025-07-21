package gui;

import controller.Controller;
import database.ConnessioneDatabase;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Calendar;

//Classe GUI per la VistaBacheca, che ci permette di entrare nella bacheca selezionata e di vedere i to do contenuti all'interno
public class VistaBacheca {
    private JPanel principale;
    private JPanel ricerca;
    private JTextField textField1;
    private JComboBox<String> comboBoxOrdina;
    private JButton tornaAllaHomeButton;
    private JButton creaToDoButton;
    private JButton buttonCerca;
    private JPanel todoPanelRis;
    private JComboBox<String> ricercaComboBox;
    private JLabel ordinaLabel;

    //JFrame e Controller
    public final JFrame frameVista;
    public final JFrame frameChiamante;
    private final Controller controller;
    private final String utenteLoggato;
    private final Bacheca bacheca;

    private static final String ERRORE_TITOLO = "Errore";
    public static final String NESSUN_ORDINAMENTO = "Nessun Ordinamento";
    public static final String TITOLO_AZ = "Titolo A-Z";
    public static final String DATA_SCADENZA = "Data di Scadenza";
    public static final String POSIZIONE = "Posizione";
    public static final String STATO_COMPLETAMENTO = "Stato Completamento";

    public static final String TITOLO = "Titolo";
    public static final String SCADENZA_OGGI = "Scadenza oggi";
    public static final String IN_SCADENZA_ENTRO = "In scadenza entro";

    /**
     * Costruttore della GUI VistaBacheca.
     * @param bacheca Bacheca selezionata
     * @param controller Controller principale
     * @param frame Frame chiamante
     * @param utenteLoggato Username dell'utente loggato
     */
    public VistaBacheca(Bacheca bacheca, Controller controller, JFrame frame, String utenteLoggato) {
        this.frameChiamante = frame;
        this.controller = controller;
        this.utenteLoggato = utenteLoggato;
        this.bacheca = bacheca;

        frameVista = new JFrame("Selezione ToDo");
        frameVista.setContentPane(principale);
        todoPanelRis.setLayout(new BoxLayout(todoPanelRis, BoxLayout.X_AXIS));
        frameVista.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameVista.setSize(900, 600);
        frameVista.setLocationRelativeTo(null);
        todoPanelRis.setLayout(new BoxLayout(todoPanelRis, BoxLayout.Y_AXIS));
        frameVista.setVisible(true);

        setupRicercaPanel();
        setupComboBoxOrdina();
        setupActionListeners();

        aggiornaListaToDo();
    }

    /** Imposta il pannello di ricerca e i relativi componenti. */
     private void setupRicercaPanel() {
         String[] criteri = {TITOLO, SCADENZA_OGGI, IN_SCADENZA_ENTRO};
         ricercaComboBox.removeAllItems();
         for (String c : criteri) ricercaComboBox.addItem(c);

         textField1.setColumns(20); // unica barra di ricerca

         // Pulisce e ricostruisce il pannello ricerca
         ricerca.removeAll();
         ricerca.setLayout(new FlowLayout(FlowLayout.LEFT));
         ricerca.add(new JLabel("Criterio:"));
         ricerca.add(ricercaComboBox);
         ricerca.add(textField1);

         buttonCerca.setText("Cerca");
         ricerca.add(buttonCerca);
         ricerca.add(new JLabel("Ordina per:"));
         ricerca.add(comboBoxOrdina);

         ricercaComboBox.addActionListener(_ -> aggiornaTextFieldEnable());
         ricercaComboBox.setSelectedIndex(0);
         textField1.setEnabled(true);
     }

    /** Imposta le voci del combo box per l'ordinamento dei ToDo. */
     private void setupComboBoxOrdina() {
         //combobox filtro ordina
         comboBoxOrdina.removeAllItems();
         comboBoxOrdina.addItem(NESSUN_ORDINAMENTO);
         comboBoxOrdina.addItem(TITOLO_AZ);
         comboBoxOrdina.addItem(DATA_SCADENZA);
         comboBoxOrdina.addItem(POSIZIONE);
         comboBoxOrdina.addItem(STATO_COMPLETAMENTO);
     }

    /** Imposta i listener dei pulsanti e delle combo box. */
     private void setupActionListeners(){
        comboBoxOrdina.addActionListener(_ -> aggiornaListaToDo());
        creaToDoButton.addActionListener(_ -> apriCreaTodo());
        buttonCerca.addActionListener(_ -> cercaToDo());
        tornaAllaHomeButton.addActionListener(_ -> tornaAllaHome());
     }

    /** Abilita o disabilita il campo testo di ricerca in base al criterio scelto. */
     private void aggiornaTextFieldEnable() {
        String criterioRicerca = (String) ricercaComboBox.getSelectedItem();
        if (TITOLO.equals(criterioRicerca) || IN_SCADENZA_ENTRO.equals(criterioRicerca)) {
            textField1.setEnabled(true);
        } else {
            textField1.setText("");
            textField1.setEnabled(false);
        }
    }

    /** Apre la finestra di creazione di un nuovo ToDo. */
    private void apriCreaTodo() {
        CreaToDo quartaGui = new CreaToDo(controller, frameVista, bacheca, utenteLoggato);
        quartaGui.getFrameCreaToDo().setVisible(true);
        frameVista.setVisible(false);
    }

    /** Esegue la ricerca dei To Do in base ai criteri selezionati. */
    private void cercaToDo() {
        String criterioOrdine = (String) comboBoxOrdina.getSelectedItem();
        String criterioRicerca = (String) ricercaComboBox.getSelectedItem();
        String testo = textField1.getText().trim();
        List<ToDo> risultati = new ArrayList<>();

        //RICERCA PER TITOLO
        try {
            if (TITOLO.equals(criterioRicerca)) {
                risultati = controller.getToDoPerBachecaUtente(bacheca, testo);
            } else if (SCADENZA_OGGI.equals(criterioRicerca)) {
                risultati = controller.getToDoInScadenzaOggi(bacheca);
            } else if (IN_SCADENZA_ENTRO.equals(criterioRicerca)) {
                risultati = controller.getToDoInScadenzaEntro(bacheca, testo);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frameVista, ex.getMessage(), ERRORE_TITOLO, JOptionPane.ERROR_MESSAGE);
            return;
        }
        ordinaRisultati(criterioOrdine, risultati);
        mostraListaToDo(risultati);
    }

    /** Applica un ordinamento ai risultati della ricerca in base al criterio selezionato. */
    private void ordinaRisultati(String criterioOrdine, List<ToDo> risultati) {
        if (criterioOrdine == null) return;
        switch (criterioOrdine) {
            case TITOLO_AZ:
                risultati.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                break;
            case DATA_SCADENZA:
                risultati.sort(Comparator.comparing(ToDo::getDatascadenza));
                break;
            case POSIZIONE:
                risultati.sort(Comparator.comparing(t -> convertiInNum(t.getPosizione())));
                break;
            case STATO_COMPLETAMENTO:
                risultati.sort(Comparator.comparing(ToDo::getStato));//fare qualcosa x mettere prima i nn completati
                break;
            default:
                break;
        }
    }

    /**
     * Pulsante per tornare alla home
     */
    private void tornaAllaHome(){
        SelezioneBacheca selez = new SelezioneBacheca(controller, null, utenteLoggato);
        selez.getFrameBacheca().setVisible(true);
        frameVista.dispose();
    }

    /** Aggiorna la lista dei To Do visualizzati, applicando eventuali ordinamenti selezionati. */
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
                    case TITOLO_AZ:
                        lista.sort(Comparator.comparing(ToDo::getTitolo, String.CASE_INSENSITIVE_ORDER));
                        break;
                    case DATA_SCADENZA:
                        lista.sort(Comparator.comparing(ToDo::getDatascadenza));
                        break;
                    case POSIZIONE:
                        lista.sort(Comparator.comparing(t -> convertiInNum(t.getPosizione())));
                        break;
                    case STATO_COMPLETAMENTO:
                        lista.sort(Comparator.comparing(ToDo::getStato));
                        break;
                    case NESSUN_ORDINAMENTO:
                    default:
                        break;
                }
            }

            // Mostra la lista ordinata
            mostraListaToDo(lista);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frameVista,
                    "Errore durante il caricamento dei ToDo: " + e.getMessage(),
                    ERRORE_TITOLO,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Converte la posizione, se numerica, in intero per l'ordinamento.
     * @param s stringa posizione
     * @return valore numerico o 0 se non valido
     */
    private int convertiInNum(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException _) {
            return 0;
        }
    }

    /**
     * Visualizza la lista dei To Do come card grafiche.
     * @param lista lista To Do da mostrare
     */
    private void mostraListaToDo(List<ToDo> lista) { // Usare List per buona pratica
        todoPanelRis.removeAll();
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);

        if (lista.isEmpty()) {
            gridPanel.add(new JLabel("Nessun ToDo presente."));
        } else {
            int colonne = 3; // Numero colonne desiderato
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;

            int riga = 0;
            int colonna = 0;
            for (ToDo t : lista) {
                gbc.gridx = colonna;
                gbc.gridy = riga;
                gridPanel.add(createToDoCard(t), gbc);
                colonna++;
                if (colonna == colonne) {
                    colonna = 0;
                    riga++;
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        todoPanelRis.setLayout(new BorderLayout());
        todoPanelRis.add(scrollPane, BorderLayout.CENTER);
        todoPanelRis.revalidate();
        todoPanelRis.repaint();
    }

    /**
     * Crea la card grafica per un singolo To Do, completa di tutte le azioni disponibili.
     * @param t To Do di riferimento
     * @return JPanel card
     */
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

        addTitleLabel(card, t);
        // Aggiungi dettagli To do
        addToDoDetailsToCard(card, t);

        addChecklistButton(card, t);
        addActionButtons(card, t);

        card.add(Box.createVerticalStrut(5));
        card.add(createCondivisiConButton(t));
        return card;
    }

    /**
     * Aggiunge il titolo del To Do alla card e lo colora di rosso se la scadenza è passata.
     */
    private void addTitleLabel(JPanel card, ToDo t) {
        JLabel labelTitolo = new JLabel(t.getTitolo());
        Calendar oggi = Calendar.getInstance();
        labelTitolo.setForeground(t.getDatascadenza().before(oggi) ? Color.RED : Color.BLACK);
        card.add(labelTitolo);
    }

    /** Aggiunge il pulsante checklist se presente una checklist nel To Do. */
    private void addChecklistButton(JPanel card, ToDo t) {
        if (t.getChecklist() != null && t.getChecklist().getAttivita() != null && !t.getChecklist().getAttivita().isEmpty()) {
            JButton checklistButton = new JButton("Checklist");
            checklistButton.setBackground(new Color(80, 200, 120));
            checklistButton.setForeground(Color.BLACK);
            checklistButton.setFocusPainted(false);
            checklistButton.addActionListener(_ -> gestisciChecklist(t));
            card.add(Box.createVerticalStrut(5));
            card.add(checklistButton);
        }
    }

    /**
     * Gestisce l'apertura della finestra checklist e l'aggiornamento della checklist del ToDo.
     */
    private void gestisciChecklist(ToDo t) {
        FinestraChecklist checklistFrame = new FinestraChecklist(
                t.getChecklist().getAttivita(),
                t,
                frameVista
        );
        checklistFrame.setVisible(true);

        List<Attivita> nuoveAttivita = checklistFrame.getAttivita();
        if (nuoveAttivita != null) {
            t.getChecklist().setAttivita(nuoveAttivita);

            aggiornaStatoToDoInBaseAllaChecklist(t, nuoveAttivita);

            try (Connection conn = ConnessioneDatabase.getConnection()) {
                controller.getCheckListDAO().aggiornaChecklistEAttivita(t);
                controller.getToDoDAO().modifica(t);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frameVista,
                        "Errore durante il salvataggio della checklist: " + ex.getMessage(),
                        "Errore Database",
                        JOptionPane.ERROR_MESSAGE);
            }
            aggiornaListaToDo();
        }
    }

    /**
     * Aggiorna lo stato del To Do in base al completamento delle attività della checklist.
     */
    private void aggiornaStatoToDoInBaseAllaChecklist(ToDo t, List<Attivita> nuoveAttivita) {
        if (t.getChecklist().tutteCompletate() && !nuoveAttivita.isEmpty()) {
            t.setStato(StatoToDo.COMPLETATO);
        } else {
            t.setStato(StatoToDo.NONCOMPLETATO);
        }
    }

    /**
     * Aggiunge i pulsanti di azione (Modifica, Elimina, Sposta) per l'autore del ToDo.
     */
    private void addActionButtons(JPanel card, ToDo t) {
        if (t.getAutore() != null && t.getAutore().getUsername().equals(utenteLoggato)) {
            card.add(createModificaButton(t));
            card.add(Box.createVerticalStrut(5));
            card.add(createEliminaButton(t));
            card.add(Box.createVerticalStrut(5));
            card.add(createSpostaButton(t));
        }
    }

    /**
     * Imposta il colore di sfondo della card in base al colore del To Do.
     */
    private void setCardBackgroundColor(JPanel card, String colore) {
        if (colore != null) {
            Color backgroundColor;
            switch (colore.toLowerCase()) {
                case "rosso" -> backgroundColor = new Color(255, 153, 153);
                case "giallo" -> backgroundColor = new Color(255, 255, 153);
                case "blu" -> backgroundColor = new Color(153, 204, 255);
                case "verde" -> backgroundColor = new Color(153, 255, 153);
                case "arancione" -> backgroundColor = new Color(255, 204, 153);
                case "rosa" -> backgroundColor = new Color(255, 204, 229);
                case "viola" -> backgroundColor = new Color(204, 153, 255);
                case "celeste" -> backgroundColor = new Color(204, 255, 255);
                case "marrone" -> backgroundColor = new Color(210, 180, 140);
                default -> backgroundColor = Color.LIGHT_GRAY;
            }
            card.setBackground(backgroundColor);
            card.setOpaque(true);
        } else {
            card.setBackground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Aggiunge le informazioni dettagliate del To Do alla card.
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

    /** Crea il pulsante di modifica To Do. */
    private JButton createModificaButton(ToDo t) {
        JButton modificaButton = new JButton("Modifica");
        modificaButton.setBackground(new Color(255, 200, 80));
        modificaButton.setForeground(Color.BLACK);
        modificaButton.setFocusPainted(false);
        modificaButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        modificaButton.addActionListener(_ -> {
            ModificaToDo modificaGui = new ModificaToDo(controller, frameVista, bacheca, utenteLoggato, t);
            modificaGui.getFrameModificaToDo().setVisible(true);
            frameVista.setVisible(false);
        });
        return modificaButton;
    }

    /** Crea il pulsante di eliminazione To Do. */
    private JButton createEliminaButton(ToDo t) {
        JButton eliminaButton = new JButton("Elimina");
        eliminaButton.setBackground(new Color(255, 80, 80));
        eliminaButton.setForeground(Color.BLACK);
        eliminaButton.setFocusPainted(false);
        eliminaButton.addActionListener(_ -> {
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
        spostaButton.addActionListener(_ -> gestisciSpostamento(t));
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
        String[] titoliBacheca = getTitoliBacheche(bacheche);
        if (titoliBacheca.length == 0) {
            mostraMessaggioNessunaBacheca();
            return;
        }

        String titolo = richiediTitoloBacheca(titoliBacheca);

        if (titolo != null) {
            gestisciSelezioneBacheca(t, bacheche, titolo);
        }
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

        List<Bacheca> bachecheCorrispondenti = trovaBachecheCorrispondenti(bacheche, titoloDestinazione);

        Bacheca bachecaDestinazione = selezioneBachecaDestinazione(bachecheCorrispondenti);

        if (bachecaDestinazione == null) return;

        Bacheca bachecaOrigine = t.getBacheca();
        controller.spostaToDoInAltraBacheca(t, bachecaOrigine, bachecaDestinazione);
        aggiornaListaToDo();
    }

    /**
     * Trova tutte le bacheche dell'utente con un dato titolo.
     */
    private List<Bacheca> trovaBachecheCorrispondenti(List<Bacheca> bacheche, String titoloDestinazione) {
        List<Bacheca> result = new ArrayList<>();
        for (Bacheca b : bacheche) {
            String titoloBacheca = b.getTitolo() != null ? b.getTitolo().toString() : null;
            if(titoloBacheca != null && titoloBacheca.equals(titoloDestinazione)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Seleziona la bacheca di destinazione se ce n'è più di una con lo stesso titolo.
     */
    private Bacheca selezioneBachecaDestinazione(List<Bacheca> bachecheCorrispondenti) {
        if(bachecheCorrispondenti.isEmpty()) {
            return null;
        }
        if(bachecheCorrispondenti.size() == 1) {
            return bachecheCorrispondenti.getFirst();
        }

        //Più bacheche con stesso titolo: chiedi la descrizione
        String[] descrizioni = new String[bachecheCorrispondenti.size()];
        for(int i = 0; i < bachecheCorrispondenti.size(); i++) {
            descrizioni[i] = bachecheCorrispondenti.get(i).getDescrizione();
        }
        String descScelta = richiediDescrizioneBacheca(descrizioni);
        for(Bacheca b : bachecheCorrispondenti) {
            if(b.getDescrizione().equals(descScelta)) {
                return b;
            }
        }
        return null;
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
     * Pulsante che ci permette di vedere le condivisioni con altri utenti
     */
    private JButton createCondivisiConButton(ToDo t) {
        JButton condivisiButton = new JButton("Condiviso con...");
        condivisiButton.setBackground(new Color(120, 200, 255));
        condivisiButton.setForeground(Color.BLACK);
        condivisiButton.setFocusPainted(false);
        condivisiButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        condivisiButton.addActionListener(_ -> {
            StringBuilder sb = new StringBuilder();
            List<Utente> utentiCondivisi;

            try {
                utentiCondivisi = controller.getUtentiCondivisiByToDoId(t.getTodoId());
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

            String messaggio = !sb.isEmpty() ? sb.toString() : "Non condiviso con nessuno.";

            JOptionPane.showMessageDialog(frameVista,
                    messaggio,
                    "Utenti con cui è condiviso",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        return condivisiButton;
    }
}