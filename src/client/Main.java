package client;


import client.gui.ClientGUI;

import javax.swing.*;

/**
 * This class is the entry point to the client application. It creates the client gui.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI("Connect");
            gui.createGUI(gui.getContentPane());
        });
    }
}
