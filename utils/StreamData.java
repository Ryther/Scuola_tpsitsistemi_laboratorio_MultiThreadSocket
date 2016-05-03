package utils;

import java.io.Serializable;

/**
 *
 * @author Edoardo Zanoni
 */
public class StreamData implements java.io.Serializable {
    
    private static final long serialVersionUID = 200000L;
    private String command;
    private boolean response;
    private Serializable serializable;
    private String objectType;
    
    public StreamData() {
        
        this.command = new String();
        this.serializable = (Serializable) new Object();
        this.objectType = new String();
    }

    public String getCommand() {
        
        return command;
    }

    public void setCommand(String command) {
        
        this.command = command;
    }

    public boolean isResponse() {
        
        return response;
    }

    public void setResponse(boolean response) {
        
        this.response = response;
    }

    public Object getTarget() {
        
        return serializable;
    }

    public void setTarget(Serializable serializable) {
        
        this.serializable = serializable;
        this.objectType = serializable.getClass().getCanonicalName();
    }
    
    @Override
    public String toString() {
        
        return this.serializable.toString();
    }
}
