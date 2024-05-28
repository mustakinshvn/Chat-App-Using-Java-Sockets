import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final Socket clienSocket; //socket used by client to and and recieve data from server

        final BufferedReader in; //object to read data from server

        final PrintWriter out; //object to write data into socket

        final Scanner sc= new Scanner(System.in); //object to read data from users (Keyboard)

        try{
            clienSocket = new Socket("177.0.0.1", 5000);
            out= new PrintWriter(clienSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));

            Thread sender = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    while (true) {
                        msg= sc.nextLine();
                        out.println(msg);
                        out.flush();
                    }
                }
            });

            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                   try{
                    msg= sc.nextLine();
                    while (msg!=null) {
                        System.out.println("Server : " +msg );
                        out.close();
                        clienSocket.close();
                    }
                   } catch(IOException e){
                    e.printStackTrace();
                   }
                }
                
            });

            receiver.start();


        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
