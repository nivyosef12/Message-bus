package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BombDestroyerEvent implements Event<Boolean> {
    private final long duration;

    public BombDestroyerEvent(long duration){
        this.duration = duration;
    }
    public long getDuration(){
        return duration;
    }
}
