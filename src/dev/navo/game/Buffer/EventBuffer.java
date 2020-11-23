package dev.navo.game.Buffer;

import org.json.simple.JSONObject;

public class EventBuffer {
    private JSONObject eventCareData;
    private boolean empty = true;

    private static EventBuffer instance=null;
    public static EventBuffer getInstance() {
        if(instance == null) instance = new EventBuffer();
        return instance;
    }
    public synchronized JSONObject get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        empty = true;
        notifyAll();
        return eventCareData;
    }

    public synchronized void put(JSONObject data) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        empty = false;
        this.eventCareData=data;
        notifyAll();
    }
}
