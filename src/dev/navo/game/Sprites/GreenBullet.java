package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Screen.PlayScreen;

public class GreenBullet extends Sprite {

    public boolean isCollision;
    public Crewmate.State dir;
    public Vector2 startV;
    public int stackDistance;
    public World world;
    public Body b2Body;
    private final static float SPEED = 4.5f;

    public GreenBullet(World world, PlayScreen screen, Vector2 v, Crewmate.State crewmateState){
        super(screen.getLaserAtlas().findRegion("laserGreen"));
        isCollision = false;
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        setBounds(v.x+4.0f, v.y+6.0f, 14, 14);
        setRegion(new TextureRegion(getTexture(), 2,  2, 37, 37));
    }

    public void update(float dt){
        if(dir.equals(Crewmate.State.UP)) {
            setPosition(this.getX(), this.getY() + SPEED);
        }
        else if(dir.equals(Crewmate.State.DOWN)){
            setPosition(this.getX(), this.getY()-SPEED);
        }
        else if(dir.equals(Crewmate.State.LEFT)){
            setPosition(this.getX()-SPEED, this.getY());
        }
        else if(dir.equals(Crewmate.State.RIGHT)){
            setPosition(this.getX()+SPEED, this.getY());
        }
        stackDistance += 5;
    }
    public boolean check(){
        return stackDistance > 350;
    }
}
