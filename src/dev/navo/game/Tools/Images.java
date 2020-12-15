package dev.navo.game.Tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.navo.game.NavoGame;

public class Images {
    public static final Texture background = new Texture("data/GameBack.png"); // 배경 이미지 초기화

    public static final Texture minimap = new Texture("back/minimap.png"); // 미니맵

    public static final Texture keys = new Texture("keys.png"); // 미니맵

    public static final Texture crewmate = new Texture("Image.png");

    public static final TextureAtlas mainAtlas = new TextureAtlas("Image.atlas");
    public static final TextureAtlas itemAtlas = new TextureAtlas("Item.atlas");
    public static final TextureAtlas laserAtlas = new TextureAtlas("laser.atlas");
    public static final TextureAtlas effectAtlas=new TextureAtlas("effect.atlas");
    public static final TextureAtlas bulletAtlas = new TextureAtlas("bullet.atlas");

    private static float[] backgroundOffsets = {0, 0, 0, 0};

    //BLUE GRAY GREEN PURPLE RED
    public static final TextureRegion[] header = {
            new TextureRegion(crewmate, 1, 25+12, 20, 25),
            new TextureRegion(crewmate, 91, 25+12, 20, 25),
            new TextureRegion(crewmate, 181, 25+12, 20, 25),
            new TextureRegion(crewmate, 271, 25+12, 20, 25),
            new TextureRegion(crewmate, 361, 25+12, 20, 25)
    };

    public static final TextureRegion[] key = {
            new TextureRegion(keys, 0, 0, 72, 72), // UP
            new TextureRegion(keys, 72, 0, 72, 72), // DOWN
            new TextureRegion(keys, 144, 0, 72, 72), // RIGHT
            new TextureRegion(keys, 216, 0, 72, 72), // LEFT
            new TextureRegion(keys, 288, 0, 72, 72), // Z
            new TextureRegion(keys, 360, 0, 72, 72), // X
            new TextureRegion(keys, 432, 0, 72, 72), // V
            new TextureRegion(keys, 504, 0, 72, 72) // M
    };

    private static Texture[] backgrounds = {
            new Texture("back/Starscape00.png"),
            new Texture("back/Starscape01.png"),
            new Texture("back/Starscape02.png"),
            new Texture("back/Starscape03.png")
    };

    public static void renderBackground(float delta, SpriteBatch batch) {
        float backgroundMaxScrollingSpeed = (float)(700) / 4;

        //update position of background images
        backgroundOffsets[0] += delta * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[1] += delta * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += delta * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += delta * backgroundMaxScrollingSpeed;

        //draw each background layer
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > 700) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer],
                    0,
                    -backgroundOffsets[layer],
                    800, 700);
            batch.draw(backgrounds[layer],
                    0,
                    -backgroundOffsets[layer] + 700,
                    800, 700);
        }
    }
}
