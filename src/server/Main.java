package server;

import server.gui.ServerGUI;
import server.services.ClaimService;
import server.services.GetFreeService;
import server.services.LoginService;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    /**
     * This is the entry point of the server.
     * It initializes all the services and starts the server with those services
     * on a new thread.
     * It also creates an empty teacher's layout.
     */
    public static void main(String[] args) {
        int size = 20, port = 9090;
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        SynchSeater synchSeater = new SynchSeater(size);
        SeatsServer server = new SeatsServer(port, new LoginService("localhost", 27017)
                , new ClaimService(synchSeater), new GetFreeService(synchSeater));
        Thread serverThread = new Thread(server::startListening);

        serverThread.start();
        SwingUtilities.invokeLater(() -> (new ServerGUI(size, synchSeater)).createGUI());
    }
}
