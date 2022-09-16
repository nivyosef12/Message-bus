package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private List<Ewok> ewoks;

    private static class SingleHolder {
        private static Ewoks instance = new Ewoks();
    }
    private Ewoks(){
        ewoks = new ArrayList<>();
    }
    public static Ewoks getInstance() {
        return SingleHolder.instance;
    }
    public void addEwok(Ewok ewokToAdd){
        ewoks.add(ewokToAdd);
    }
    public List<Ewok> getEwoks(){
        return ewoks;
    }
}
