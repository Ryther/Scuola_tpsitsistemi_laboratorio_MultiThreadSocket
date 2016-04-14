package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;
import utils.SerializedObject;

/**
 *
 * @author Edoardo Zanoni
 */
public class Service implements Runnable {

    private Socket socket;

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        SerializedObject sObject = null;
        try {
            sObject = (SerializedObject) inputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch(sObject.getCommand()) {
            case "COUNT":
                //PROGRAMMANDO QUI
                break;
        }
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);

        while (true) {

            try {
                if (inputStreamReader.ready()) {
                    if (bufferedReader.readLine().equals("stop")) {
                        break;
                    }
                    
                    OutMT.threadMessage(bufferedReader.readLine());
                }
                OutMT.threadMessage(bufferedReader.readLine());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
