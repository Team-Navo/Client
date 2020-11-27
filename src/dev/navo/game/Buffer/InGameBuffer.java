package dev.navo.game.Buffer;

import dev.navo.game.Sprites.Bullet;
import org.json.simple.JSONObject;

public class InGameBuffer extends Buffer {
    private static InGameBuffer instance=null;
    public static InGameBuffer getInstance() {
        if(instance == null) instance = new InGameBuffer();
        return instance;
    }

}
