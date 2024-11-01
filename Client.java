import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket clientSocket; // socket used by client to send and receive data from the server
        final BufferedReader in; // object to read data from the server
        final PrintWriter out; // object to write data into the socket
        final Scanner sc = new Scanner(System.in); // object to read data from users (Keyboard)

        try {
            clientSocket = new Socket("127.0.0.1", 5000); // connect to server
            System.out.println("Connected to the server!");

            out = new PrintWriter(clientSocket.getOutputStream(), true); // get output stream to send data to the server
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // get input stream from the server

            // Prompt for the client's name
            System.out.print("Enter your name: ");
            String clientName = sc.nextLine();
            out.println(clientName);


            // Print commands help
            System.out.println("Available commands:");
            System.out.println("/online - Show online users");
            System.out.println("/quit - Exit the chat");

            // Thread for sending messages
            Thread sender = new Thread(() -> {
                while (!clientSocket.isClosed()) {
                    String msg = sc.nextLine();
                    if (msg.equals("/online")) {
                        out.println("/online");
                    } else {
                        out.println(clientName + ": " + msg);
                    }
                    
                    if (msg.equalsIgnoreCase("/quit")) {
                        break;
                    }
                }
            });
            sender.start();

            // Thread for receiving messages
            Thread receiver = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg); // show received message
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                } finally {
                    closeConnection(clientSocket, in, out, sc);
                }
            });
            receiver.start();

        } catch (IOException e) {
            System.out.println("Unable to connect.");
        }
    }

    // Close resources method
    private static void closeConnection(Socket socket, BufferedReader in, PrintWriter out, Scanner sc) {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (sc != null) sc.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error closing resources.");
        }
    }

   
}
