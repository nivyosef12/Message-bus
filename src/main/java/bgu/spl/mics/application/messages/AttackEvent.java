package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

import java.util.Iterator;
import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private List<Integer> serials;
    private final long duration;

    public AttackEvent(List<Integer> serials, long duration){
        this.serials = serials;
        this.duration = duration;
    }
    public List<Integer> getSerials(){
        return serials;
    }
    public long getDuration(){
        return duration;
    }
    public Iterator<Integer> getSerialsIterator(){
        return serials.iterator();
        // since serials is sorted, getSerialsIterator() will return an iterator that goes throw the elements in sorted order
    }
}
