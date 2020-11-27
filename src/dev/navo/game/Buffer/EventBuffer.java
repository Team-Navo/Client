package dev.navo.game.Buffer;

public class EventBuffer extends Buffer{
    private static EventBuffer instance=null;
    public static EventBuffer getInstance() {
        if(instance == null) instance = new EventBuffer();
        return instance;
    }

}
