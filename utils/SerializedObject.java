package utils;

/**
 *
 * @author Edoardo Zanoni
 */
public class SerializedObject implements java.io.Serializable {
    
    private static final long serialVersionUID = 120000L;
    private String command;
    private boolean response;
    private Object target;
    private String objectType;
    
    public SerializedObject() {
        
        this.command = new String();
        this.target = new Object();
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
        
        return target;
    }

    public void setTarget(Object target) {
        
        this.target = target;
        this.objectType = target.getClass().getCanonicalName();
    }
    
    @Override
    public String toString() {
        
        return this.target.toString();
    }
}
