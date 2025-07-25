package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.GregorianCalendar;

/**
 * Classe per la GUI di ModificaToDo, ci permette di andare a vedere i dati del to do e di modificarli
 */
public class ModificaToDo {
    private JPanel panel1;
    private JPanel imgLabel;
    private JLabel titoloLabel;
    private JLabel descrizioneLabel;
    private JLabel scadenzaLabel;
    private JLabel posizioneLabel;
    private JLabel urlLabel;
    private JLabel coloreLabel;
    private JTextField textFieldTitolo;
    private JTextField textFieldDescrizione;
    private JTextField textFieldData;
    private JTextField textFieldImg;
    private JTextField textFieldPosizione;
    private JTextField textFieldUrl;
    private JTextField textFieldColore;
    private JButton buttonModifica;
    private JRadioButton completatoRadioButton;
    private JLabel statoLabel;


    private final JFrame frameModificaToDo;
    private final JFrame frameChiamante;
    private String titolo;
    private String descrizione;
    private String dataScadenza;
    private String img;
    private String posizione;
    private String url;
    private String colore;
    private StatoToDo stato;
    private Controller controller;
    private Bacheca bacheca;
    private String utente;
    private ToDo toDo;
    private JList<String> utentiList;
    private JLabel CondivisiPanel;
    private JButton checklistButton;
    private JButton buttonAnnulla;

    /**
     * Costruttore per creare la GUI di modifica ToDo.
     * @param controller Controller dell'applicazione
     * @param frame Finestra chiamante
     * @param bacheca Bacheca di riferimento
     * @param utente Utente loggato
     * @param t To Do da modificare
     */
    public ModificaToDo(Controller controller, JFrame frame, Bacheca bacheca, String utente, ToDo t) {
        this.controller = controller;
        this.bacheca = bacheca;
        this.utente = utente;
        this.toDo = t;
        this.frameChiamante = frame;
        this.frameModificaToDo = new JFrame("Pagina Modifica");
        inizializzaGUI();
        popolaCampiTextField();
        popolaListaUtenti();
        impostaStatoToDoRadio();
        inizializzaChecklistListener();
        inizializzaModificaListener();

        buttonAnnulla.addActionListener(e -> {
            frameModificaToDo.dispose();
            VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
            vistaBacheca.frameVista.setVisible(true);
        });
    }

    /**
     * Inizializza e visualizza la finestra di modifica To Do.
     */
    private void inizializzaGUI() {
            frameModificaToDo.setContentPane(panel1);
            frameModificaToDo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frameModificaToDo.pack();
            frameModificaToDo.setLocationRelativeTo(null);
            frameModificaToDo.setVisible(true);
        }

    /**
     * Popola i campi della GUI con i dati attuali del To Do.
     */
    private void popolaCampiTextField() {
            textFieldTitolo.setText(toDo.getTitolo());
            textFieldDescrizione.setText(toDo.getDescrizione());

            GregorianCalendar data = toDo.getDatascadenza();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataString = sdf.format(data.getTime());
            textFieldData.setText(dataString);

            textFieldImg.setText(toDo.getImage());
            textFieldPosizione.setText(toDo.getPosizione());
            textFieldUrl.setText(toDo.getUrl());
            textFieldColore.setText(toDo.getColoresfondo());
        }

    /**
     * Popola la lista degli utenti con cui è possibile condividere il To Do.
     * Seleziona automaticamente quelli già associati.
     */
    private void popolaListaUtenti() {
        //Serve a popolare la list per le condivisioni dei To do
        DefaultListModel<String> utentiModel = new DefaultListModel<>();
        for (Utente u : controller.getTuttiUtentiDalDB()) {
            if (!u.getUsername().equals(utente))
                utentiModel.addElement(u.getUsername());
        }
        utentiList.setModel(utentiModel);
        List<Utente> utentiAttualiCondivisi = toDo.getUtentiPossessori();
        List<Integer> indicesToSelect = new ArrayList<>();

        for (int i = 0; i < utentiModel.getSize(); i++) {
            String usernameInList = utentiModel.getElementAt(i);
            for (Utente utenteCondiviso : utentiAttualiCondivisi) {
                if (usernameInList.equals(utenteCondiviso.getUsername())) {
                    indicesToSelect.add(i);
                    break;
                }
            }
        }

        int[] selectedIndicesArray = new int[indicesToSelect.size()];
        for (int i = 0; i < indicesToSelect.size(); i++) {
            selectedIndicesArray[i] = indicesToSelect.get(i);
        }
        utentiList.setSelectedIndices(selectedIndicesArray);
    }

