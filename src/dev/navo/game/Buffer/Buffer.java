package dev.navo.game.Buffer;

import org.json.simple.JSONObject;

public class Buffer {
    protected JSONObject data;
    protected boolean empty = true;

    public synchronized JSONObject get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        empty = true;
        notifyAll();
        return data;
    }


    public synchronized void put(JSONObject data) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        empty = false;
        this.data =data;
        notifyAll();
    }
}
