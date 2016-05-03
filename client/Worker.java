package runner;

import utils.Lock;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;
import utils.SerializedObject;

public class Worker implements Runnable{

    private OutMT debugging = new OutMT();
    private String file = null;
    private StringBuilder messageBuilder = new StringBuilder();
    
    public Worker(String file) {
        
        this.file = file;
    }
    
    // worker process con protezione accesso risorse condivise
    @Override
    public void run() {
        debugging.activate();
        //Nome del file da leggere + Stringa per leggere le singole righe + Random per simulazione attesa

        try {
            //Misuro il tempo effettivo di esecuzione
            long startTime = System.nanoTime();

            //LETTURA------------------------------------------------------------------------		
            //apro il File di input e lo incapsulo in BufferedReader
            FileReader fileReader = new FileReader(Consts.filePath + file); //Throws FileNotFoundException
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            //Preparo l'oggetto da inviare
            SerializedObject sObject = new SerializedObject();
            //Aggiungo comando all'oggetto
            sObject.setCommand("COUNT");
            //Aggiungo dati all'oggetto
            String line = null;
            messageBuilder.delete(0, messageBuilder.length());
            while ((line = bufferedReader.readLine()) != null) { //Throws IOException                
                
                messageBuilder.append(line).append("\n");
            }
            //Aggiungo il messaggio all'oggetto serializzabile
            sObject.setTarget(new StringBuilder(messageBuilder));
            // Inizializzo variabile response a false
            sObject.setResponse(false);
            
            //Chiudo il file di input
            bufferedReader.close();
            fileReader.close();

            //Instauro una connessione con il server
            Socket socket = null;
            
            while (true) {
                try {
                    socket = new Socket(Consts.SERVER_IP, Consts.SERVER_PORT);
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
            
            // Inizializzo i buffer per la scrittura sul socket
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            // Invio oggetto al server
            outputStream.writeObject(sObject);


            // Inizializzo lo stream in input
            debugging.threadMessage("Inizializzo lo stream in input");
            ObjectInputStream inputStream = null;
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Fine inizializzazione input
            
            debugging.threadMessage("Inizio ricezione");
            try {
                sObject = (SerializedObject) inputStream.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            debugging.threadMessage("Oggetto letto");
            int wordCount = (int) sObject.getTarget();
            debugging.threadMessage(String.valueOf(wordCount));
            
            //------------CRITICAL SECTION START------------
            //apro il File di output (x APPEND) 

            //Acquisisco il lock per file di result
            debugging.threadMessage("Thread per " + file + " in acquisizione lock per file risultati.");
            Lock.acquire(0);

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
            Lock.release(0);
            
            //Acquisisco il lock per file di log
            debugging.threadMessage("Thread per " + file + " in acquisizione lock per file log");
            Lock.acquire(1);
            
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
            Lock.release(1);
            
        } //------------CRITICAL SECTION END------------
        catch (FileNotFoundException ex1) {
            String msg = "Impossibile trovare il file '" + Consts.filePath + file + "' - " + ex1.getMessage();
            debugging.threadMessage(msg);
        } catch (IOException ex2) {
            String msg = "I/O error: " + ex2.getMessage();
            debugging.threadMessage(msg);
        }
    }
}