package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.Images;

public class WeaponBullet extends Sprite {

    public boolean isCollision;
    public Crewmate2D.State dir;
    public Vector2 startV;
    public int stackDistance;
    public World world;
    private final static float SPEED = 2.5f;
    private int type;
    public int getType(){return type;}
    public WeaponBullet(World world, PlayScreen screen, Vector2 v, Crewmate2D.State crewmateState, int type){
        super(screen.getLaserAtlas().findRegion("laserRed"));
        isCollision = false;
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        if(type==0){
            setBounds(v.x+2.5f, v.y+4.0f, 17, 17);
            setRegion(new TextureRegion(getTexture(), 80,  3, 37, 37));
        }
        else if(type==1){
            setBounds(v.x+8.5f, v.y+8.0f, 5, 5);
            setRegion(new TextureRegion(getTexture(), 41,  2, 37, 37));
        }
        else if(type==2){
            setBounds(v.x+4.0f, v.y+6.0f, 14, 14);
            setRegion(new TextureRegion(getTexture(), 2,  2, 37, 37));
        }
        else{
            setBounds(v.x+4.0f, v.y+6.0f, 14, 14);
            setRegion(new TextureRegion(getTexture(), 2,  2, 37, 37));
        }

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
        if(type==0)
            return stackDistance > 450;
        else if(type==1)
            return stackDistance > 150;
        else
            return stackDistance > 350;
    }
}
