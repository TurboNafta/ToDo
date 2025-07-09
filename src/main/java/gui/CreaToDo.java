package gui;
import controller.Controller;
import model.Attivita;
import model.Bacheca;
import model.ToDo;
import model.Utente;
import model.CheckList;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;


public class CreaToDo {
    private JPanel panel1;
    private JLabel TitoloLabel;
    private JLabel DescrizioneLabel;
    private JLabel ScadenzaLabel;
    private JPanel ImgLabel;
    private JLabel PosizioneLabel;
    private JLabel URLLabel;
    private JLabel ColoreLabel;
    private JButton addToDoButton;
    private JTextField textFieldTitolo;
    private JTextField textFieldDescrizione;
    private JTextField textFieldData;
    private JTextField textFieldImg;
    private JTextField textFieldPosizione;
    private JTextField textFieldUrl;
    private JTextField textFieldColore;

    public JFrame frameCreaToDo, frameChiamante;
    private Controller controller;
    private Bacheca bacheca;
    private String utente;
    private JList<String> utentiList;
    private JButton checklistButton;
    private CheckList checklistTemp = new CheckList(null, false);  // ci servirà per accumulare dati

    public CreaToDo(Controller controller, JFrame frame, Bacheca bacheca, String utente) {
        this.controller = controller;
        this.bacheca=bacheca;
        this.utente = utente;
        frameChiamante = frame;

        frameCreaToDo = new JFrame("PaginaInserimento");
        frameCreaToDo.setContentPane(panel1);
        frameCreaToDo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameCreaToDo.pack();
        frameCreaToDo.setLocationRelativeTo(null);
        frameCreaToDo.setVisible(true);

      // ci servirà per accumulare dati

        //BOTTONE CHE APRE FINESTRACHECKLIST
        checklistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lancia la finestra checklist passando la lista attività attuale
                FinestraChecklist finestraChecklist = new FinestraChecklist(checklistTemp.getAttivita());
                finestraChecklist.show();
    // All'uscita aggiorna la tua checklist temporanea con le attività modificate
                checklistTemp.setAttivita(finestraChecklist.getAttivita());
            }
        });

        //Serve a popolare la list per le condivisioni dei Todo
        DefaultListModel<String> utentiModel = new DefaultListModel<>();
        for (Utente u : controller.getTuttiUtenti()) {
            if (!u.getUsername().equals(utente)) // non aggiungere l'autore
                utentiModel.addElement(u.getUsername());
        }
        utentiList.setModel(utentiModel);
        utentiList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        addToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                String dataStr = textFieldData.getText();
                if (!dataStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    JOptionPane.showMessageDialog(frameCreaToDo, "Inserisci la data nel formato gg/MM/aaaa");
                    return;
                }
                try{
                    // Recupera i dati dai campi di testo
                    String titolo = textFieldTitolo.getText();
                    String descrizione = textFieldDescrizione.getText();
                    String dataScadenza = textFieldData.getText();
                    String img = textFieldImg.getText();
                    String posizione = textFieldPosizione.getText();
                    String url = textFieldUrl.getText();
                    String colore = textFieldColore.getText();

                    // recupera l'utente loggato
                    ArrayList<Utente> utenti = new ArrayList<>();
                    utenti.add(controller.getUtenteByUsername(utente));

                    for (String u : utentiList.getSelectedValuesList()) {
                        utenti.add(controller.getUtenteByUsername(u));
                    }

                    //Crea ToDo
                    ToDo nuovoToDo=new ToDo(titolo, descrizione,url,dataScadenza,img,posizione,colore,utenti, controller.getUtenteByUsername(utente));
                    nuovoToDo.setChecklist(checklistTemp);


                    for (Utente u : utenti) {
                        Bacheca bachecaUtente = controller.getOrCreateBacheca(
                                bacheca.getTitolo(),
                                bacheca.getDescrizione(),
                                u.getUsername()
                        );
                        controller.addToDo(bachecaUtente, nuovoToDo, u.getUsername());
                    }

                    frameCreaToDo.dispose();
                    VistaBacheca vistaBacheca = new VistaBacheca(bacheca, controller, frameChiamante, utente);
                    vistaBacheca.frameVista.setVisible(true);

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(frameCreaToDo,"Errore: "+ ex.getMessage());
                }
            }
        });
    }
}
