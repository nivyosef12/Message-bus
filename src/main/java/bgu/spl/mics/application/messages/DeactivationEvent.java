package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeactivationEvent implements Event<Boolean> {
    private final long duration;

    public DeactivationEvent(long duration){
        this.duration = duration;
    }
    public long getDuration(){
        return duration;
    }
}
