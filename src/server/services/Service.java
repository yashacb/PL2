package server.services;

import org.json.JSONObject;

import java.io.PrintWriter;

/**
 * This is the interface a class must implement if it has to be added as a service
 * to the server.
 */
public interface Service {
    /**
     * @param action - The input action.
     * @return - Returns whether the service can handle such an action or not.
     */
    boolean match(String action);

    /**
     * The calling function should first check if the request can be handled or not.
     * @param req - The input request.
     * @param pw - the Writer to which results have to be written.
     */
    void process(JSONObject req, PrintWriter pw);
}
