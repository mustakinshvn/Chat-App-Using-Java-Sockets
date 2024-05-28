import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        final ServerSocket serverSocket; // creating a server that can accept and handle client requests over a network.
        final Socket clientSocket; // Sockets enable applications to send and receive data over a network, such as the internet.
        final BufferedReader in; // used for reading text from an input stream efficiently
        final PrintWriter out; // used in the context of socket programming to send data to a server or a client.
        final Scanner sc = new Scanner(System.in);

        try {
            serverSocket = new ServerSocket(5000); // Create a ServerSocket bound to port 5000
            System.out.println("Server is running and waiting for a client connection on port 5000...");

            clientSocket = serverSocket.accept(); // It blocks the execution of the program until a client connects.
            System.out.println("Client connected!");

            out = new PrintWriter(clientSocket.getOutputStream(), true); // responsible for sending data to the client. Set auto-flush to true

            // new InputStreamReader( clientSocket.getInputStream()) : creates a stream reader for the socket. However, this stream reader only reads data as bytes; therefore, it must be passed to BufferedReader to be converted into characters.
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // use to read from clientSocket.

            // Sender thread: it contains the code the server will use to send messages to the client
            Thread sender = new Thread(() -> {
                String msg;
                while (true) {
                    msg = sc.nextLine();
                    out.println(msg);
                    out.flush(); // used to clear any buffered data in an output stream, ensuring that all data is actually written out to the destination.
                }
            });

            sender.start();

            // Receiver thread: it contains the code the server will use to receive messages from the client
            Thread receiver = new Thread(() -> {
                String msg;
                try {
                    msg = in.readLine();
                    while (msg != null) {
                        System.out.println("Client: " + msg);
                        msg = in.readLine();
                    }
                    System.out.println("Client disconnected.");
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
