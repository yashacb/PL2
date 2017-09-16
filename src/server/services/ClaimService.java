package server.services;

import org.json.JSONObject;
import server.SynchSeater;

import java.io.PrintWriter;

/**
 * This service handles the claiming of seats.
 */
public class ClaimService implements Service {

    /**
     * seater - The seater that is being used currently.
     */
    private SynchSeater seater;

    public ClaimService(SynchSeater seater) {
        this.seater = seater;
    }

    /**
     * This service accepts actions only of the type "claim".
     *
     * @param action - The input action.
     * @return - Returns true if the input action is "claim", false otherwise.
     */
    @Override
    public boolean match(String action) {
        return action.equals("claim");
    }

    /**
     * This function takes a "claim" request object and checks if the seat in the request
     * can be claimed or not. It outputs a json object to the PrintWriter given.
     * The output written is of the form:
     * {"error": (true/false), "msg": (The error message if error has occurred.)}
     *
     * @param req - The input request.
     * @param pw  - the Writer to which results have to be written.
     */
    @Override
    public void process(JSONObject req, PrintWriter pw) {
        String roll = req.getString("rollNo");
        String name = req.getString("name");
        int seatNo = req.optInt("seatNo");
        JSONObject ans = new JSONObject();

        try {
            if (seater.claimSeat(roll, name, seatNo)) {
                ans.put("msg", "Seat claimed successfully");
                ans.put("error", false);
            } else {
                ans.put("error", true);
                ans.put("msg", "Seat has already been claimed");
            }
        } catch (Exception ex) {
            ans.put("error", true);
            ans.put("msg", "Invalid seat number");
        }
        pw.println(ans.toString());
    }
}
