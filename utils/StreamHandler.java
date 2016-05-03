package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class StreamHandler {
    
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    public StreamHandler(Socket socket) {
        
        this.inputStream = null;
        this.outputStream = null;
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            this.outputStream = new ObjectOutputStream(os);
            this.outputStream.flush();
            this.inputStream = new ObjectInputStream(is);
        } catch (IOException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object pullFromStream() {
        
        // Leggo l'oggetto dallo stream
        Serializable serializable = null;
        try {
            serializable = (Serializable) this.inputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return serializable;
    }
    
    public void pushToStream(Serializable serializable) {
        
        //Invio l'oggetto sul buffer
        try {
            this.outputStream.writeObject(serializable);
        } catch (IOException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeStream() {
        
        try {
            this.inputStream.close();
            this.outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
