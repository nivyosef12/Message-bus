package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	private int serialNumber;
	boolean available;
	//singelton
    public Ewok(int serialNumber){
        this.serialNumber = serialNumber;
        available = true;
    }

    /**
     * Acquires an Ewok
     */
    public synchronized void acquire() {
        while (!isAvailable()){
            try{ // if a thread is trying to acquire a occupied ewok
                this.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
		available = false;
    }

    /**
     * release an Ewok
     */
    public synchronized void release() {
    	available = true;
    	notifyAll(); //notify all threads that are blocked (by trying to acquire a occupied ewok) that this ewok can no be acquired
    }
    public boolean isAvailable(){
        return available;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
}
