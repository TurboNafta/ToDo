package gui;

import controller.Controller;
import model.TitoloBacheca;
import model.Utente;
import javax.swing.*;
import static model.TitoloBacheca.*;

/**
 * Classe GUI per la creazione di una nuova bacheca
 */
public class CreaBacheca {
    private JPanel panel1;
    private JPanel contenutoPanel;
    private JRadioButton universitaRadioButton;
    private JRadioButton lavoroRadioButton;
    private JRadioButton tempoLiberoRadioButton;
    private JTextField textFieldDescrizione;
    private JLabel descrizioneLabel;
    private JButton buttonCreazione;
    private JButton buttonAnnulla;

    private final JFrame frameCreaBacheca;
    private final JFrame frameChiamante;
    private final Controller controller;

    public JFrame getFrameCreaBacheca() {
        return frameCreaBacheca;
    }
    public JFrame getFrameChiamante() {
        return frameChiamante;
    }

    private static final String ERRORE_BACHECA_NON_AGGIUNTA = "Bacheca non aggiunta";
    private static final String TITOLO_ERRORE_BACHECA = "Errore nella creazione della bacheca";

    /**
     * Costruttore che ci permette di creare la gui CreaBacheca
     */
    public CreaBacheca(Controller controller, JFrame callframe, String utentelog) {
        this.controller = controller;
        this.frameChiamante = callframe;

        Utente utente = controller.getUtenteByUsername(utentelog);
        controller.setUtenteLoggato(utente);

        frameCreaBacheca = new JFrame("Crea bacheca");
        frameCreaBacheca.setContentPane(panel1);
        frameCreaBacheca.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameCreaBacheca.pack();
        frameCreaBacheca.setLocationRelativeTo(null);
        frameCreaBacheca.setVisible(true);

        ButtonGroup tipoBacheca = new ButtonGroup();
        tipoBacheca.add(universitaRadioButton);
        tipoBacheca.add(lavoroRadioButton);
        tipoBacheca.add(tempoLiberoRadioButton);

        /**
         * Pulsante che ci permette di creare la bacheca per l'utente loggato
         */
        buttonCreazione.addActionListener(e-> {
                if(universitaRadioButton.isSelected()){
                    try{
                        TitoloBacheca tipo = UNIVERSITÀ;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);
                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException | IllegalStateException _){
                        JOptionPane.showMessageDialog(frameCreaBacheca, ERRORE_BACHECA_NON_AGGIUNTA, TITOLO_ERRORE_BACHECA, JOptionPane.ERROR_MESSAGE);
                    }
                } else if (lavoroRadioButton.isSelected()) {
                    try{
                        TitoloBacheca tipo = LAVORO;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);
                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException | IllegalStateException _){
                        JOptionPane.showMessageDialog(frameCreaBacheca, ERRORE_BACHECA_NON_AGGIUNTA, TITOLO_ERRORE_BACHECA, JOptionPane.ERROR_MESSAGE);
                    }
                } else if (tempoLiberoRadioButton.isSelected()) {
                    try{
                        TitoloBacheca tipo = TEMPOLIBERO;
                        controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                        frameChiamante.setVisible(true);

                        frameCreaBacheca.setVisible(false);
                        frameCreaBacheca.dispose();
                    }catch(IllegalArgumentException | IllegalStateException _){
                        JOptionPane.showMessageDialog(frameCreaBacheca, ERRORE_BACHECA_NON_AGGIUNTA, TITOLO_ERRORE_BACHECA, JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(frameCreaBacheca, "Scegli il titolo della bacheca", "Errore nella creazione del bacheca", JOptionPane.ERROR_MESSAGE);
                }
        });

        /**
         * Pulsante per annullare l'operazione di creazione
         */
        buttonAnnulla.addActionListener(e-> {
                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameCreaBacheca, utentelog);
                secondGui.getFrameBacheca().setVisible(true);
                getFrameCreaBacheca().setVisible(false);
        });
    }
}
