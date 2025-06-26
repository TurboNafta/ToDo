package gui;

import controller.Controller;
import model.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class  Accesso{
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;

    //Frame e controller
    public static JFrame frameAccesso;
    private Controller controller;

    public static void main(String[] args) {
        Controller controller = new Controller();

        frameAccesso = new JFrame("PrimaPagina");
        frameAccesso.setContentPane(new Accesso().mainPanel);
        frameAccesso.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAccesso.pack();
        frameAccesso.setLocationRelativeTo(null);
        frameAccesso.setVisible(true);
    }

    public Accesso() {
        controller = new Controller();
        controller.buildAdmin();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean prova = controller.esisteUtente(username, password);
                if(prova){
                    Utente utente = controller.getUtente(username);
                    controller.setUtenteLoggato(utente);

                    JOptionPane.showMessageDialog(mainPanel, "Accesso con successo");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameAccesso,username);
                    secondGui.frameBacheca.setVisible(true);
                    frameAccesso.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Credenziali errate");
                }
            }
        });

        registratiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean prova = controller.esisteUtente(username, password);
                if(password.isEmpty() && username.isEmpty()){
                    JOptionPane.showMessageDialog(mainPanel, "Inserisci un nome e una password");
                }else if(prova == true){
                    JOptionPane.showMessageDialog(mainPanel, "Utente già registrato");
                }else{
                    Registrazione registrazioneGui= new Registrazione(controller, frameAccesso);
                    registrazioneGui.frame.setVisible(true);
                    frameAccesso.setVisible(false);
                }
            }
        });
    }
}
