package server;

import java.net.Socket;
import utils.OutMT;
import utils.StreamData;
import utils.StreamHandler;

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
        // Inizializzo lo stream
        debugging.threadMessage("Inizializzo lo stream...");
        StreamHandler streamHandler = new StreamHandler(socket);
        debugging.threadMessage("...stream inizializzato");
        
        // Leggo l'oggetto serializzabile dallo stream
        debugging.threadMessage("Leggo l'oggetto serializzabile dallo stream");
        StreamData sObject = (StreamData) streamHandler.pullFromStream();
        
        // Switch/Case per la gestione dei comandi inviati al server
        debugging.threadMessage("Switch/Case per la gestione dei comandi inviati al server");
        while (!sObject.getCommand().equals("STOP")) {
            if (!sObject.isResponse()) {
                switch(sObject.getCommand()) {
                    // Il comando COUNT permette di contare il numero di parole presenti in una stringa (che puo' anche comprendere \n)
                    case "COUNT":
                        debugging.threadMessage("Counting");
                        String line = (String) sObject.getTarget();
                        debugging.threadMessage(line);
                        String[] counted = line.split(" ");
                        debugging.threadMessage("Invio dati calcolati: " + counted.length);
                        // Preparo l'oggetto da inviare
                        // Aggiungo dati all'oggetto da inviare
                        sObject.setTarget(new Integer(counted.length));

                        break;
                    // Di default viene ritornato un errore se il comando non viene riconosciuto
                    default:
                        // Preparo l'oggetto errore da inviare
                        // Aggiungo tipologia comando
                        sObject.setCommand("ERROR");
                        // Aggiungo messaggio errore
                        sObject.setTarget(new String("ERRORE-404: Comando non riconosciuto"));
                        
                        break;
                }
            } else {

                // Preparo l'oggetto errore da inviare
                // Aggiungo tipologia comando
                sObject.setCommand("ERROR");
                // Aggiungo messaggio errore
                sObject.setTarget(new String("ERRORE-500: Atteso messaggio, ricevuta risposta"));
            }
            
            //Invio l'oggetto sul buffer
            sObject.setResponse(true);
            streamHandler.pushToStream(sObject);
            
            // Leggo l'oggetto serializzabile dallo stream
            debugging.threadMessage("Leggo prossimo oggetto serializzabile dallo stream");
            sObject = (StreamData)streamHandler.pullFromStream();
        }
        // Fine del Switch/case per la gestione comandi
        
        // Chiudo tutti i flussi aperti
        debugging.threadMessage("Chiudo tutti gli stream");
        streamHandler.closeStream();
    }
}
