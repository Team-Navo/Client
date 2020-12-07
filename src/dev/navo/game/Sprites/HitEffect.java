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

public class HitEffect extends Sprite {
    private Animation hitEffect;
    public World world;
    private float stateTimer = 0;
    private final static float frameDuration = (float) 0.075;
    public float getStateTimer(){
        return stateTimer;
    }
    public float getFrameDuration(){return frameDuration; }
    public HitEffect(World world, Vector2 v){
        super(Images.effectAtlas.findRegion("ef1"));
        this.world = world;
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(getTexture(), 2,  2004, 2500, 2000));
        frames.add(new TextureRegion(getTexture(), 2,  2, 2500, 2000));
        frames.add(new TextureRegion(getTexture(), 2504,  2004, 2500, 2000));
        frames.add(new TextureRegion(getTexture(), 2504,  2, 2500, 2000));

        hitEffect = new Animation(frameDuration, frames);
        setBounds(v.x-3, v.y, 30, 30);
    }
    public boolean update(float dt){
        setPosition(this.getX(), this.getY());
        setRegion(getFrame(dt));

        return getStateTimer() >= getFrameDuration()*4;
    }
    public TextureRegion getFrame(float dt){
        TextureRegion region;
        region = (TextureRegion)hitEffect.getKeyFrame(stateTimer);
        if(stateTimer >= frameDuration * 4)
            stateTimer = 0;
        stateTimer = stateTimer + dt;
        return region;
    }
}