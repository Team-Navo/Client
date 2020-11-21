package dev.navo.game.Buffer;

public class EventBuffer {
    private String eventCareData;
    private boolean empty = true;

    private static EventBuffer instance=null;
    public static EventBuffer getInstance() {
        if(instance==null) instance=new EventBuffer();
        return instance;
    }
    public synchronized String get() {
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

    public synchronized void put(String data) {
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
