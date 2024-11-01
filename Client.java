import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final Socket clientSocket; // socket used by client to send and receive data from the server
        final BufferedReader in; // object to read data from the server
        final PrintWriter out; // object to write data into the socket
        final Scanner sc = new Scanner(System.in); // object to read data from users (Keyboa

        try {
            clientSocket = new Socket("127.0.0.1", 5000); // connect to server
            System.out.println("Connected to the server!");

            out = new PrintWriter(clientSocket.getOutputStream(), true); // get output stream to send data to the server
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // get input stream from the server

            // Prompt for the client's name
            System.out.print("Enter your name: ");
            String clientName = sc.nextLine();
            out.println(clientName);

            // Thread for sending messages
            Thread sender = new Thread(() -> {
                while (!clientSocket.isClosed()) {
                    String msg = sc.nextLine();
                    out.println(clientName + ": " + msg); // prefix message with client's name
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
                } 
            });
            receiver.start();

        } catch (IOException e) {
            System.out.println("Unable to connect.");
        }
    }

   
}
