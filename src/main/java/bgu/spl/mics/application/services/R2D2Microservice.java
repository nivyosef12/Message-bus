package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    public R2D2Microservice() {
        super("R2D2");
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateMissionBroadcast.class, c -> {
            Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
            terminate();});
        subscribeEvent(DeactivationEvent.class, c -> {
            try{
                Thread.sleep(c.getDuration());
                complete(c, true);
                Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        Main.countDownLatch.countDown();
    }
}
