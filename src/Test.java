import imageapi.ImageAPI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    public static void main(String[] args) {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        ImageAPI ip = new ImageAPI("images") ;

        ip.deleteAll() ;

        int added = addAll("E:/images", ip) ;
        System.out.println(added);

        SwingUtilities.invokeLater(() -> {
            for(int i = 1 ; i <= 20 ; i++) {
                GUI g = new GUI(ip.getImage(String.valueOf(1000 + i)));
            }
        });
    }

    public static int addAll(String dir, ImageAPI ip){
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            int ans = 0 ;
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println(ip.addImage(listOfFiles[i].getAbsolutePath())) ;
                    ans++ ;
                }
            }
            return ans ;
        }
        return 0 ;
    }
}

class GUI{
    public GUI(byte[] bytes){
        JFrame jf = new JFrame() ;
        JLabel label = new JLabel() ;

        label.setIcon(new ImageIcon(bytes));
        jf.add(label) ;
        jf.setLayout(new FlowLayout());
        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jf.setSize(500, 500);
        jf.setVisible(true);
    }
}