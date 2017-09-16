package client.gui;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GetFreeGUI extends JFrame {
    private String[] empty = null;

    public GetFreeGUI(String[] empty) {
        super("Empty Seats");
        this.empty = empty;
    }

    public void createGUI(int size) {
        int c = 5, r = size / 5 + (size % 5 == 0 ? 0 : 1);
        setLayout(new GridLayout(r, c));
        Set<String> empty = new HashSet<>();

        empty.addAll(Arrays.asList(this.empty));

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                int seatNo = i * c + j;
                if (seatNo < size) {
                    JLabel toAdd;
                    if (empty.contains(String.valueOf(seatNo)))
                        toAdd = new JLabel("<html>" + seatNo + " : <font color='green'>Empty</font></html>");
                    else
                        toAdd = new JLabel("<html>" + seatNo + " : <font color='red'>Full</font></html>");
                    toAdd.setFont(new Font(toAdd.getFont().getName(), Font.BOLD, 17));
                    toAdd.setHorizontalAlignment(SwingConstants.CENTER);
                    toAdd.setVerticalAlignment(SwingConstants.CENTER);
                    add(toAdd);
                    toAdd.setBorder(new EmptyBorder(50, 50, 50, 50));
                }
            }
        }
        try {
            setIconImage(ImageIO.read(new File("C:/Users/yb/IdeaProjects/PL2/src/icons/seats.png")));
        } catch (Exception e) {
            System.out.println("Unable to load image icon");
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(c * 200, r * 200);
        setLocationRelativeTo(null);
        setVisible(true);
//        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
