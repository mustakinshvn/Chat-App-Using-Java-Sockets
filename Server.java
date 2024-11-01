import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Runnable {
    
      Socket socket;

      public static Vector clients = new Vector();

      public Server (Socket socket){
        try {
            this.socket=socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
      }

      public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clients.add(writer);
              
            while (true) {
                String msg= reader.readLine().trim();
                System.out.println("Received message : "+ msg);

                for(int i=0; i<clients.size(); i++){
                    BufferedWriter bw = (BufferedWriter) clients.get(i);
                    bw.write(msg);
                    bw.write("\r\n");
                    bw.flush();
                }
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
      }


      

    public static void main(String[] args) {
     

        try {
            ServerSocket serverSocket = new ServerSocket(5000); // Create a ServerSocket bound to port 5000
          
            System.out.println("Server is running and waiting for client connections on port 5000...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // It blocks the execution of the program until a client connects.
              
               Server server = new Server(clientSocket);    
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
