package gui;

import controller.Controller;
import javax.swing.*;

public class Registrazione {
    public JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton registraButton;
    private JLabel regoleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    private Controller controller;

    public Registrazione() {
        controller = new Controller();

        frame = new JFrame("Registrazione");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        regoleLabel.setText("Username in MAIUSCOLO, Password in minuscolo");

        registraButton.addActionListener(e -> {
           String username = usernameField.getText().toUpperCase();
           String password = passwordField.getText().toLowerCase();

           if(username.isEmpty() || password.isEmpty()){
               JOptionPane.showMessageDialog(panel, "Completa tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
               return;
           }

            JOptionPane.showMessageDialog(panel, " Registrazione completata!\n Username:" + username, "Successo", JOptionPane.INFORMATION_MESSAGE);

           //Apri la nuova finestra
            SelezioneBacheca bachecaGUI = new SelezioneBacheca(controller, frame);
            bachecaGUI.frameBacheca.setVisible(true);
            frame.dispose();
        });
    }
}