    /**
     * Imposta il radio button sullo stato completato se il To Do è già completato.
     */
    private void impostaStatoToDoRadio() {
            completatoRadioButton.setSelected(toDo.getStato() == StatoToDo.COMPLETATO);
        }

    /**
     * Inizializza il listener per la gestione della checklist.
     * Permette di aggiungere/modificare attività e aggiorna lo stato del To Do al termine.
     */
    private void inizializzaChecklistListener() {
        /**
         * Pulsante che ci permette di passare alla pagina per aggiungere attività alla checklsit
         */
        checklistButton.addActionListener(e -> {
                FinestraChecklist checklist = new FinestraChecklist(toDo.getChecklist().getAttivita(), toDo, frameModificaToDo);
                checklist.setVisible(true);
                //quando FinestraChecklist viene chiusa, aggiorno To do
                toDo.getChecklist().setAttivita(checklist.getAttivita());

                //controllo se tutte le attività sono completate e aggiorno lo stato dei To do
                if (toDo.getChecklist().tutteCompletate() && !toDo.getChecklist().getAttivita().isEmpty()) {
                    completatoRadioButton.setSelected(true);
                    toDo.setStato(StatoToDo.COMPLETATO);
                } else {
                    completatoRadioButton.setSelected(false);
                    toDo.setStato(StatoToDo.NONCOMPLETATO);
                }

            });
        }

