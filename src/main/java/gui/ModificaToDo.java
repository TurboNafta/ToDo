package gui;

import controller.Controller;
import model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ModificaToDo {
    private JPanel panel1;
    private JPanel ImgLabel;
    private JLabel TitoloLabel;
    private JLabel DescrizioneLabel;
    private JLabel ScadenzaLabel;
    private JLabel PosizioneLabel;
    private JLabel URLLabel;
    private JLabel ColoreLabel;
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


    public JFrame frameModificaToDo, frameChiamante;
    private Controller controller;
    private Bacheca bacheca;
    private String utente;
    private ToDo toDo;
    private JList<String> utentiList;
    private JLabel CondivisiPanel;
    private JButton checklistButton;
    private ArrayList<Attivita> checklistItems = new ArrayList<>();

    public ModificaToDo(Controller controller, JFrame frame, Bacheca bacheca, String utente, ToDo t) {
        this.controller = controller;
        this.bacheca=bacheca;
        this.utente = utente;
        toDo = t;
        frameChiamante = frame;

        frameModificaToDo = new JFrame("Pagina Modifica");
        frameModificaToDo.setContentPane(panel1);
        frameModificaToDo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameModificaToDo.pack();
        frameModificaToDo.setLocationRelativeTo(null);
        frameModificaToDo.setVisible(true);

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

        //Serve a popolare la list per le condivisioni dei Todo
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
                .mapToInt(username -> utentiModel.indexOf(username))
                .filter(i -> i >= 0)
                .toArray();
        utentiList.setSelectedIndices(indices);

        if(toDo.getStato() == StatoToDo.COMPLETATO) {
            completatoRadioButton.setSelected(true);
        } else {
            completatoRadioButton.setSelected(false);
        }

        checklistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FinestraChecklist checklist = new FinestraChecklist(checklistItems);
                checklist.show();
                checklistItems = checklist.getAttivita();
            }
        });

        buttonModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dataStr = textFieldData.getText();
                if (!dataStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    JOptionPane.showMessageDialog(frameModificaToDo, "Inserisci la data nel formato gg/MM/aaaa");
                    return;
                }
                try{
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
                    StatoToDo stato=completatoRadioButton.isSelected() ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;

                    if(completatoRadioButton.isSelected()) {
                        stato = StatoToDo.COMPLETATO;
                    }else{
                        stato = StatoToDo.NONCOMPLETATO;
                    }

                    // COSTRUISCE NUOVA LISTA POSSESSORI
                    ArrayList<Utente> nuoviPossessori = new ArrayList<>();
                    nuoviPossessori.add(controller.getUtenteByUsername(utente));
                    for(String nome : utentiList.getSelectedValuesList()){
                        nuoviPossessori.add(controller.getUtenteByUsername(nome));
                    }
                    toDo.setUtentiPossessori(nuoviPossessori);

                    // MODIFICA TODO
                    t.modificaToDo(toDo, titolo, descrizione, dataScadenza, img, posizione, url, colore, stato);

                    // AGGIORNA BACHECHE DEI POSSESSORI
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

                    // TOGLIE TODO DA UTENTI NON PIU' POSSESSORI
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

                    frameModificaToDo.dispose();
                    ArrayList<Bacheca> bacheche = controller.getBachecaList(bacheca.getTitolo().toString(), utente);
                    if (!bacheche.isEmpty()) {
                        VistaBacheca vistaBacheca = new VistaBacheca(bacheche.get(0), controller, frameChiamante, utente);
                        vistaBacheca.frameVista.setVisible(true);
                    }else{
                        VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
                    }

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(frameModificaToDo,"Errore: "+ ex.getMessage());
                }
            }
        });
    }
}
