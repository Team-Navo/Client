package dev.navo.game.Buffer;

public class InGameBuffer {
    private String inGameData;
    private static InGameBuffer instance=null;
    public static InGameBuffer getInstance() {
        if(instance==null) instance=new InGameBuffer();
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
        return inGameData;
    }
    public synchronized void put(String data) {
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
