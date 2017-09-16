package client.gui;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ErrorGUI extends JFrame {

    private String msg;

    public ErrorGUI(String msg) {
        super("Error");
        this.msg = msg;
    }

    public void createGUI() {
        JLabel err = new JLabel(this.msg);
        err.setHorizontalAlignment(SwingConstants.CENTER);
        err.setVerticalAlignment(SwingConstants.CENTER);
        err.setFont(new Font(err.getFont().getName(), Font.BOLD, 17));
        setSize(350, 200);
        add(err);
        try {
            setIconImage(ImageIO.read(new File("C:/Users/yb/IdeaProjects/PL2/src/icons/error.png")));
        } catch (Exception e) {
            System.out.println("Unable to load icon");
        }
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}

