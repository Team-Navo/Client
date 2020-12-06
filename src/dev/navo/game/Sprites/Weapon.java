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
import dev.navo.game.Tools.Images;

public class Weapon extends Sprite {
    public enum Type { NORMAL, RED, BLUE, GREEN , SUPER};
    public World world;

    private Type type;
    public Type getType(){ return type; }

    public Weapon(World world, Vector2 v, Type type){
        super(Images.bulletAtlas.findRegion("NormalBullet"));
        this.type = type;
        this.world = world;
        setBounds(v.x, v.y, 14, 14);
        initRegion();
    }

    private void initRegion(){
        if(Type.RED.equals(type))
            setRegion(new TextureRegion(getTexture(), 80,  16, 37, 37));
        else if(Type.GREEN.equals(type))
            setRegion(new TextureRegion(getTexture(), 2,  15, 37, 37));
        else
            setRegion(new TextureRegion(getTexture(), 41,  15, 37, 37));
    }
}
