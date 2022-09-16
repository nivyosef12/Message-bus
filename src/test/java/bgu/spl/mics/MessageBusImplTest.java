package bgu.spl.mics;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.testBroadcast;
import bgu.spl.mics.application.messages.testEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.services.testMicroService;
import bgu.spl.mics.application.messages.TerminateMissionBroadcast;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBusImpl messageBus;
    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetInstance() {
        assertEquals(messageBus,MessageBusImpl.getInstance());
    }
    @Test
    void testComplete()  {
        testEvent event = new testEvent("test");
        MicroService m = new testMicroService();
        messageBus.register(m);
        messageBus.subscribeEvent(event.getClass(),m);
        Future<String> future = messageBus.sendEvent(event);
        assertNotEquals(future.get(100, TimeUnit.MILLISECONDS), "test");
        while(true) {
            try {
                Event<String> eventReceived = (Event<String>)messageBus.awaitMessage(m);
                messageBus.complete(eventReceived, "completed");
                break;
            } catch (InterruptedException e) {
                // keep waiting foe messages
            }
        }
        assertEquals(future.get(), "completed");
        messageBus.unregister(m);
    }

    @Test
    void testSendBroadcast() {
        // tests register(MicroService m) and subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) too
        testBroadcast broadcast = new testBroadcast();
        MicroService m = new testMicroService();
        messageBus.register(m);
        messageBus.subscribeBroadcast(broadcast.getClass(), m);
        messageBus.sendBroadcast(broadcast);
        while (true) {
            try {
                assertEquals(broadcast, messageBus.awaitMessage(m));
                break;
            } catch (InterruptedException e){
                // keep waiting foe messages
            }
        }
        messageBus.unregister(m);
    }

    @Test
    void testSendEvent() {
        // tests subscribeEvent(Class<? extends Event<T>> type, MicroService m) too
        testEvent event = new testEvent("test");
        MicroService m = new testMicroService();
        messageBus.register(m);
        messageBus.subscribeEvent(event.getClass(), m);
        messageBus.sendEvent(event);
        while(true) {
            try {
                 assertEquals(event.getStr(), ((testEvent)messageBus.awaitMessage(m)).getStr());
                break;
            } catch (InterruptedException e) {
                // keep waiting foe messages
            }
        }
        messageBus.unregister(m);
    }
    @Test
    void testAwaitMessage() {
        testEvent event = new testEvent("test");
        MicroService m = new testMicroService();
        messageBus.register(m);
        messageBus.subscribeEvent(event.getClass(), m);
        messageBus.sendEvent(event);
        while(true) {
            try {
                assertEquals(event.getStr(), ((testEvent)messageBus.awaitMessage(m)).getStr()); // check if the event that has been sent is equals to the message that m2 received
                break;
            } catch (InterruptedException e) {
                // keep waiting foe messages
            }
        }
        messageBus.unregister(m);
    }
}