package dev.navo.game.Buffer;

import org.json.simple.JSONObject;

public class InGameBuffer {
    private JSONObject inGameData;
    private static InGameBuffer instance = null;

    public static InGameBuffer getInstance() {
        if(instance == null)
            instance = new InGameBuffer();
        return instance;
    }

    private boolean empty = true;
    public synchronized JSONObject get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = true;
        notifyAll();
        return inGameData;
    }

    public synchronized void put(JSONObject data) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = false;
        this.inGameData=data;
        notifyAll();
    }
}
