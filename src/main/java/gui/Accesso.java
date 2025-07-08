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
    public JFrame frameAccesso;
    private Controller controller;

    //PROVOLA

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.buildAdmin();
        controller.buildBacheche();
        controller.buildToDoPerBachecaUtente();

       new Accesso(controller);
    }


    public Accesso(Controller controller) {
        this.controller = controller;

        frameAccesso = new JFrame("PrimaPagina");
        frameAccesso.setContentPane(mainPanel);
        frameAccesso.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameAccesso.pack();
        frameAccesso.setLocationRelativeTo(null);
        frameAccesso.setVisible(true);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean prova = controller.esisteUtente(username, password);
                if(prova){
                    Utente utente = controller.getUtenteByUsername(username);
                    controller.setUtenteLoggato(utente);

                    JOptionPane.showMessageDialog(mainPanel, "Accesso con successo");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameAccesso,username);
                    secondGui.frameBacheca.setVisible(true);
                    frameAccesso.dispose();
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
                    Utente u = new Utente(username, password);
                    controller.addUtente(u);
                    JOptionPane.showMessageDialog(mainPanel, "Utente registrato");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, frameAccesso,username);
                    secondGui.frameBacheca.setVisible(true);
                    frameAccesso.dispose();
                }
            }
        });
    }
}
