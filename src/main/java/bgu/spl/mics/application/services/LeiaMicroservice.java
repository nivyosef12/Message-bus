package bgu.spl.mics.application.services;
import java.util.LinkedList;
import java.util.List;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private List<Future<Boolean>> futureList;
	long r2D2DeactivationDuration;
	long landoBombDuration;

    public LeiaMicroservice(Attack[] attacks, long r2D2DeactivationDuration, long landoBombDuration) {
        super("Leia");
		this.attacks = attacks;
		futureList = new LinkedList<>();
		this.r2D2DeactivationDuration = r2D2DeactivationDuration;
		this.landoBombDuration = landoBombDuration;
    }
    /** when leia ends senting attaks she waits for the attack to end.. until all future are resolved**/
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateMissionBroadcast.class, c -> {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
            terminate();
        });
        try { // waits for all micro services to initialize before start sending events
            Main.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Attack currAttackToSend : attacks) {
            long duration = currAttackToSend.getDuration();
            List<Integer> serials = currAttackToSend.getSerials();
            serials.sort(Integer::compareTo);
            // serials field in an instance of AttackEvent -  must be sorted!!
            AttackEvent attackEvent = new AttackEvent(serials, duration);
            futureList.add(sendEvent(attackEvent));
            System.out.println("send attack event");
        }
        while (!futureList.isEmpty()) {
            Future<Boolean> future = futureList.get(0);
            future.get(); // if future is not resolved yet then Leia waits until it(future) is resolved
            futureList.remove(0);
        }
        // System.out.println("send finished waiting" );
        DeactivationEvent deactivationEvent = new DeactivationEvent(r2D2DeactivationDuration);
        futureList.add(sendEvent(deactivationEvent));
        System.out.println("deactivate" );
        while (!futureList.isEmpty()) {
            Future<Boolean> future = futureList.get(0);
            future.get(); // if future is not resolved yet then Leia waits until it(future) is resolved
            futureList.remove(0);
        }
        BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent(landoBombDuration);
        futureList.add((sendEvent(bombDestroyerEvent)));
        System.out.println("bomb");
        while (!futureList.isEmpty()) {
            Future<Boolean> future = futureList.get(0);
            future.get(); // if future is not resolved yet then Leia waits until it(future) is resolved
            futureList.remove(0);
        }
        // System.out.println("terminate");
        TerminateMissionBroadcast terminateMissionBroadcast = new TerminateMissionBroadcast();
        sendBroadcast(terminateMissionBroadcast);
    }
}
