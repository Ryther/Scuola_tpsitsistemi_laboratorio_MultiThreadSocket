package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;
import utils.SerializedObject;

/**
 *
 * @author Edoardo Zanoni
 */
public class Service implements Runnable {

    OutMT debugging = new OutMT();
    private Socket socket;

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        debugging.activate();
        // Inizializzo lo stream in input
        debugging.threadMessage("Inizializzo lo stream in input");
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Fine inizializzazione input
        
        // Inizializzo lo stream in output
        debugging.threadMessage("Inizializzo lo stream in output");
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter, true);
        // Fine inizializzazione input
        
        // Leggo l'oggetto serializzabile dallo stream
        debugging.threadMessage("Leggo l'oggetto serializzabile dallo stream");
        SerializedObject sObject = null;
        try {
            sObject = (SerializedObject) inputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Fine lettura oggetto serializzabile dallo stream
        
        // Chiudo il canale in lettura non più necessario
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Fine chiusura canale in lettura
        
        // Switch/Case per la gestione dei comandi inviati al server
        debugging.threadMessage("Switch/Case per la gestione dei comandi inviati al server");
        switch(sObject.getCommand()) {
            // Il comando COUNT permette di contare il numero di parole presenti in una stringa (che puo' anche comprendere \n)
            case "COUNT":
                debugging.threadMessage("Counting");
                int counter=0;
                Scanner scanner = new Scanner(sObject.getTarget().toString());
                while (scanner.hasNextLine()) {
                    
                    String line = scanner.nextLine();
                    debugging.threadMessage(line);
                    String[] counted = line.split(" ");
                    counter+=counted.length;
                }
                debugging.threadMessage("Invio dati calcolati: " + counter);
                printWriter.println(counter);
                break;
            // Di default viene ritornato un errore se il comando non viene riconosciuto
            default:
                printWriter.println(new StringBuilder("ERRORE-404: Comando non riconosciuto").toString());
                break;
        }
        // Fine del Switch/case per la gestione comandi
        
        // Chiudo tutti i flussi aperti
        printWriter.close();
        try {
            bufferedWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outputStreamWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
