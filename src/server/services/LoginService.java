package server.services;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.io.PrintWriter;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class LoginService implements Service {

    /**
     * mcol - The collection to use for login requests.
     */
    private MongoCollection<Document> mcol;

    public LoginService(String host, int port) {
        MongoClient mc = new MongoClient(host, port);
        MongoDatabase mdb = mc.getDatabase("login");
        mcol = mdb.getCollection("login");
    }

    /**
     * @param action - The input action.
     * @return - Returns true if the input action is "login", false otherwise.
     */
    @Override
    public boolean match(String action) {
        return action.equals("login");
    }

    /**
     * This function takes a "login" request object and checks the user exists in the collection
     * ot not. It outputs a json object to the PrintWriter given.
     * The output written is of the form:
     * {"error": (true/false), "msg": (The error message if error has occurred.)}
     *
     * @param req - The input request.
     * @param pw  - the Writer to which results have to be written.
     */
    @Override
    public void process(JSONObject req, PrintWriter pw) {
        JSONObject ans = new JSONObject();
        System.out.println(req);
        try {
            String rollNo = req.optString("rollNo", "noRollNo");
            String name = req.optString("name", "noName");
            long numUsers = mcol.count(and(eq("rollNo", rollNo), eq("name", name)));
            if (numUsers != 1) {
                ans.put("error", true);
                ans.put("msg", "No such user exists");
            } else
                ans.put("error", false);
        } catch (Exception ex) {
            JSONObject res = new JSONObject();
            res.put("error", false);
            res.put("msg", "Unknown error occurred");
        }
        pw.println(ans.toString());
    }
}
