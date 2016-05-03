package utils;

import java.io.Serializable;

/**
 *
 * @author Edoardo Zanoni
 */
public class StreamData implements java.io.Serializable {
    
    private static final long serialVersionUID = 130000L;
    private String command;
    private boolean response;
    private Object object;
    private String objectType;
    
    public StreamData() {
        
        this.command = new String();
        this.object = new Object();
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
        
        return object;
    }

    public void setTarget(Object object) {
        
        if (object instanceof Serializable) {
            
            this.object = object;
            this.objectType = object.getClass().getCanonicalName();
        } else {
            
            throw new ClassCastException();
        }
    }
    
    @Override
    public String toString() {
        
        return this.object.toString();
    }
}