package gui;

import controller.Controller;
import model.*;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.GregorianCalendar;

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

    public JFrame getFrameModificaToDo() {
        return frameModificaToDo;
    }
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }
    private Controller controller;
    private Bacheca bacheca;
    private String utente;
    private ToDo toDo;
    private JList<String> utentiList;
    private JLabel CondivisiPanel;
    private JButton checklistButton;

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
        private void initUI() {

            frameModificaToDo.setContentPane(panel1);
            frameModificaToDo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frameModificaToDo.pack();
            frameModificaToDo.setLocationRelativeTo(null);
            frameModificaToDo.setVisible(true);
        }
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
        private void popolaListaUtenti() {
            //Serve a popolare la list per le condivisioni dei To do
            DefaultListModel<String> utentiModel = new DefaultListModel<>();
            for (Utente u : controller.getListaUtenti()) {
                if (!u.getUsername().equals(utente)) // non aggiungere l'autore
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
        private void impostaStatoToDoRadio() {
            completatoRadioButton.setSelected(toDo.getStato() == StatoToDo.COMPLETATO);
        }
        private void inizializzaChecklistListener() {
            //BOTTONE CHE APRE FINESTRACHECKLIST
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

        private void inizializzaModificaListener() {
            buttonModifica.addActionListener(e -> {
                if (!isValidDate(textFieldData.getText())) {
                    try {
                        if (!isValidDate(textFieldData.getText())) {
                            JOptionPane.showMessageDialog(frameModificaToDo, "Inserisci la data nel formato gg/mm/aaaa");
                            return;
                        }
                        // PRENDE VECCHI POSSESSORI
                        ArrayList<Utente> vecchiPossessori = new ArrayList<>(toDo.getUtentiPossessori());
                        // Recupera i dati dai campi di testo
                        String titolo = textFieldTitolo.getText();
                        String descrizione = textFieldDescrizione.getText();
                        String dataScadenza = textFieldData.getText();
                        String img = textFieldImg.getText();
                        String posizione = textFieldPosizione.getText();
                        String url = textFieldUrl.getText();
                        String colore = textFieldColore.getText();
                        StatoToDo stato = completatoRadioButton.isSelected() ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;

                        // COSTRUISCE NUOVA LISTA POSSESSORI
                        ArrayList<Utente> nuoviPossessori = getNuoviPossessori();

                        // MODIFICA TO do
                        modificaToDo(titolo, descrizione, dataScadenza, img, posizione, url, colore, stato, nuoviPossessori);

                        // AGGIORNA BACHECHE
                        aggiornaBacheche(vecchiPossessori, nuoviPossessori);
                        chiudiEApriVista();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frameModificaToDo, "Errore: " + ex.getMessage());
                    }
                }
            });
        }

    private boolean isValidDate(String dateStr) {
        return dateStr.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private ArrayList<Utente> getNuoviPossessori() {
        ArrayList<Utente> nuoviPossessori = new ArrayList<>();
        nuoviPossessori.add(controller.getUtenteByUsername(utente));
        for(String nome: utentiList.getSelectedValuesList()) {
            nuoviPossessori.add(controller.getUtenteByUsername(nome));
        }
        return nuoviPossessori;
    }

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

    private void aggiornaBacheche(ArrayList<Utente> vecchiPossessori, ArrayList<Utente> nuoviPossessori) {
        for (Utente u : nuoviPossessori) {
            Bacheca bachecaUtente = controller.getOrCreateBacheca(
                    bacheca.getTitolo(),
                    bacheca.getDescrizione(),
                    u.getUsername()
            );
            if (!bachecaUtente.getTodo().contains(toDo)) {
                controller.addToDo(bachecaUtente, toDo, u.getUsername());
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
}
