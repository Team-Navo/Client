package dev.navo.game.Sprites;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.Images;

public class BlueBullet extends Sprite {

    public boolean isCollision;
    public Crewmate2D.State dir;
    public Vector2 startV;
    public int stackDistance;
    public World world;
    private final static float SPEED = 5.5f;

    public BlueBullet(World world, PlayScreen screen, Vector2 v, Crewmate2D.State crewmateState){
        super(screen.getLaserAtlas().findRegion("laserBlue"));
        isCollision = false;
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        setBounds(v.x+8.5f, v.y+8.0f, 5, 5);
        setRegion(new TextureRegion(getTexture(), 41,  2, 37, 37));
    }
    public void update(float dt){
        if(dir.equals(Crewmate2D.State.UP)) {
            setPosition(this.getX(), this.getY() + SPEED);
        }
        else if(dir.equals(Crewmate2D.State.DOWN)){
            setPosition(this.getX(), this.getY()-SPEED);
        }
        else if(dir.equals(Crewmate2D.State.LEFT)){
            setPosition(this.getX()-SPEED, this.getY());
        }
        else if(dir.equals(Crewmate2D.State.RIGHT)){
            setPosition(this.getX()+SPEED, this.getY());
        }
        stackDistance += 5;
    }
    public boolean distanceOverCheck(){
        return stackDistance > 150;
    }
}
