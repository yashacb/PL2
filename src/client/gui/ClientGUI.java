package client.gui;

import client.workers.ClaimSeatWorker;
import client.workers.GetFreeWorker;
import client.workers.LoginWorker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ClientGUI extends JFrame {

    public ClientGUI(String str) {
        super(str);
    }

    /**
     * This created the pre-login screen, which takes the rollNo and name as input.
     *
     * @param pane the content pane
     */
    public void createGUI(Container pane) {
        JPanel jPanel;
        JLabel rollNoLabel, nameLabel, hostLabel, portLabel;
        JButton login;
        JTextField rollNo, name, host, port;
        Font f = new Font("Georgia", Font.ITALIC | Font.BOLD, 17);
        UIManager.put("Button.font", f);
        UIManager.put("Label.font", f);
        UIManager.put("TextField.font", f);

        jPanel = new JPanel();
        rollNoLabel = new JLabel("Roll No");
        nameLabel = new JLabel("Name");
        hostLabel = new JLabel("Host");
        portLabel = new JLabel("Port");
        rollNo = new JTextField("1000");
        name = new JTextField("Yash");
        host = new JTextField("localhost");
        port = new JTextField("9090");
        login = new JButton("Login");

        rollNoLabel.setBounds(110, 100, 70, 35);
        nameLabel.setBounds(110, 150, 70, 35);
        hostLabel.setBounds(110, 200, 70, 35);
        portLabel.setBounds(110, 250, 70, 35);

        rollNo.setBounds(200, 100, 160, 35);
        rollNo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        name.setBounds(200, 150, 160, 35);
        name.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        host.setBounds(200, 200, 160, 35);
        host.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        port.setBounds(200, 250, 160, 35);
        port.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        login.setBounds(200, 320, 100, 35);

        jPanel.setLayout(null);
        jPanel.add(rollNoLabel);
        jPanel.add(rollNo);
        jPanel.add(nameLabel);
        jPanel.add(name);
        jPanel.add(hostLabel);
        jPanel.add(host);
        jPanel.add(portLabel);
        jPanel.add(port);
        jPanel.add(login);

        login.addActionListener((ae) -> {
            try {
                (new LoginWorker(host.getText(), Integer.parseInt(port.getText()),
                        rollNo.getText(), name.getText(), ClientGUI.this)).execute();
            } catch (Exception e) {
                ErrorGUI err = new ErrorGUI("Incorrect host and port");
                err.createGUI();
            }
        });

        pane.add(jPanel);
        setSize(500, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        try {
            setIconImage(ImageIO.read(new File("C:/Users/yb/IdeaProjects/PL2/src/icons/client.png")));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * This is the post-login gui. It takes the seat number to claim and can also display the free
     * seats.
     *
     * @param pane   - The content pane.
     * @param rollNo - The rollNo of the student.
     * @param name   - The name of the student.
     * @param host   - The ip address of the server.
     * @param port   - The port of the server.
     */
    public void postLoginGUI(Container pane, String rollNo, String name, String host, int port) {
        pane.removeAll();
        JPanel jPanel;
        JLabel rollNoLabel, nameLabel, seatLabel;
        JButton submit, free;
        JTextField seat;

        jPanel = new JPanel();
        rollNoLabel = new JLabel("Roll No:      " + rollNo);
        nameLabel = new JLabel("Name:           " + name);
        seatLabel = new JLabel("Seat");
        seat = new JTextField("0");
        submit = new JButton("Submit");
        free = new JButton("Free");

        rollNoLabel.setBounds(110, 100, 250, 35);
        nameLabel.setBounds(110, 150, 250, 35);
        seatLabel.setBounds(110, 200, 70, 35);

        seat.setBounds(200, 200, 160, 35);
        seat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        free.setBounds(110, 320, 100, 35);
        submit.setBounds(260, 320, 100, 35);

        jPanel.setLayout(null);
        jPanel.add(rollNoLabel);
        jPanel.add(nameLabel);
        jPanel.add(seatLabel);
        jPanel.add(seat);
        jPanel.add(free);
        jPanel.add(submit);


        submit.addActionListener((ae) -> {
            try {
                int seatNo = Integer.parseInt(seat.getText());

                (new ClaimSeatWorker(host, port, rollNo,
                        name, seatNo)).execute();
            } catch (Exception e) {
                ErrorGUI err = new ErrorGUI("Incorrect input values");
                err.createGUI();
            }
        });

        free.addActionListener((ae) -> {
            try {
                (new GetFreeWorker(host, port)).execute();
            } catch (Exception e) {
                ErrorGUI err = new ErrorGUI("Incorrect host and port");
                err.createGUI();
            }
        });

        pane.add(jPanel);
        try {
            setIconImage(ImageIO.read(new File("C:/Users/yb/IdeaProjects/PL2/src/icons/client.png")));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        setVisible(true);
    }
}
