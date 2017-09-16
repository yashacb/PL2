package server.services;

import org.json.JSONObject;
import server.SynchSeater;

import java.io.PrintWriter;

/**
 * This service handles requests which request free seats
 */
public class GetFreeService implements Service {

    /**
     * seater - The seater that is being used currently.
     */
    private SynchSeater seater;

    public GetFreeService(SynchSeater seater) {
        this.seater = seater;
    }

    /**
     *
     * @param action - The input action.
     * @return - Returns true if the input action is "free", false otherwise.
     */
    @Override
    public boolean match(String action) {
        return action.equals("free");
    }

    /**
     * This function takes a "free" request object and returns all the free seats.
     * The output written is of the form:
     * {"error": (true/false), "size": (No. of seats), "empty": [(Array of seat numbers which are free)])}
     *
     * @param req - The input request.
     * @param pw - the Writer to which results have to be written.
     */
    @Override
    public void process(JSONObject req, PrintWriter pw) {
        int size = seater.getSize();
        JSONObject ans = new JSONObject();

        ans.put("error", false);
        ans.put("size", size);
        for (int i = 0; i < size; i++) {
            if (seater.isEmpty(i))
                ans.append("empty", String.valueOf(i));
        }
        pw.println(ans.toString());
    }
}
