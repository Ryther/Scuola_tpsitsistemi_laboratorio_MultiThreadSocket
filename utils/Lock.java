package utils;

import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.List;

public class Lock {
	private static final ReentrantLock reLock = new ReentrantLock();
	private static final List<ReentrantLock> lock = new LinkedList<ReentrantLock>();
	
	public static void acquire(int n)
	{
		reLock.lock();
		if (n >= lock.size()){
			
                    lock.add(n, new ReentrantLock());
                    //Utils.tO("Lock Created " + n);
		}
                
                if (lock.get(n) == null) {
                    lock.set(n, new ReentrantLock());
                    //Utils.tO("Lock Created " + n);
                }
		reLock.unlock();
		
		//Utils.tO("Lock PreAcquired " + n);
		lock.get(n).lock();
		//Utils.tO("Lock Acquired " + n);
	}
	
	public static void release(int n)
	{
            
            if (lock.get(n)!=null && lock.get(n).isLocked()){
			
		lock.get(n).unlock();
                //Utils.tO("Lock Relased " + n);
            } 
            
            if (lock.get(n) == null) {
                
                throw new NullPointerException();
            }
            
            if (!lock.get(n).isLocked()) {
                
                throw new IllegalMonitorStateException();
            }
	}
}