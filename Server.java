import java.io.*;
import java.net.*;
import java.util.*;
public class Server {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>()); // thread-safe set to store connected clients

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket); // create a new client handler for the client
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;
       
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // getting client name
                clientName = in.readLine();
                broadcastMessage(clientName + " has joined the chat!");
                System.out.println(clientName + " connected from " + socket.getInetAddress());

                // handle client msgs
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                  
                    broadcastMessage( clientName + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void broadcastMessage(String message) {
            System.out.println(message); 
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != this) { // Don't send message back to sender
                        client.out.println(message);
                    }
                }
            }
        }

        private void disconnect() {
            try {
                clients.remove(this);
                if (clientName != null) {
                    broadcastMessage(clientName + " has left the chat.");
                }
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error disconnecting client: " + e.getMessage());
            }
        }


    }
}