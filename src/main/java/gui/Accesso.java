package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Classe GUI per l'accesso dell'utente
 */
public class  Accesso{
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;

    //Frame e controller
    private final JFrame frameAccesso;

    public JFrame getFrameAccesso() {
        return frameAccesso;
    }

    // Necessario come campo per accesso dai listener lambda
    // Usato nelle lambda, ignora falso positivo SonarQube
    private final Controller controller;
    private static final Logger logger = Logger.getLogger(Accesso.class.getName());

    public static void main(String[] args) {try {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) { // O "Windows", "Mac OS X", ecc.
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Errore durante il caricamento del LookAndFeel", e);
    }
        Controller controller = new Controller();
        controller.buildAdmin();
        controller.buildBacheche();
        controller.buildToDoPerBachecaUtente();

       new Accesso(controller);
    }

    /**
     * Costruttore per creare la GUI Accesso
     */
    public Accesso(Controller controller) {
        this.controller = controller;

        frameAccesso = new JFrame("PrimaPagina");
        frameAccesso.setContentPane(mainPanel);
        frameAccesso.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameAccesso.pack();
        frameAccesso.setLocationRelativeTo(null);
        frameAccesso.setVisible(true);

        /**
         * Pulsante per il login
         */
        loginButton.addActionListener(e-> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean prova = controller.esisteUtente(username, password);
                if(prova){
                    Utente utente = controller.getUtenteByUsername(username);
                    controller.setUtenteLoggato(utente);

                    JOptionPane.showMessageDialog(mainPanel, "Accesso con successo");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, getFrameAccesso(), username);
                    secondGui.getFrameBacheca().setVisible(true);
                    getFrameAccesso().dispose();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Credenziali errate");
                }
        });

        /**
         * Pulsante per la registrazione, verifica inoltre se quell'utente è già registrato, così da fargli fare l'accesso
         */
        registratiButton.addActionListener (e-> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean prova = controller.esisteUtente(username, password);
                if(password.isEmpty() && username.isEmpty()){
                    JOptionPane.showMessageDialog(mainPanel, "Inserisci un nome e una password");
                }else if(prova){
                    JOptionPane.showMessageDialog(mainPanel, "Utente già registrato");
                }else{
                    Utente u = new Utente(username, password);
                    controller.addUtente(u);
                    JOptionPane.showMessageDialog(mainPanel, "Utente registrato");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, getFrameAccesso(),username);
                    secondGui.getFrameBacheca().setVisible(true);
                    getFrameAccesso().dispose();
                }
        });
    }
}
