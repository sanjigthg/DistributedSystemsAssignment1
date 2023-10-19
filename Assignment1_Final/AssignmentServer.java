import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AssignmentServer {
    private static int clientCount = 0;
    private static ArrayList<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started. Waiting for a client connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                clientCount++;
                String clientAddress = clientSocket.getInetAddress().getHostAddress();

                System.out.println("Client " + clientCount + " connected from: " + clientAddress);

                new ClientHandler(clientSocket, clientCount).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int clientNumber;
        private String clientUsername;

        public ClientHandler(Socket clientSocket, int clientNumber) {
            this.clientSocket = clientSocket;
            this.clientNumber = clientNumber;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                clientUsername = in.readLine();

                out.println("Welcome, you are client " + clientNumber + ", with username: " + clientUsername);

                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Client " + clientNumber + " (" + clientUsername + ") connected from: " + clientAddress);

                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equals("DATE")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = dateFormat.format(new Date());
                        sendMessageToAllClients(clientUsername + " has requested the date: " + currentDate);
                    } else if (request.equals("TIME")) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = timeFormat.format(new Date());
                        sendMessageToAllClients(clientUsername + " has requested the time: " + currentTime);
                    } else {
                        sendMessageToAllClients(clientUsername + " made an invalid request: " + request);
                    }
                }

                System.out.println("Client (" + clientUsername + ") disconnected.");

                clientSocket.close();
            } catch (IOException e) {
                // Client disconnected unexpectedly
                System.out.println("Client disconnected unexpectedly.");
                e.printStackTrace();
            }
        }

        private void sendMessageToAllClients(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
