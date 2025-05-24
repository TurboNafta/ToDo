package gui;

import controller.Controller;
import model.TitoloBacheca;
import model.ToDo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

import static model.TitoloBacheca.UNIVERSITA;

public class AddToBacheca {
    private JPanel panel1;
    private JRadioButton buttonUni;
    private JRadioButton buttonTL;
    private JRadioButton buttonLavoro;
    private JPanel radioBoxPanel;
    private JTextField textFieldTitolo;
    private JTextField textFieldDescrizione;
    private JTextField textFieldData;
    private JTextField textFieldUrl;
    private JTextField textFieldColore;
    private JLabel TitoloLabel;
    private JLabel DescrizioneLabel;
    private JLabel DataLabel;
    private JLabel urlLabel;
    private JLabel ColoreLabel;
    private JButton buttonInserisci;
    private JTextField textFieldImg;
    private JTextField textFieldPosizione;

    public static JFrame addFrame, frameChia;
    private Controller controller;

    private TitoloBacheca bachecaScelta;

    public AddToBacheca(Controller controller, JFrame chiamante) {
        this.controller = controller;
        addFrame = chiamante;

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(buttonUni);
        radioButtonGroup.add(buttonTL);
        radioButtonGroup.add(buttonLavoro);

        addFrame = new JFrame("PaginaInserimento");
        addFrame.setContentPane(panel1);
        addFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addFrame.pack();
        addFrame.setVisible(true);

        buttonInserisci.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(buttonUni.isSelected()) {
                    try{
                        /*
                        String data = textFieldData.getText();
                        String[] dataSplit = data.split("/");
                        int anno = Integer.parseInt(dataSplit[2]);
                        //Gregorian salva partendo da 0, quindi devo fare così per salvare, quando stampo +1
                        int mese = Integer.parseInt(dataSplit[1])-1;
                        int gg = Integer.parseInt(dataSplit[0]);
                        GregorianCalendar dataScadenza = new GregorianCalendar(anno, mese, gg);*/

                        TitoloBacheca tipo = UNIVERSITA;

                        controller.addABacheca(new ToDo(textFieldTitolo.getText(), textFieldDescrizione.getText(), textFieldUrl.getText(), textFieldData.getText(), textFieldImg.getText(), textFieldPosizione.getText(), textFieldColore.getText()), tipo);
                        JOptionPane.showMessageDialog(addFrame, "Aggiunto in Università");

                    }catch(IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(addFrame, "Corso non aggiunto :-(, durata negativa", "Errore nella creazione del corso", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
