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
        final Scanner sc = new Scanner(System.in); // object to read data from users (Keyboard)

        try {
            clientSocket = new Socket("127.0.0.1", 5000); // Connect to the server at localhost on port 5000
            System.out.println("Connected to the server!");

            out = new PrintWriter(clientSocket.getOutputStream(), true); // Set auto-flush to true
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread sender = new Thread(() -> {
                while (true) {
                    String msg = sc.nextLine();
                    out.println(msg);
                    out.flush();
                }
            });

            sender.start();

            Thread receiver = new Thread(() -> {
                try {
                    String msg = in.readLine();
                    while (msg != null) {
                        System.out.println("Server: " + msg);
                        msg = in.readLine();
                    }
                    System.out.println("Server disconnected.");
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            receiver.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
