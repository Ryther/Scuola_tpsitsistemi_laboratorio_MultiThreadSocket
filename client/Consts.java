package runner;

/**
 *
 * @author Flavio
 */
public class Consts {
    public static String SERVER_IP = "10.10.11.7";
    public static int SERVER_PORT = 1234;
    
    public static final int     maxWaitTimeInMs = 500;
    public static final String  outputFile = "output.txt";
    public static final String  logFile = "log.txt";
    public static final String  workingPath = System.getProperty("user.dir")+"\\build\\classes";
    public static final String  filePath = System.getProperty("user.dir").replace("\\build\\classes", "/InputOutput/");
    public static final int     LOCKS = 2;
    //public static final String  worker = "Worker";
    
    public static final String  mutexResultsName = "lock1";
    public static final String  mutexLogName = "lock2";
}
