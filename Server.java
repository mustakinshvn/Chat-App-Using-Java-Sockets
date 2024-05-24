import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

        final ServerSocket serverSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc= new Scanner(System.in);


        try{
            serverSocket = new ServerSocket(5000);
    
        }catch(IOException e){
            e.printStackTrace();
            
        }
    }

    


    }

  
