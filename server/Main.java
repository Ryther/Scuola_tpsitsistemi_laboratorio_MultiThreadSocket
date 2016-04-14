package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;

/**
 *
 * @author Edoardo Zanoni
 */
public class Main {

    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setSoTimeout(5000);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        Socket clientSocket = null;
        
        while (true) {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    OutMT.threadMessage("Client connesso!");
                    Thread thread = new Thread(new Service(clientSocket));
                    thread.start();
                    break;
                } catch (IOException ex) {
                    System.out.println("In attesa di connessione...");
                    //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
//        try {
//            serverSocket.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
