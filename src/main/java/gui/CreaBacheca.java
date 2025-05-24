package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreaBacheca {
    private JPanel panel1;
    private JPanel contenutoPanel;
    private JRadioButton universitàRadioButton;
    private JRadioButton lavoroRadioButton;
    private JRadioButton tempoLiberoRadioButton;
    private JTextField textFieldDescrizione;
    private JButton buttonCreazione;
    private JLabel descrizioneLabel;

    public static JFrame frameCreaBacheca, frameChiamante;
    private Controller controller;

    public CreaBacheca(Controller controller, JFrame callframe) {
        this.controller = controller;
        frameChiamante = callframe;

        frameCreaBacheca = new JFrame("Crea bacheca");
        frameCreaBacheca.setContentPane(panel1);
        frameCreaBacheca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameCreaBacheca.pack();
        frameCreaBacheca.setVisible(true);


        buttonCreazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameChiamante);
                secondGui.frameBacheca.setVisible(true);
                frameCreaBacheca.setVisible(false);
            }
        });
    }
}
