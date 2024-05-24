import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

        final ServerSocket serverSocket;   //creating a server that can accept and handle client requests over a network.

        final Socket clientSocket;      //Sockets enable applications to send and receive data over a network, such as the internet.

        final BufferedReader in;

        final PrintWriter out;  //used in the context of socket programming to send data to a server or a client.

        final Scanner sc= new Scanner(System.in);


        try{
            
            serverSocket = new ServerSocket(5000);  // Create a ServerSocket bound to port 5000

            clientSocket = serverSocket.accept();   //It blocks the execution of the program until a client connects.
            
            out = new PrintWriter(clientSocket.getOutputStream());  //responsible for sending data to the client.

    
        }catch(IOException e){
            e.printStackTrace();
            
        }
    }




    }

  
