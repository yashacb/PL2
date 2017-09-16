package client.workers;

import client.gui.ClientGUI;
import client.gui.ErrorGUI;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class handles login tasks.
 */
public class LoginWorker extends SwingWorker<Boolean, Void> {

    /**
     * err       - The error message in case an error occurs.
     * rollNo    - The roll No. of the student.
     * name      - The name of the student.
     * host      - The ip address of the server.
     * port      - The port the server is listening on.
     * clientGUI - The GUI to update when the processing is done.
     */
    private String err = null;
    private String rollNo, name, host;
    private int port;
    private ClientGUI clientGUI;

    public LoginWorker(String host, int port, String rollNo, String name, ClientGUI clientGUI) {
        this.host = host;
        this.port = port;
        this.rollNo = rollNo;
        this.name = name;
        this.clientGUI = clientGUI;
    }

    /**
     * This function sends a request to the server containing the rollNo and name of
     * the user. If an error is encountered, the "err" string is updated.
     *
     * @return - Returns true if the task succeeded, false otherwise.
     */
    @Override
    protected Boolean doInBackground() {
        try {
            Socket client = new Socket(host, port);
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
            JSONObject req = new JSONObject(), res;

            req.put("action", "login");
            req.put("rollNo", this.rollNo);
            req.put("name", this.name);
            pw.println(req.toString());
            res = new JSONObject(br.readLine());
            System.out.println(res.toString());

            boolean error = res.getBoolean("error");
            if (error) {
                err = res.optString("msg");
                throw new IllegalArgumentException(res.optString("msg", "Unknown error occurred"));
            }

            return true;
        } catch (Exception ex) {
            if (err == null)
                err = "Unable to connect to server";
            return false;
        }
    }

    /**
     * Updates the clientGUI if there is no error. Displays an error box otherwise.
     */
    @Override
    protected void done() {
        if (err != null) {
            ErrorGUI egui = new ErrorGUI(err);
            egui.createGUI();
        } else {
            clientGUI.postLoginGUI(clientGUI.getContentPane(), rollNo, name, host, port);
        }
    }
}
