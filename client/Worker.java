package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Lock;
import utils.OutMT;
import utils.StreamData;
import utils.StreamHandler;

public class Worker implements Runnable{

    private static final int RESULTS_LOCK = 0;
    private static final int LOG_LOCK = 1;
    private OutMT debugging = new OutMT();
    private String file = null;
    private StringBuilder messageBuilder;
    private InetAddress host;
    
    public Worker(String file) {
        
        this.file = file;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // worker process con protezione accesso risorse condivise
    @Override
    public void run() {
        debugging.activate();
        messageBuilder = new StringBuilder();
        //Nome del file da leggere + Stringa per leggere le singole righe + Random per simulazione attesa

        try {
            //Misuro il tempo effettivo di esecuzione
            long startTime = System.nanoTime();

            //LETTURA------------------------------------------------------------------------		
            //apro il File di input e lo incapsulo in BufferedReader
            FileReader fileReader = new FileReader(Consts.filePath + file); //Throws FileNotFoundException
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            //Preparo l'oggetto da inviare
            StreamData sData = new StreamData();
            String line = null;
            int wordCount = 0;
            //Instauro una connessione con il server
            Socket socket = null;
            
            while (true) {
                try {
                    socket = new Socket(host, Consts.SERVER_PORT);
                    break;
                } catch (IOException ex) {
                    OutMT.threadMessage(messageBuilder.delete(0, messageBuilder.length()).append("In attesa di connessione a ").append(Consts.SERVER_IP).append(" : ").append(Consts.SERVER_PORT).toString());
                    //Attendo per non intasare di richieste il server
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
            
            // Inizializzo i buffer per la scrittura/lettura sul socket
            debugging.threadMessage("Inizializzo lo stream...");
            StreamHandler streamHandler = new StreamHandler(socket);
            debugging.threadMessage("...stream inizializzato");
            
            while ((line = bufferedReader.readLine()) != null) { //Throws IOException                
                //Aggiungo comando all'oggetto
                sData.setCommand("COUNT");
                //Aggiungo dati all'oggetto
                //Aggiungo il messaggio all'oggetto serializzabile
                sData.setTarget(line);
                // Inizializzo variabile response a false
                sData.setResponse(false);
                // Invio oggetto da elaborare
                debugging.threadMessage("Invio oggetto da elaborare");
                streamHandler.pushToStream(sData);
                
                // Leggo oggetto elaborato
                debugging.threadMessage("Inizio ricezione oggetto elaborato");
                sData = (StreamData) streamHandler.pullFromStream();
                wordCount += (int) sData.getTarget();
                debugging.threadMessage(String.valueOf(wordCount));
            }
            // Aggiungo comando di stop all'oggetto
            sData.setCommand("STOP");
            // Inizializzo variabile response a false
            sData.setResponse(false);
            // Invio oggetto stop al servizio
            streamHandler.pushToStream(sData);
            
            //Chiudo il file di input
            bufferedReader.close();
            fileReader.close();
            streamHandler.closeStream();
            
            //------------CRITICAL SECTION START------------
            //apro il File di output (x APPEND) 

            //Acquisisco il lock per file di result
            debugging.threadMessage("Thread per " + file + " in acquisizione lock per file risultati.");
            Lock.create(RESULTS_LOCK);
            Lock.acquire(RESULTS_LOCK);

            //Scrivo il conteggio delle parole nel file di output
            String results = "File '" + file + "'\r\n"
                    + "\tParole: " + wordCount + "\r\n";

            RandomAccessFile fileWriter = new RandomAccessFile(Consts.filePath + Consts.outputFile, "rwd");
            long outputLength = fileWriter.length();
            fileWriter.seek(outputLength > 0 ? outputLength - 1 : 0);
            fileWriter.write(results.getBytes());
            //Chiudo il file di output
            fileWriter.close();
            //Rilascio il lock
            debugging.threadMessage("Thread per " + file + " in rilascio lock per file risultati.");
            Lock.release(RESULTS_LOCK);
            
            //Acquisisco il lock per file di log
            debugging.threadMessage("Thread per " + file + " in acquisizione lock per file log");
            Lock.create(LOG_LOCK);
            Lock.acquire(LOG_LOCK);
            
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;

            //Scrivo i tempi nel file di log
            String log = "File '" + file + "'\r\n"
                    + "\tTempo di esecuzione effettivo: " + elapsedTime / 1000000 + " ms\r\n";

            RandomAccessFile logWriter = new RandomAccessFile(Consts.filePath + Consts.logFile, "rwd");
            long logLength = logWriter.length();
            logWriter.seek(logLength > 0 ? logLength - 1 : 0);
            logWriter.write(log.getBytes());
            //Chiudo il file di log
            logWriter.close();
            //Rilascio il lock
            debugging.threadMessage("Thread per " + file + " in rilascio lock per file log.");
            Lock.release(LOG_LOCK);
            
        } //------------CRITICAL SECTION END------------ //------------CRITICAL SECTION END------------
        catch (FileNotFoundException ex1) {
            String msg = "Impossibile trovare il file '" + Consts.filePath + file + "' - " + ex1.getMessage();
            debugging.threadMessage(msg);
        } catch (IOException ex2) {
            String msg = "I/O error: " + ex2.getMessage();
            debugging.threadMessage(msg);
        }
    }
}