package gui;

import controller.Controller;
import model.TitoloBacheca;
import model.Utente;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final String TITOLO_ERRORE_BACHECA = "Errore nella creazione della bacheca";
    private static final Logger LOGGER = Logger.getLogger(CreaBacheca.class.getName());

    /**
     * Costruttore che crea la schermata di creazione bacheca.
     * @param controller Controller
     * @param callframe Frame chiamante (per tornare indietro)
     * @param utentelog Username dell'utente loggato
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
         * Gestisce la creazione della bacheca in base ai dati inseriti.
         */
        buttonCreazione.addActionListener(_ -> {
            try {
                final TitoloBacheca tipo;
                if (universitaRadioButton.isSelected()) {
                    tipo = TitoloBacheca.UNIVERSITÀ;
                } else if (lavoroRadioButton.isSelected()) {
                    tipo = TitoloBacheca.LAVORO;
                } else if (tempoLiberoRadioButton.isSelected()) {
                    tipo = TitoloBacheca.TEMPOLIBERO;
                } else {
                    JOptionPane.showMessageDialog(frameCreaBacheca,
                            "Scegli il titolo della bacheca",
                            TITOLO_ERRORE_BACHECA,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.addBacheca(tipo, textFieldDescrizione.getText(), utentelog);
                frameChiamante.setVisible(true);
                frameCreaBacheca.setVisible(false);
                frameCreaBacheca.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frameCreaBacheca,
                        "Errore: " + ex.getMessage(),
                        TITOLO_ERRORE_BACHECA,
                        JOptionPane.ERROR_MESSAGE);
                LOGGER.log(Level.SEVERE, "Eccezione catturata", ex);
            }
        });

        /**
         * Annulla la creazione e torna alla selezione bacheca.
         */
        buttonAnnulla.addActionListener(_ -> {
                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameCreaBacheca, utentelog);
                secondGui.getFrameBacheca().setVisible(true);
                getFrameCreaBacheca().setVisible(false);
        });
    }
}
