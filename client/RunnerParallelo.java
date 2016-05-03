package runner;

import java.io.*;
import java.util.Arrays;

public class RunnerParallelo {

    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("user.dir", Consts.workingPath);
        
        int numInput = args.length;
        if (numInput < 2) {
            System.out.println("Usage: java Runner input1.txt input2.txt [.. inputN.txt]");
        } else {

            //Pulisco il file di output prima dell'esecuzione
            File f = new File(Consts.filePath);
            if (!f.exists()) {
                
                f.mkdir();
            }
            
            f = new File(Consts.filePath + Consts.outputFile);
            f.delete();

            // Pulisco il file di file di log - x versione 2
            f = new File(Consts.filePath + Consts.logFile);
            f.delete();

            //Inizializzo l'array di threads
            Thread[] threads = new Thread[numInput];
            
            //Misuro il tempo prima dell'esecuzione dei processi
            long startTime = System.nanoTime();

            System.out.println("Runner - esecuzione threads");
            //Istanzio e lancio N processi in sequenza
            for (int i = 0; i < numInput; i++) {

                System.out.println("Runner - creazione thread #" + i);
                threads[i] = new Thread(new Worker(args[i]));
                threads[i].start();
            }

            int threadCounter = 0;
            boolean[] isTerminated = new boolean[numInput];
            while (threadCounter < numInput) {
                for (int i = 0; i < numInput; i++) {

                    if (!threads[i].isAlive() && !isTerminated[i]) {
                        threads[i].join(100);
                        System.out.println("Runner - Thread #" + i + " terminato");
                        isTerminated[i] = true;
                        threadCounter++;
                    } 

                    //Misuro il tempo dopo l'esecuzione dei processi e calcolo il tempo di elaborazione
                }
            }
            
            long endTime = System.nanoTime();
                    long elapsedTime = endTime - startTime;
                    System.out.println("Runner - Tempo di esecuzione complessivo: " + elapsedTime / 1000000 + " ms");
        }
    }
}