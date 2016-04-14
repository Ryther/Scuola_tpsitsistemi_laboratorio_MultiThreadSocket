package runner;

import java.util.concurrent.locks.ReentrantLock;

public class Lock {
	private static final ReentrantLock reLock = new ReentrantLock();
	private static final ReentrantLock[] lock = new ReentrantLock[Consts.LOCKS];
	
	public static void acquire(int n)
	{
		reLock.lock();
		if (lock[n]==null){
			
			lock[n] = new ReentrantLock();
			//Utils.tO("Lock Created " + n);
			
		}
		reLock.unlock();
		
		//Utils.tO("Lock PreAcquired " + n);
		lock[n].lock();
		//Utils.tO("Lock Acquired " + n);
	}
	
	public static void release(int n)
	{
		lock[n].unlock();
		//Utils.tO("Lock Relased " + n);
	}
}