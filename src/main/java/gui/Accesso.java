package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Accesso {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    public static JFrame frame;

    private Controller controller;

    public static void main(String[] args) {
        frame = new JFrame("Accesso");
        frame.setContentPane(new Accesso().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Accesso() {
        controller = new Controller();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelezioneBacheca secondGui = new SelezioneBacheca(controller, frame);
                secondGui.frameBacheca.setVisible(true);
                frame.setVisible(false);
            }
        });
    }
}
