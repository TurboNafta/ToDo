package gui;
import controller.Controller;
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
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;

    public static JFrame frameCreaToDo, frameChiamante;
    private Controller controller;

    public CreaToDo(Controller controller, JFrame frame) {
        this.controller = controller;
        frameChiamante = frame;

        frameCreaToDo = new JFrame("PaginaInserimento");
        frameCreaToDo.setContentPane(panel1);
        frameCreaToDo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameCreaToDo.pack();
        /*addToDoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e){
                VistaBacheca quartaGui = new VistaBacheca(bacheca,controller, frameChiamante);
                quartaGui.frameBacheca.setVisible(true);
                frameCreaToDo.setVisible(false);
            }
        });*/

    }




}
