package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    public LandoMicroservice() {
        super("Lando");
    }

    @Override
    protected void initialize() {
       subscribeBroadcast(TerminateMissionBroadcast.class, c -> {
           Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
           terminate();});
       subscribeEvent(BombDestroyerEvent.class, c -> {
           try{
               Thread.sleep(c.getDuration());
               complete(c, true);
           } catch (InterruptedException e){
               e.printStackTrace();
           }
       });
        Main.countDownLatch.countDown();
    }
}
