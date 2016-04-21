package utils;

/**
 *
 * @author Edoardo Zanoni
 */
public class SerializedObject implements java.io.Serializable {
    
    private static final long serialVersionUID = 110000L;
    private String command;
    private boolean response;
    private StringBuilder target;
    
    public SerializedObject() {
        
        this.command = new String();
        this.target = new StringBuilder();
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

    public StringBuilder getTarget() {
        
        return target;
    }

    public void setTarget(StringBuilder target) {
        
        this.target = target;
    }
    
    public void addToTarget(String string) {
        
        this.target.append(string);
    }
    
    public void resetTarget() {
        
        this.target.delete(0, this.target.length());
    }
    
    @Override
    public String toString() {
        
        return this.target.toString();
    }
}
