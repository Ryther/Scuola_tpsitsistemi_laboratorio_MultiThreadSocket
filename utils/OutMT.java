package utils;

/**
 *
 * @author Flavio
 */
public class OutMT {
    
    private static boolean active = false;
    
    // Display a message, preceded by
    // the name of the current thread
    public static void threadMessage(String message) {
        
        if (active) {
            String threadName =
                Thread.currentThread().getName();
            System.out.format("%s: %s%n",
                              threadName,
                              message);
        }
    }
    
    public void activate() {
        
        this.active = true;
    }
    
    public void deactivate() {
        
        this.active = false;
    }
}
