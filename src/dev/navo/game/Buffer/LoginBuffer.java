package dev.navo.game.Buffer;

import org.json.simple.JSONObject;

public class LoginBuffer {
    private JSONObject loginData;
    private static LoginBuffer instance = null;

    public static LoginBuffer getInstance() {
        if(instance == null)
            instance = new LoginBuffer();
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
        return loginData;
    }

    public synchronized void put(JSONObject data) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = false;
        this.loginData=data;
        notifyAll();
    }
}
