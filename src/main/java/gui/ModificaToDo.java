package gui;

import controller.Controller;
import model.Bacheca;
import model.ToDo;
import model.Utente;
import model.StatoToDo;

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

        if(toDo.getStato() == StatoToDo.COMPLETATO) {
            completatoRadioButton.setSelected(true);
        } else {
            completatoRadioButton.setSelected(false);
        }

        buttonModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dataStr = textFieldData.getText();
                if (!dataStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    JOptionPane.showMessageDialog(frameModificaToDo, "Inserisci la data nel formato gg/MM/aaaa");
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
                    StatoToDo stato=completatoRadioButton.isSelected() ? StatoToDo.COMPLETATO : StatoToDo.NONCOMPLETATO;

                    if(completatoRadioButton.isSelected()) {
                        stato = StatoToDo.COMPLETATO;
                    }else{
                        stato = StatoToDo.NONCOMPLETATO;
                    }



                    ArrayList<Utente> utenti = new ArrayList<>();
                    utenti.add(controller.getUtente(utente));

                    //aggiungo il todo alla bacheca
                    controller.modificaToDo(toDo, titolo, descrizione, dataScadenza, img, posizione, url, colore,stato);

                    //chiudo la finestra e riapro VistaBacheca
                    frameModificaToDo.dispose();
                    VistaBacheca vistaBacheca= new VistaBacheca(bacheca, controller,frameChiamante,utente);
                    vistaBacheca.frameVista.setVisible(true);

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(frameModificaToDo,"Errore: "+ ex.getMessage());
                }
            }
        });
    }
}
