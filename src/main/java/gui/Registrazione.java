package gui;

import javax.swing.*;

public class Registrazione {
    public JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton registraButton;
    private JLabel regoleLabel;
    private JLabel Username;
    private JLabel Password;

    public Registrazione() {
        frame = new JFrame();
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        regoleLabel.setText("Username in MAIUSCOLO, Password in minuscolo");

        registraButton.addActionListener(e -> {
           String username = usernameField.getText();
           String password = passwordField.getText();

           //Controlli
            if(!username.equals(username.toUpperCase()) || !password.equals(password.toUpperCase())) {
                JOptionPane.showMessageDialog(frame, " Username deve essere TUTTO in MAIUSCOLO\n Password deve essere tutto in minuscolo", "Errore di formato", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(frame, " Registrazione completata!\n Username:" + username, "Successo", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });
    }
}