package gui;
import controller.Controller;
import model.Bacheca;
import model.ToDo;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreaToDo {
    private JPanel panel1;
    private JLabel TitoloLabel;
    private JLabel DescrizioneLabel;
    private JLabel ScadenzaLabel;
    private JPanel ImgLabel;
    private JLabel PosizioneLabel;
    private JLabel URLLabel;
    private JLabel ColoreLabel;
    private JButton addToDoButton;
    private JTextField textFieldTitolo;
    private JTextField textFieldDescrizione;
    private JTextField textFieldData;
    private JTextField textFieldImg;
    private JTextField textFieldPosizione;
    private JTextField textFieldUrl;
    private JTextField textFieldColore;

    public static JFrame frameCreaToDo, frameChiamante;
    private Controller controller;
    private Bacheca bacheca;

    public CreaToDo(Controller controller, JFrame frame, Bacheca bacheca) {
        this.controller = controller;
        this.bacheca=bacheca;
        frameChiamante = frame;

        frameCreaToDo = new JFrame("PaginaInserimento");
        frameCreaToDo.setContentPane(panel1);
        frameCreaToDo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameCreaToDo.pack();
        addToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                //controller.addToDo(new ToDo(textFieldTitolo.getText(), textFieldDescrizione.getText(), textFieldData.getText(), textFieldImg.getText(), textFieldPosizione.getText(), textFieldUrl.getText(), textFieldColore.getText()), bacheca);
            }
        });
    }
}
