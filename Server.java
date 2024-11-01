import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>()); // thread-safe set to store connected clients
    private static Map<String, ClientHandler> clientsByName = Collections.synchronizedMap(new HashMap<>()); // map to store clients by name

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
                clientsByName.put(clientName, this);
                broadcastMessage(clientName + " has joined the chat!");
                System.out.println(clientName + " connected from " + socket.getInetAddress());


                sendOnlineUsers(); // Send current online users list to the new client
               

                // handle client msgs
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }else if (message.equals("/online")) {
                        sendOnlineUsers();
                    } else if(message.startsWith("/pm ")){
                        handlePrivateMessage(message);

                    }else {
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        broadcastMessage("[" + timestamp + "] " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                disconnect();
            }
        }


        private void handlePrivateMessage(String message) {
            // Format: /pm username message
            try {
                String[] parts = message.split(" ", 3);
                if (parts.length < 3) {
                    out.println("Invalid private message format. Use: /pm username message");
                    return;
                }

                String targetUsername = parts[1];
                String pmMessage = parts[2];
                ClientHandler targetClient = clientsByName.get(targetUsername);

                if (targetClient == null) {
                    out.println("Error: User '" + targetUsername + "' is not online.");
                    return;
                }

                // Send to recipient
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String formattedMessage = String.format("[%s] [PM from %s]: %s", timestamp, clientName, pmMessage);
                targetClient.out.println(formattedMessage);

                // Send confirmation to sender
                String confirmationMessage = String.format("[%s] [PM to %s]: %s", timestamp, targetUsername, pmMessage);
                out.println(confirmationMessage);

            } catch (Exception e) {
                out.println("Error sending private message: " + e.getMessage());
            }
        }


        private void sendOnlineUsers() {
            StringBuilder userList = new StringBuilder("\n=== Online Users ===\n");
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    userList.append("- ").append(client.clientName).append("\n");
                }
            }
            userList.append("=================");
            out.println(userList.toString());
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