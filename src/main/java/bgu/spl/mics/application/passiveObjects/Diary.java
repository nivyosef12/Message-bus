package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private  long HanSoloTerminate;
    private  long C3POTerminate;
    private  long LeiaTerminate;
    private  long R2D2Terminate;
    private  long LandoTerminate;
    private static class SingleHolder {
        private static Diary instance = new Diary();
    }
    private Diary(){
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0; HanSoloTerminate = 0;
        C3POFinish = 0; C3POTerminate = 0;
        R2D2Deactivate = 0; R2D2Terminate = 0;
        LeiaTerminate = 0;
        LandoTerminate = 0;
    }
     //Retrieves the single instance of this class.
    public static Diary getInstance() {
        return SingleHolder.instance;
    }
    public AtomicInteger getNumberOfAttacks(){
        return totalAttacks;
    }
    public void resetNumberAttacks(){
        totalAttacks = new AtomicInteger(0);
    }
    public void setHanSoloFinish(long finishTime){
        HanSoloFinish = finishTime;
    }
    public void setC3POFinish(long finishTime){
        C3POFinish = finishTime;
    }
    public void setR2D2Deactivate(long deactivateTime){
        R2D2Deactivate = deactivateTime;
    }
    public void setHanSoloTerminate(long hanSoloTerminateTime){
        HanSoloTerminate = hanSoloTerminateTime;
    }
    public void setC3POTerminate(long C3POTerminateTime){
        C3POTerminate = C3POTerminateTime;
    }
    public void setLeiaTerminate(long leiaTerminateTime){
        LeiaTerminate = leiaTerminateTime;
    }
    public void setR2D2Terminate(long R2D2TerminateTime){
        R2D2Terminate = R2D2TerminateTime;
    }
    public void setLandoTerminate(long landoTerminateTime){
        LandoTerminate = landoTerminateTime;
    }
    public void incrementTotalAttacks(){
        int oldTotalAttacks;
        int newTotalAttacks;
        do {
            oldTotalAttacks = totalAttacks.intValue();
            newTotalAttacks = oldTotalAttacks + 1;
        }   while (!totalAttacks.compareAndSet(oldTotalAttacks, newTotalAttacks));
    }
    public int getTotalAttacks(){
        return totalAttacks.get();
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }
}
