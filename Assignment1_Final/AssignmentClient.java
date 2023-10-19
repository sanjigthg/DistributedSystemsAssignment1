import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class AssignmentClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextField serverAddressInput;
    private JTextField usernameInput;

    public AssignmentClient() {
        JFrame frame = new JFrame("Client Request Panel");
        frame.setLocation(100, (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 3);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        serverAddressInput = new JTextField();
        usernameInput = new JTextField();
        JButton dateButton = new JButton("Get Date");
        JButton timeButton = new JButton("Get Time");

        dateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("DATE");
            }
        });

        timeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequest("TIME");
            }
        });

        frame.setLayout(new GridLayout(3, 2));
        frame.add(new JLabel("Server Address:"));
        frame.add(serverAddressInput);
        frame.add(new JLabel("Username:"));
        frame.add(usernameInput);
        frame.add(dateButton);
        frame.add(timeButton);

        frame.setVisible(true);

        try {
            String serverAddress = JOptionPane.showInputDialog("Enter server address:");
            String clientUsername = JOptionPane.showInputDialog("Enter your username:");

            socket = new Socket(serverAddress, 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(clientUsername);

            serverAddressInput.setText(serverAddress);
            usernameInput.setText(clientUsername);
            Thread receiveThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String request) {
        out.println(request);

        try {
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AssignmentClient();
    }
}
