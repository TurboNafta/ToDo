package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Classe GUI per l'accesso e la registrazione dell'utente.
 * Gestisce la visualizzazione della schermata di login e registrazione.
 */
public class  Accesso {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;

    //Frame e controller
    private final JFrame frameAccesso;

    /**
     * Restituisce il frame della schermata di accesso.
     */
    public JFrame getFrameAccesso() {
        return frameAccesso;
    }

    private final Controller controller;
    private static final Logger logger = Logger.getLogger(Accesso.class.getName());

    public static void main(String[] args) {
        setLookAndFeel();
        inizializzaAdmin();
    }

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento del LookAndFeel", e);
        }
    }

    private static void inizializzaAdmin() {
        Controller controller = new Controller();
        controller.buildAdmin();
        controller.buildBacheche();
        controller.buildToDoPerBachecaUtente();
        new Accesso(controller);
    }

    /**
     * Costruttore che crea la schermata di accesso.
     * @param controller Controller dell'applicazione
     */
    public Accesso(Controller controller) {
        this.controller = controller;

        frameAccesso = new JFrame("PrimaPagina");
        frameAccesso.setContentPane(mainPanel);
        frameAccesso.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frameAccesso.pack();
        frameAccesso.setLocationRelativeTo(null);
        frameAccesso.setVisible(true);

        setupLoginButton();
        setupRegistraButton();
    }

    /**
     * Imposta l'azione del pulsante login.
     */
    private void setupLoginButton() {
        loginButton.addActionListener(_ -> gestisciLogin());
    }

    /**
     * Imposta l'azione del pulsante registrazione.
     */
    private void setupRegistraButton() {
        registratiButton.addActionListener(_ -> gestisciRegistrazione());
    }

    /**
     * Gestisce il login dell'utente.
     */
    private void gestisciLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

            try {
                boolean prova = controller.esisteUtente(username, password);
                if (prova) {
                    Utente utente = controller.getUtenteByUsername(username);
                    controller.setUtenteLoggato(utente);

                    JOptionPane.showMessageDialog(mainPanel, "Accesso con successo");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, getFrameAccesso(), username);
                    secondGui.getFrameBacheca().setVisible(true);
                    getFrameAccesso().dispose();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Credenziali errate");
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Errore durante il caricamento del Login", ex);
                JOptionPane.showMessageDialog(mainPanel, "Errore durante il login" + ex.getMessage());
            }
        }

    /**
     * Gestisce la registrazione di un nuovo utente.
     */
    private void gestisciRegistrazione() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (password.isEmpty() && username.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Inserisci un nome e una password");
        } else {
            try {
                boolean prova = controller.esisteUtente(username, password);
                if (prova) {
                    JOptionPane.showMessageDialog(mainPanel, "Utente già registrato");
                } else {
                    Utente u = new Utente(username, password);
                    controller.addUtente(u);
                    JOptionPane.showMessageDialog(mainPanel, "Utente registrato");

                    SelezioneBacheca secondGui = new SelezioneBacheca(controller, getFrameAccesso(), username);
                    secondGui.getFrameBacheca().setVisible(true);
                    getFrameAccesso().dispose();
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Errore durante la registrazione", ex);
                JOptionPane.showMessageDialog(mainPanel, "Errore durante la registrazione");
            }
        }
    }
}
