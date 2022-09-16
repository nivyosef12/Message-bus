package bgu.spl.mics.application.services;
import java.util.Iterator;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
	
    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateMissionBroadcast.class, c -> {
            Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
            terminate();});
        subscribeEvent(AttackEvent.class, c -> {
            Iterator<Integer> serialsIterator = c.getSerialsIterator();
            // getSerialsIterator() passes in sorted order in order to avoid deadlocks --> @INV: AttackEvent.serials is a sorted list
            while (serialsIterator.hasNext()){
                Integer currSerial = serialsIterator.next();
                Ewoks.getInstance().getEwoks().get(currSerial - 1).acquire();
                // acquires resources for the attack, if there is an ewok that is already acquired -->
                // --> method acquire() will block han solo by wait()
            }
            try{
                System.out.println("C3PO attacks");
                Thread.sleep(c.getDuration());
                System.out.println("C3PO finished");
                complete(c, true);
                Diary.getInstance().setC3POFinish(System.currentTimeMillis()); // overrides each time stamp until the leas one is executing
                Diary.getInstance().incrementTotalAttacks();
                serialsIterator = c.getSerialsIterator();
                while (serialsIterator.hasNext()){
                    Integer currSerialToRelease = serialsIterator.next();
                    Ewoks.getInstance().getEwoks().get(currSerialToRelease - 1).release();
                    // release() notify all threads that are blocked (by trying to acquire a occupied ewok) that this ewok can now be acquired
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        Main.countDownLatch.countDown();
    }
}
