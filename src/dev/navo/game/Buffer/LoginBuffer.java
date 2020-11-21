package dev.navo.game.Buffer;

public class LoginBuffer {
    private String loginData;
    private static LoginBuffer instance=null;
    public static LoginBuffer getInstance() {
        if(instance==null) instance=new LoginBuffer();
        return instance;
    }
    private boolean empty = true;
    public synchronized String get() {
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
    public synchronized void put(String data) {
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
