package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Tools.FontGenerator;

public class TrapItem  extends Sprite {

    private final static Vector2 regionV = new Vector2(1, 12);

    public World world;


    public TrapItem(World world, PlayScreen screen, Vector2 v){
        super(screen.getItemAtlas().findRegion("pill_yellow"));
        this.world = world;
        setBounds(v.x, v.y, 22, 21);
        setRegion(new TextureRegion(getTexture(), 50, 2, 22 ,21));
    }

    public void update(float dt){
        setPosition(this.getX(), this.getY());
    }
}
