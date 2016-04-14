package runner;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.OutMT;

public class Worker implements Runnable{

    private String file = null;
    private StringBuilder messageBuilder = new StringBuilder();
    
    public Worker(String file) {
        
        this.file = file;
    }
    
    // worker process con protezione accesso risorse condivise
    @Override
    public void run() {
        //Nome del file da leggere + Stringa per leggere le singole righe + Random per simulazione attesa
        String line = null;

        try {
            //Misuro il tempo effettivo di esecuzione
            long startTime = System.nanoTime();

            //LETTURA------------------------------------------------------------------------		
            //apro il File di input e lo incapsulo in BufferedReader
            FileReader fileReader = new FileReader(Consts.filePath + file); //Throws FileNotFoundException
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Instauro una connessione con il server
            Socket socket = null;
            
            while (true) {
                try {
                    socket = new Socket(Consts.SERVER_IP, Consts.SERVER_PORT);
                    break;
                } catch (IOException ex) {
                    OutMT.threadMessage(messageBuilder.delete(0, messageBuilder.length()).append("In attesa di connessione a ").append(Consts.SERVER_IP).append(" : ").append(Consts.SERVER_PORT).toString());
                }
            }
            
            //Inizializzo i buffer per la lettura/scrittura sul socket
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedReader socketReader = new BufferedReader(inputStreamReader);

            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }

            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            PrintWriter socketWriter = new PrintWriter(bufferedWriter);
            
            //Finchè ci sono righe da leggere, leggo una riga e conto le parole ivi contenute
            int wordCount = 0;
            while ((line = bufferedReader.readLine()) != null) { //Throws IOException                
                
                socketWriter.println(line);
                
                while (true) {
                    if (inputStreamReader.ready()) {
                        
                        wordCount += Integer.valueOf(socketReader.readLine());
                        break;
                    }
                }
                // ELABORAZIONE
                String[] words = line.split(" ");
                wordCount += words.length;
            }

            //Chiudo il file di input
            bufferedReader.close();

            //------------CRITICAL SECTION START------------
            //apro il File di output (x APPEND) 

            //Acquisisco il lock per file di result
            System.out.println("Thread per " + file + " in acquisizione lock per file risultati.");
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
            System.out.println("Thread per " + file + " in rilascio lock per file risultati.");
            Lock.release(0);
            
            //Acquisisco il lock per file di log
            System.out.println("Thread per " + file + " in acquisizione lock per file log");
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
            System.out.println("Thread per " + file + " in rilascio lock per file log.");
            Lock.release(1);
            
        } //------------CRITICAL SECTION END------------
        catch (FileNotFoundException ex1) {
            String msg = "Impossibile trovare il file '" + Consts.filePath + file + "' - " + ex1.getMessage();
            System.out.println(msg);
        } catch (IOException ex2) {
            String msg = "I/O error: " + ex2.getMessage();
            System.out.println(msg);
        }
    }
}