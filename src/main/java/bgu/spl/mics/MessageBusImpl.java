package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
	private volatile ConcurrentHashMap <Event ,Future> eventFutureConcurrentHashMap;
	private volatile ConcurrentHashMap <Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventMicroServicesQueueHashMap;
	private volatile ConcurrentHashMap <Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastMicroServicesQueueHashMap;
	private volatile ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceMessageQueueHashMap;

	private static class SingelHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl(){
		eventFutureConcurrentHashMap = new ConcurrentHashMap<>();
		eventMicroServicesQueueHashMap = new ConcurrentHashMap<>();
		broadcastMicroServicesQueueHashMap = new ConcurrentHashMap<>();
		microServiceMessageQueueHashMap = new ConcurrentHashMap<>();
	}
	public static MessageBusImpl getInstance(){
		return SingelHolder.instance;
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventMicroServicesQueueHashMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		eventMicroServicesQueueHashMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastMicroServicesQueueHashMap.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		broadcastMicroServicesQueueHashMap.get(type).add(m);

	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		eventFutureConcurrentHashMap.get(e).resolve(result);
		eventFutureConcurrentHashMap.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(broadcastMicroServicesQueueHashMap.get(b.getClass()) != null && !broadcastMicroServicesQueueHashMap.get(b.getClass()).isEmpty()){
			for(MicroService microServiceToGetBroadcast : broadcastMicroServicesQueueHashMap.get(b.getClass())) {
				// each micro service that has subscribed to b.getClass() type of broadcast will get broadcast b to its message queue
				microServiceMessageQueueHashMap.get(microServiceToGetBroadcast).add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// iff a micro service has subscribed to this type (e.getClass()) of message a future object will be created and returned to the sender
		if((eventMicroServicesQueueHashMap.get(e.getClass()) != null) && !(eventMicroServicesQueueHashMap.get(e.getClass()).isEmpty())) {
			// lock LinkedQueue of micro services that registered to handle events of type e.getClass()
			synchronized (eventMicroServicesQueueHashMap.get(e.getClass())) {
				Future<T> future = new Future<>(); // future to be sent to the sender of Event e
				eventFutureConcurrentHashMap.put(e, future); // restore future in order to resolve it when Event e has processed
				MicroService microServiceToGetEvent = eventMicroServicesQueueHashMap.get(e.getClass()).poll();
				if(microServiceToGetEvent == null)
					return null;
				if (microServiceMessageQueueHashMap.get(microServiceToGetEvent) == null)
					register(microServiceToGetEvent);
				// lock LinkedQueue of micro services that registered to handle events of type e.getClass()
				synchronized (microServiceMessageQueueHashMap.get(microServiceToGetEvent)) {
					microServiceMessageQueueHashMap.get(microServiceToGetEvent).add(e); // add Event e to message queue of microServiceToGetEvent
					eventMicroServicesQueueHashMap.get(e.getClass()).add(microServiceToGetEvent); // assigning events in a Round Robin manner
					return future;
				}
			}
		}
		else
			return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceMessageQueueHashMap.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		// for each Event instance in eventMicroServicesHashMap.keySet(), remove m from queue if m is in there
		for(Class<? extends Event> event_Class: eventMicroServicesQueueHashMap.keySet()){
			//if(event_Class != null) {
				eventMicroServicesQueueHashMap.get(event_Class).remove(m);
			//}
		}
		// for each Broadcast instance in broadcastMicroServicesHashMap.keySet(), remove m from queue if m is in there
		for(Class<? extends Broadcast> broadcast_Class: broadcastMicroServicesQueueHashMap.keySet()){
			//if(broadcast_Class != null) {
				broadcastMicroServicesQueueHashMap.get(broadcast_Class).remove(m);
			//}
		}
		// removes the message queue allocated for m
		synchronized (microServiceMessageQueueHashMap) {
			microServiceMessageQueueHashMap.remove(m);
		}
	}
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microServiceMessageQueueHashMap.get(m).take();
	}
}
