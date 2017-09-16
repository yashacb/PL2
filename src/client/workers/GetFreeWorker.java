package client.workers;

import client.gui.ErrorGUI;
import client.gui.GetFreeGUI;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * This Worker Thread connects to the server and tries to retrieve the seats that are currently empty.
 */

public class GetFreeWorker extends SwingWorker<String[], Void> {

    private String host;
    private int port;
    private String err = null;
    private int size;

    public GetFreeWorker(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * This function runs in a background thread. It tries to retrieve the seats that are empty.
     * If there is an error, it sets the 'err' instance variable.
     */
    @Override
    protected String[] doInBackground() throws Exception {
        String[] empty;
        try {
            Socket client = new Socket(host, port);
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
            JSONObject req = new JSONObject(), res;
            JSONArray ja;

            req.put("action", "free");
            pw.println(req.toString());
            String response = br.readLine();
            res = new JSONObject(response);
            ja = res.getJSONArray("empty");
            this.size = res.getInt("size");
            System.out.println(res.toString());
            empty = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++)
                empty[i] = ja.getString(i);
        } catch (Exception e) {
            err = "Unable to connect to server";
            return null;
        }
        return empty;
    }

    /**
     * This method runs on the UI thread. If the empty list is successfully retrieved, a new window is created,
     * which shows the same. Else, an error dialog is displayed.
     */
    @Override
    protected void done() {
        if (err != null) {
            ErrorGUI error = new ErrorGUI(err);
            error.createGUI();
            return;
        }
        try {
            String[] res = get();
            System.out.println(Arrays.toString(res));
            GetFreeGUI gui = new GetFreeGUI(res);
            gui.createGUI(size);
        } catch (Exception e) {
            new ErrorGUI(e.toString());
        }
        super.done();
    }
}
