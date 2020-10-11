package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Screen.PlayScreen;

public class Crewmate  extends Sprite {
    public enum State { UP, DOWN, LEFT, RIGHT };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2Body;
    private TextureRegion crewmateFrontStand;
    private Animation crewmateFront;
    private Animation crewmateBack;
    private Animation crewmateLeft;
    private Animation crewmateRight;

    private boolean isStop;
    private float stateTimer;
    private final static float frameDuration = (float) 0.2;
    public float getStateTimer(){
        return stateTimer;
    }

    public Crewmate(World world, PlayScreen screen, Vector2 v){
        super(screen.getAtlas().findRegion("CrewmateMove"));
        this.world = world;
        currentState = State.DOWN;
        previousState = State.DOWN;
        stateTimer = 0;
        Array<TextureRegion> frames = new Array<>();
        //Down animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i, 50, 20, 25));
        crewmateFront = new Animation(frameDuration, frames);
        frames.clear();

        //Up animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i, 75, 20, 25));
        crewmateBack = new Animation(frameDuration, frames);
        frames.clear();

        //Left animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i, 25, 20, 25));
        crewmateLeft = new Animation(frameDuration, frames);
        frames.clear();

        //Right animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i, 0, 20, 25));
        crewmateRight = new Animation(frameDuration, frames);
        frames.clear();

        defineCrewmate(v);
        crewmateFrontStand = new TextureRegion(getTexture(), 0, 50, 20, 25);
        setBounds(200-11, 500-12, 20, 25);
        setRegion(crewmateFrontStand);
    }

    public void defineCrewmate(Vector2 v){
        BodyDef bDef = new BodyDef();
        bDef.position.set(v.x,v.y);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8.5f, 11);

        fDef.shape = shape;
        b2Body.createFixture(fDef);
    }

    public void update(float dt){
        setPosition(b2Body.getPosition().x - getWidth() /2 -1, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        if(isStop){
            previousState = currentState;
            stateTimer = 0;
            return crewmateFrontStand;
        }

        TextureRegion region;
        switch (currentState){
            case UP:
                region = (TextureRegion)crewmateBack.getKeyFrame(stateTimer);
                break;
            case DOWN:
                region = (TextureRegion)crewmateFront.getKeyFrame(stateTimer);
                break;
            case RIGHT:
                region = (TextureRegion)crewmateRight.getKeyFrame(stateTimer);
                break;
            case LEFT:
                region = (TextureRegion)crewmateLeft.getKeyFrame(stateTimer);
                break;
            default:
                region = crewmateFrontStand;
                break;
        }
        if(stateTimer >= frameDuration*3)
            stateTimer = 0;

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        isStop = false;
        if(b2Body.getLinearVelocity().x > 0)
            return State.RIGHT;
        else if(b2Body.getLinearVelocity().x < 0)
            return State.LEFT;
        else if(b2Body.getLinearVelocity().y > 0)
            return State.UP;
        else if(b2Body.getLinearVelocity().y < 0)
            return State.DOWN;
        else {
            isStop = true;
            return State.DOWN;
        }
    }
}
