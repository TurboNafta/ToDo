package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class  Accesso{
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;
    public static JFrame frame;

    private Controller controller;

    public static void main(String[] args) {
        Controller controller = new Controller();

        frame = new JFrame("Accesso");
        frame.setContentPane(new Accesso().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400, 300);
    }

    public Accesso() {
        controller = new Controller();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if(!username.equals(username.toUpperCase()) || !password.equals(password.toLowerCase())){
                JOptionPane.showMessageDialog(mainPanel, "Formato errato: username in MAIUSCOLO e password in minuscolo.", "Errore formato", JOptionPane.WARNING_MESSAGE);
                return; //Blocca tutto
            }

            //controllo: username MAIUSCOLO, password minuscola
            if(controller.verificaAccesso(username, password)){
                JOptionPane.showMessageDialog(mainPanel, "Accesso con successo");

                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frame,username);
                secondGui.frameBacheca.setVisible(true);
                frame.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Credenziali errate");
            }
        });

        registratiButton.addActionListener(e -> {
           Registrazione registrazioneGui= new Registrazione(controller, frame);
           registrazioneGui.frame.setVisible(true);
           frame.setVisible(false);
        });
    }
}
