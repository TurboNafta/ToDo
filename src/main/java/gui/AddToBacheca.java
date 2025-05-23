package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;

public class AddToBacheca {
    private JPanel panel1;
    private JRadioButton buttonUni;
    private JRadioButton buttonTL;
    private JRadioButton buttonLavoro;
    private JPanel radioBoxPanel;
    private JPasswordField textFieldTitolo;
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

    public static JFrame addFrame, frameChia;
    private Controller controller;

    public AddToBacheca(Controller controller, JFrame chiamante) {
        this.controller = controller;
        addFrame = chiamante;

        addFrame = new JFrame("Bacheca");
        addFrame.setContentPane(panel1);
        addFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addFrame.pack();
        addFrame.setVisible(true);

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(buttonUni);
    }
}
