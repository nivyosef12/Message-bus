package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class testEvent implements Event<String> {
    private String str;

    public testEvent(String str){
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
