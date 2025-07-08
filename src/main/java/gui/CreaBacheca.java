package gui;

import controller.Controller;
import model.TitoloBacheca;
import model.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static model.TitoloBacheca.*;

public class CreaBacheca {
    private JPanel panel1;
    private JPanel contenutoPanel;
    private JRadioButton universitàRadioButton;
    private JRadioButton lavoroRadioButton;
    private JRadioButton tempoLiberoRadioButton;
    private JTextField textFieldDescrizione;
    private JButton buttonCreazione;
    private JLabel descrizioneLabel;
    private JButton buttonAnnulla;
    private String utentelog;

    public static JFrame frameCreaBacheca, frameChiamante;
    private Controller controller;

    public CreaBacheca(Controller controller, JFrame callframe, String utentelog) {
        this.controller = controller;
        frameChiamante = callframe;
        Utente utente = controller.getUtenteByUsername(utentelog);
        controller.setUtenteLoggato(utente);

        frameCreaBacheca = new JFrame("Crea bacheca");
        frameCreaBacheca.setContentPane(panel1);
        frameCreaBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameCreaBacheca.pack();
        frameCreaBacheca.setLocationRelativeTo(null);
        frameCreaBacheca.setVisible(true);


        buttonCreazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                if(universitàRadioButton.isSelected()){
                    try{
                        TitoloBacheca tipo = UNIVERSITA;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);
                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    } catch(IllegalStateException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (lavoroRadioButton.isSelected()) {
                    try{
                        TitoloBacheca tipo = LAVORO;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);
                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    }catch(IllegalStateException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (tempoLiberoRadioButton.isSelected()) {
                    try{
                        TitoloBacheca tipo = TEMPOLIBERO;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);

                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    }catch(IllegalStateException ex){
                        JOptionPane.showMessageDialog(frameCreaBacheca, "Bacheca non aggiunta", "Errore nella creazione della bacheca", JOptionPane.ERROR_MESSAGE);
                    }
                }

                else {
                    JOptionPane.showMessageDialog(frameCreaBacheca, "Scegli il titolo della bacheca", "Errore nella creazione del bacheca", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameCreaBacheca, utentelog);
                secondGui.frameBacheca.setVisible(true);
                frameCreaBacheca.setVisible(false);
            }
        });
    }
}
