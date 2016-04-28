package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        
        // Switch/Case per la gestione dei comandi inviati al server
        debugging.threadMessage("Switch/Case per la gestione dei comandi inviati al server");
        if (!sObject.isResponse()) {
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
                    // Preparo l'oggetto da inviare
                    sObject.setTarget(new Integer(counter));
                    sObject.setResponse(true);
                    // Inizializzo i buffer per la scrittura sul socket
                    ObjectOutputStream outputStream = null;
            
                    try {
                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                    } catch (IOException ex) {
                        Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //Invio l'oggetto sul buffer
                    try {
                        outputStream.writeObject(sObject);
                    } catch (IOException ex) {
                        Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                // Di default viene ritornato un errore se il comando non viene riconosciuto
                default:
                    // Preparo l'oggetto errore da inviare
                    sObject.setCommand("ERROR");
                    sObject.setTarget(new String("ERRORE-404: Comando non riconosciuto"));
                    sObject.setResponse(true);
                    break;
            }
        } else {
            
            // Preparo l'oggetto errore da inviare
            sObject.setCommand("ERROR");
            sObject.setTarget(new String("ERRORE-500: Atteso messaggio, ricevuta risposta"));
            sObject.setResponse(true);
        }
        // Fine del Switch/case per la gestione comandi
        
        // Chiudo tutti i flussi aperti
        // Chiudo il canale in lettura non più necessario
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Fine chiusura canale in lettura
        // Chiudo il canale (e relativi wrapper) di scrittura
        
        // Fine chiusura del canale (e relativi wrapper) di scrittura
    }
}
