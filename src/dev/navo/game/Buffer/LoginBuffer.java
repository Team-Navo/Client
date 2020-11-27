package dev.navo.game.Buffer;

public class LoginBuffer extends Buffer {

    private static LoginBuffer instance = null;

    public static LoginBuffer getInstance() {
        if(instance == null)
            instance = new LoginBuffer();
        return instance;
    }
}
