package utils;

/**
 *
 * @author Edoardo Zanoni
 */
public class SerializedObject implements java.io.Serializable {
    
    private String command;
    private StringBuilder target;

    public String getCommand() {
        
        return command;
    }

    public void setCommand(String command) {
        
        this.command = command;
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
}
