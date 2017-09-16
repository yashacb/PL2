package client.workers;

import client.gui.ErrorGUI;
import client.gui.InfoGUI;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This thread tries to connect to the server and tries to claim a seat.
 */
public class ClaimSeatWorker extends SwingWorker<JSONObject, Void> {
    private String err = null ;
    private String host, rollNo, name ;
    private int port, seatNo ;

    public ClaimSeatWorker(String host, int port, String rollNo, String name, int seatNo) {
        this.host = host;
        this.rollNo = rollNo;
        this.name = name ;
        this.port = port;
        this.seatNo = seatNo ;
    }

    /**
     * This function runs in a background thread. It tries to claim the requested seat.
     * If there is an error, it sets the 'err' instance variable.
     */
    @Override
    public JSONObject doInBackground() throws Exception{
        try{
            Socket client = new Socket(host, port) ;
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream())) ;
            PrintWriter pw = new PrintWriter(client.getOutputStream(), true) ;
            JSONObject req = new JSONObject(), res ;

            req.put("action", "claim") ;
            req.put("rollNo", this.rollNo) ;
            req.put("name", this.name) ;
            req.put("seatNo", this.seatNo) ;
            pw.println(req.toString()) ;
            res = new JSONObject(br.readLine()) ;
            System.out.println(res.toString());

            boolean error = res.getBoolean("error") ;
            if(error){
                err = res.optString("msg") ;
                throw new IllegalArgumentException(res.optString("msg", "Unknown error occurred")) ;
            }

            return res ;
        }
        catch (Exception ex){
            if(err == null)
                err = "Unknown error occurred" ;
            return null ;
        }
    }

    /**
     * This method runs on the UI thread. If the seat is claimed successfully, an info gui is created.
     * Else, an error dialog is displayed.
     */
    @Override
    public void done(){
        if(err != null) {
            ErrorGUI egui = new ErrorGUI(err);
            egui.createGUI();
        }
        else {
            InfoGUI igui = new InfoGUI("Seat claimed successfully !") ;
            igui.createGUI() ;
        }
    }

}
