package gui;

import controller.Controller;

import javax.swing.*;

public class Registrazione {
    private JButton registraButton;
    private JLabel Username;
    private JTextField usernameField;
    private JLabel Password;
    private JLabel regoleLabel;
    private JPasswordField passwordField;
    private JPanel panel;

    public static JFrame frame;
    private Controller controller;
    private JFrame chiamante;

    public Registrazione(Controller controller, JFrame chiamante) {
        this.controller = controller;
        this.chiamante = chiamante;

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
