package server;

import org.json.JSONObject;
import server.services.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the server. Currently, it provides three services.
 * -- Login Service : This handles login requests.
 * -- Claim Seat Service : This handles claiming of seats.
 * -- Get Free Seats Service : This handles free seats requests.
 * No two services should serve a single request type.
 */
class SeatsServer {

    private ServerSocket server;
    private ExecutorService executor;
    private boolean started = false;
    private List<Service> services;

    /**
     * The constructor adds the services, initializes the thread pool and binds
     * the server to the port. Throws an exception if any of the above fails.
     *
     * @param port     - The port to start the server on.
     * @param services - The list of services to add to this server.
     */
    SeatsServer(int port, Service... services) {
        System.out.println("Starting server on port: " + port);
        this.services = new ArrayList<>();
        this.services.addAll(Arrays.asList(services));
        try {
            this.server = new ServerSocket(port);
            this.executor = Executors.newFixedThreadPool(100);
            started = true;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create server");
        }
    }

    /**
     * Start listening for connections. A new thread will be created for each client.
     */
    void startListening() {
        System.out.println("Listening for connections....");
        if (started) {
            while (true) {
                try {
                    Socket client = server.accept();
                    executor.execute(new HandleClient(client));

                } catch (Exception e) {
                    break;
                }
            }
        } else {
            throw new IllegalStateException("server has not been created yet.");
        }
    }

    /**
     * This class handles an incoming client.
     */
    private class HandleClient implements Runnable {
        /**
         * client - The socket corresponding to the new client
         */
        Socket client;

        private HandleClient(Socket client) {
            this.client = client;
        }

        /**
         * This reads the json request from the client and finds the service which can
         * handle this client(if any) and handles the request to that service.
         */
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                String req = br.readLine();
                System.out.println("Got a request from: " + client.getInetAddress());
                System.out.println(req);
                JSONObject reqJson = new JSONObject(req);
                String action = reqJson.getString("action");
                services.stream().filter(p -> p.match(action)).forEach(p -> p.process(reqJson, pw));
                client.close();
                System.out.println("Handled client " + client.getInetAddress() + " successfully.");
            } catch (Exception e) {
                System.out.println("Unable to handle client: " + client.getInetAddress() + ".");
                System.out.println("Exception: " + executor.toString());
            }
        }
    }
}
