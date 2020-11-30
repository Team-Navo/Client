package dev.navo.game.Sprites.Character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Screen.PlayScreen;

public class Cat extends Sprite {

    public enum State { UP, DOWN, LEFT, RIGHT };
    public Cat.State currentState;
    public Cat.State previousState;

    public World world;
    public Body b2Body;
    private TextureRegion catFrontStand;
    private Animation catFront;
    private Animation catBack;
    private Animation catLeft;
    private Animation catRight;

    private boolean isStop;
    private float stateTimer;
    private final static float frameDuration = (float) 0.15;

    public float getStateTimer(){
        return stateTimer;
    }

    public Cat(World world, PlayScreen screen){
//        super(screen.getAtlas().findRegion("CatFront"));
        this.world = world;
        currentState = Cat.State.DOWN;
        previousState = Cat.State.DOWN;
        stateTimer = 0;
        Array<TextureRegion> frames = new Array<>();

        //Down animation create
        for(int i = 1 ; i < 5 ; i++)
            frames.add(new TextureRegion(getTexture(), 45*i, 0, 45, 60));
        catFront = new Animation(frameDuration, frames);
        frames.clear();

        defineCat();
        catFrontStand = new TextureRegion(getTexture(), 0, 0, 45, 60);
        setBounds(250-10.5f, 1170-14, 21, 28);
        setRegion(catFrontStand);
    }

    public void defineCat(){
        BodyDef bDef = new BodyDef();
        bDef.position.set(250,1170);
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox( 10.5f, 14);

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
            return catFrontStand;
        }

        TextureRegion region;
        switch (currentState){
//            case UP:
//                region = (TextureRegion)catBack.getKeyFrame(stateTimer);
//                break;
            case DOWN:
                region = (TextureRegion)catFront.getKeyFrame(stateTimer);
                break;
//            case RIGHT:
//                region = (TextureRegion)catRight.getKeyFrame(stateTimer);
//                break;
//            case LEFT:
//                region = (TextureRegion)catLeft.getKeyFrame(stateTimer);
//                break;
            default:
                region = catFrontStand;
                break;
        }
        if(stateTimer >= frameDuration*4)
            stateTimer = 0;

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public Cat.State getState(){
        isStop = false;
        if(b2Body.getLinearVelocity().x > 0)
            return Cat.State.RIGHT;
        else if(b2Body.getLinearVelocity().x < 0)
            return Cat.State.LEFT;
        else if(b2Body.getLinearVelocity().y > 0)
            return Cat.State.UP;
        else if(b2Body.getLinearVelocity().y < 0)
            return Cat.State.DOWN;
        else {
            isStop = true;
            return Cat.State.DOWN;
        }
    }
}
