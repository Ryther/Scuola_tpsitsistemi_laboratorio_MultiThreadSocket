package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;

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

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

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
