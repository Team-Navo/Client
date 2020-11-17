package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Screen.PlayScreen;

public class Bullet extends Sprite {

    public boolean isCollision;
    public Crewmate2D.State dir;
    public Vector2 startV;
    public int stackDistance;
    public World world;
    public Body b2Body;
    private final static float SPEED = 3.5f;


    public Bullet(World world, PlayScreen screen, Vector2 v, Crewmate2D.State crewmateState){
        super(screen.getAtlas().findRegion("Bullet"));
        isCollision = false;
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        defineBullet(v);
        setBounds(v.x+6.5f, v.y+7.25f, 9, 9);
        setRegion(new TextureRegion(getTexture(), 1,  1, 9, 9));
    }

    public void defineBullet(Vector2 v){
//        BodyDef bDef = new BodyDef();
//        bDef.position.set(v.x,v.y);
//        bDef.type = BodyDef.BodyType.KinematicBody;
//        b2Body = world.createBody(bDef);
//        b2Body.setBullet(true);
//        FixtureDef fDef = new FixtureDef();
//       PolygonShape shape = new PolygonShape();
//        shape.setRadius(5f);
//
//        fDef.shape = shape;
//        b2Body.createFixture(fDef);
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
//        setPosition(b2Body.getPosition().x - getWidth() /2, b2Body.getPosition().y - getHeight() / 2);
//        setRegion(getFrame(dt));
    }
    public boolean distanceOverCheck(){
        return stackDistance > 250;
    }

}
