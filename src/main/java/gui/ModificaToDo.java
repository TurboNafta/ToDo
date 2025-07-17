package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
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

    /**
     * Costruttore per creare la gui ModificaToDo
     */
    public ModificaToDo(Controller controller, JFrame frame, Bacheca bacheca, String utente, ToDo t) {
        this.controller = controller;
        this.bacheca = bacheca;
        this.utente = utente;
        this.toDo = t;
        this.frameChiamante = frame;
        this.frameModificaToDo = new JFrame("Pagina Modifica");
        initUI();
        popolaCampiTextField();
        popolaListaUtenti();
        impostaStatoToDoRadio();
        inizializzaChecklistListener();
        inizializzaModificaListener();
    }

    /**
     * Metodo per la visualizzazione del Frame
     */
    private void initUI() {
            frameModificaToDo.setContentPane(panel1);
            frameModificaToDo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frameModificaToDo.pack();
            frameModificaToDo.setLocationRelativeTo(null);
            frameModificaToDo.setVisible(true);
        }

    /**
     * Metodo che ci permette di popolare i dati per la modifica con i dati attualmente presenti
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
     * Metodo che ci popola la lista degli utenti che condividono quel to do
     */
    private void popolaListaUtenti() {
            //Serve a popolare la list per le condivisioni dei To do
            DefaultListModel<String> utentiModel = new DefaultListModel<>();
            for (Utente u : controller.getListaUtenti()) {
                if (!u.getUsername().equals(utente))
                    utentiModel.addElement(u.getUsername());
            }
            utentiList.setModel(utentiModel);
            utentiList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            // PRENDE UTENTI GIA POSSESSORI
            ArrayList<String> utentiPossessori = new ArrayList<>();
            for (Utente u : toDo.getUtentiPossessori()) {
                if (!u.getUsername().equals(utente))
                    utentiPossessori.add(u.getUsername());
            }
            int[] indices = utentiPossessori.stream()
                    .mapToInt(utentiModel::indexOf)
                    .filter(i -> i >= 0)
                    .toArray();
            utentiList.setSelectedIndices(indices);
        }

    /**
     * Metodo che imposta lo stato del to do a completato se abbiamo selezionato il RadioButton
     */
    private void impostaStatoToDoRadio() {
            completatoRadioButton.setSelected(toDo.getStato() == StatoToDo.COMPLETATO);
        }

    /**
     * Metodo che ci permette di creare una checklist all'interno del to do
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
     * Metodo che ci permette di modificare effettivamente i dati del to do
     */
    private void inizializzaModificaListener() {
        buttonModifica.addActionListener(e -> {
            try {
                String posizioneStr = textFieldPosizione.getText().trim();
                String coloreStr = textFieldColore.getText().trim();
                String dataStr = textFieldData.getText().trim();

                if (!isValidDate(dataStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo, "Inserisci la data nel formato gg/mm/aaaa");
                    return;
                }

                if (!controller.isValidPosition(posizioneStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo,
                            "La posizione deve essere un numero intero positivo",
                            "Errore formato posizione",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!controller.isValidColor(coloreStr)) {
                    JOptionPane.showMessageDialog(frameModificaToDo,
                            "Colore non valido. Colori disponibili: rosso, giallo, blu, verde, arancione, rosa, viola, celeste, marrone",
                            "Errore formato colore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (ToDo t : bacheca.getTodo()) {
                    if (!t.equals(toDo) && Integer.parseInt(t.getPosizione()) == Integer.parseInt(posizioneStr)) {
                        JOptionPane.showMessageDialog(frameModificaToDo,
                                "Esiste già un altro ToDo con questa posizione nella bacheca.",
                                "Errore posizione duplicata",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                ArrayList<Utente> vecchiPossessori = new ArrayList<>(toDo.getUtentiPossessori());
                String titolo = textFieldTitolo.getText();
                String descrizione = textFieldDescrizione.getText();
                String dataScadenza = dataStr;
                String img = textFieldImg.getText();
                String posizione = posizioneStr;
                String url = textFieldUrl.getText();
                String colore = coloreStr;
                StatoToDo stato = completatoRadioButton.isSelected() ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;
                ArrayList<Utente> nuoviPossessori = getNuoviPossessori();

                modificaToDo(titolo, descrizione, dataScadenza, img, posizione, url, colore, stato, nuoviPossessori);
                aggiornaBacheche(vecchiPossessori, nuoviPossessori);
                chiudiEApriVista();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameModificaToDo,
                        "Errore: " + ex.getMessage(),
                        "Errore durante la modifica",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Funzione che controlla se la data è inserita nel formato corretto
     */
    private boolean isValidDate(String dateStr) {
        if( !dateStr.matches("\\d{2}/\\d{2}/\\d{4}")){
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Metodo che prende i possessori del to do dopo le modifiche
     */
    private ArrayList<Utente> getNuoviPossessori() {
        ArrayList<Utente> nuoviPossessori = new ArrayList<>();
        nuoviPossessori.add(controller.getUtenteByUsername(utente));
        for(String nome: utentiList.getSelectedValuesList()) {
            nuoviPossessori.add(controller.getUtenteByUsername(nome));
        }
        return nuoviPossessori;
    }

    /**
     * Metodo che passa i dati alla funzione modificaToDo
     */
    private void modificaToDo(String titolo, String descrizione, String dataScadenza, String img,
                              String posizione, String url, String colore, StatoToDo stato,
                              ArrayList<Utente> nuoviPossessori) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataScadenza = dataScadenza;
        this.img = img;
        this.posizione = posizione;
        this.url = url;
        this.colore = colore;
        this.stato = stato;
        toDo.setUtentiPossessori(nuoviPossessori);
        toDo.modificaToDo(toDo, titolo, descrizione, dataScadenza, img, posizione, url, colore, stato);
    }

    /**
     * Metodo che aggiorna le bacheche degli utenti che posseggono quel to do dopo la modifica
     */
    private void aggiornaBacheche(ArrayList<Utente> vecchiPossessori, ArrayList<Utente> nuoviPossessori) {
        for (Utente u : nuoviPossessori) {
            Bacheca bachecaUtente = controller.getOrCreateBacheca(
                    bacheca.getTitolo(),
                    bacheca.getDescrizione(),
                    u.getUsername()
            );
            if (!bachecaUtente.getTodo().contains(toDo)) {
                // Se il ToDo è nuovo (ID = 0), lo aggiungo
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
                Bacheca bachecaUtente = controller.getOrCreateBacheca(
                        bacheca.getTitolo(),
                        bacheca.getDescrizione(),
                        u.getUsername()
                );
                controller.eliminaToDo(bachecaUtente, toDo);
            }
        }
    }

    /**
     * Metodo per tornare all'interno della bacheca
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
    public JFrame getFrameModificaToDo() {
        return frameModificaToDo;
    }
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }
}
