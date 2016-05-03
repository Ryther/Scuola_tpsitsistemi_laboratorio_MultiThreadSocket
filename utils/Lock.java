package utils;

import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lock {
    private static final ReentrantLock reLock = new ReentrantLock();
    private static final List<ReentrantLock> lock = new LinkedList<ReentrantLock>();

    public static void create(int n) {

        reLock.lock();
        if (n == lock.size()) {
            
            lock.add(new ReentrantLock());
        } else if (n > lock.size()) {
            
            Logger.getLogger(Lock.class.getName()).log(Level.SEVERE, "Il numero utilizzato per il lock non è consecutivo al lock creato in precedenza, verificare la numerazione.");
            throw new IndexOutOfBoundsException();
        }
        reLock.unlock();
    }

    public static void acquire(int n) {

        lock.get(n).lock();
    }
	
    public static void release(int n) {

        if (lock.get(n)!=null && lock.get(n).isLocked()){

            lock.get(n).unlock();
        } 
    }
}