    /**
     * Inizializza il listener per la modifica effettiva del To Do.
     * Esegue validazione, aggiorna il database, aggiorna le condivisioni e chiude la finestra.
     */
    private void inizializzaModificaListener() {
        buttonModifica.addActionListener(e -> {
            try {
                String posizioneStr = textFieldPosizione.getText().trim();
                String coloreStr = textFieldColore.getText().trim();
                String dataStr = textFieldData.getText().trim();

                if (!isValidData(dataStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo, "Inserisci la data nel formato gg/mm/aaaa");
                    return;
                }

                if (!controller.isValidPosizione(posizioneStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo,
                            "La posizione deve essere un numero intero positivo",
                            "Errore formato posizione",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!controller.isValidColore(coloreStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo,
                            "Colore non valido. Colori disponibili: rosso, giallo, blu, verde, arancione, rosa, viola, celeste, marrone",
                            "Errore formato colore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int nuovaPosizioneInt = Integer.parseInt(posizioneStr);

                if(esisteToDoConStessaPosizione(nuovaPosizioneInt)){
                    mostraErrore("Esiste già un altro ToDo con questa posizione nella bacheca.", "Errore posizione duplicata");
                    return;
                }

                ArrayList<Utente> vecchiPossessori = new ArrayList<>(toDo.getUtentiPossessori());

                aggiornaToDoData();

                controller.aggiornaToDoCompleto(this.toDo);
                bacheca.setTodo(controller.getToDoByBachecaAndUtente(bacheca.getId(), utente));
                aggiornaBacheche(vecchiPossessori, getNuoviPossessori());

                MostraInfo("ToDo aggiornato con successo!", "Modifica Completa");

                chiudiEApriVista();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frameModificaToDo,
                        "Errore del database durante l'aggiornamento del ToDo: " + ex.getMessage(),
                        "Errore Database",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameModificaToDo,
                        "Si è verificato un errore inatteso: " + ex.getMessage(),
                        "Errore Generico",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    /**
     * Controlla se esiste già nella bacheca un altro To Do con la stessa posizione.
     * @param nuovaPosizioneInt nuova posizione da verificare
     * @return true se esiste già, false altrimenti
     */
    private boolean esisteToDoConStessaPosizione(int nuovaPosizioneInt) {
        for (ToDo t : bacheca.getTodo()) {
            if (!t.equals(toDo) && t.getPosizione() != null && controller.isValidPosizione(t.getPosizione())) {
                int existingPosizioneInt = Integer.parseInt(t.getPosizione());
                if (existingPosizioneInt == nuovaPosizioneInt) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Mostra un errore nella finestra di modifica To Do.
     * @param message
     * @param title
     */
    private void mostraErrore(String message, String title) {
        JOptionPane.showMessageDialog(frameModificaToDo,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra un messaggio informativo nella finestra di modifica To Do.
     * @param message
     * @param title
     */
    private void MostraInfo(String message, String title) {
        JOptionPane.showMessageDialog(frameModificaToDo,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Aggiorna i dati del To Do con i valori inseriti nei campi della GUI.
     */
    private void aggiornaToDoData() {
        String titolo = textFieldTitolo.getText();
        String descrizione = textFieldDescrizione.getText();
        String img = textFieldImg.getText();
        String url = textFieldUrl.getText();
        String dataStr = textFieldData.getText().trim();
        String posizioneStr = textFieldPosizione.getText().trim();
        String coloreStr = textFieldColore.getText().trim();

        String[] dataSplit = dataStr.split("/");
        int anno = Integer.parseInt(dataSplit[2]);
        int mese = Integer.parseInt(dataSplit[1]) - 1;
        int gg = Integer.parseInt(dataSplit[0]);
        GregorianCalendar data = new GregorianCalendar(anno, mese, gg);

        StatoToDo stato = completatoRadioButton.isSelected() ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;
        ArrayList<Utente> nuoviPossessori = getNuoviPossessori();

        this.toDo.setTitolo(titolo);
        this.toDo.setDescrizione(descrizione);
        this.toDo.setDatascadenza(data);
        this.toDo.setImage(img);
        this.toDo.setPosizione(posizioneStr);
        this.toDo.setUrl(url);
        this.toDo.setColoresfondo(coloreStr);
        this.toDo.setStato(stato);
        this.toDo.setUtentiPossessori(nuoviPossessori);
    }

    /**
     * Controlla che la data sia nel formato corretto gg/MM/yyyy.
     * @param dateStr la stringa data
     * @return true se valida, false altrimenti
     */
    private boolean isValidData(String dateStr) {
        if( !dateStr.matches("\\d{2}/\\d{2}/\\d{4}")){
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception _) {
            return false;
        }

    }

    /**
     * Restituisce la lista aggiornata degli utenti possessori (compresi i selezionati nella lista).
     * @return lista utenti possessori
     */
    private ArrayList<Utente> getNuoviPossessori() {
        Set<String> selectedUsernames = new HashSet<>();

        if (utentiList != null) {
            for (String username : utentiList.getSelectedValuesList()) {
                selectedUsernames.add(username);
            }
        }

        selectedUsernames.add(this.utente);

        ArrayList<Utente> nuoviPossessoriList = new ArrayList<>();
        for (String username : selectedUsernames) {
            Utente u = controller.getUtenteByUsername(username);
            if (u != null) {
                nuoviPossessoriList.add(u);
            }
        }
        return nuoviPossessoriList;
    }


    /**
     * Aggiorna le bacheche degli utenti possessori dopo una modifica del To Do.
     * @param vecchiPossessori lista utenti precedenti
     * @param nuoviPossessori lista utenti attuali
     */
    private void aggiornaBacheche(ArrayList<Utente> vecchiPossessori, ArrayList<Utente> nuoviPossessori) {
        for (Utente u : nuoviPossessori) {
            Bacheca bachecaUtente = controller.getOrCreateBacheca(
                    bacheca.getTitolo(),
                    bacheca.getDescrizione(),
                    u.getUsername()
            );
            if (!bachecaUtente.getTodo().contains(toDo)) {
                if (toDo.getTodoId() == 0) {
                    controller.addToDo(bachecaUtente, toDo, u.getUsername());
                } else {
                    // Se esiste già, lo aggiungo solo in memoria senza creare un duplicato nel DB
                    bachecaUtente.getTodo().add(toDo);
                }
            }
        }

        for (Utente u : vecchiPossessori) {
            if (!nuoviPossessori.contains(u)) {
                if (!u.getUsername().equals(utente)) {
                    Bacheca bachecaUtente = controller.getOrCreateBacheca(
                            bacheca.getTitolo(),
                            bacheca.getDescrizione(),
                            u.getUsername()
                    );
                    controller.eliminaToDo(bachecaUtente, toDo);
                }
            }
        }
    }

    /**
     * Chiude la finestra di modifica e apre la vista della bacheca aggiornata.
     */
    private void chiudiEApriVista() {
        frameModificaToDo.dispose();
        List<Bacheca> bacheche = controller.getBachecaList(bacheca.getTitolo().toString(), utente);
        if (!bacheche.isEmpty()) {
            VistaBacheca vistaBacheca = new VistaBacheca(bacheche.get(0), controller, frameChiamante, utente);
            vistaBacheca.frameVista.setVisible(true);
        } else {
            new VistaBacheca(bacheca, controller, frameChiamante, utente);
        }
    }

    /**
     * Restituisce la finestra di modifica To Do.
     * @return finestra di modifica To Do
     */
    public JFrame getFrameModificaToDo() {
        return frameModificaToDo;
    }

    /**
     * Restituisce la finestra chiamante.
     * @return finestra chiamante
     */
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }
}
