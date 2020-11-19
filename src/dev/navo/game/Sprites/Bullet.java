package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Sprites.Character.Crewmate2D;

public class Bullet extends Sprite {

    public World world;

    public boolean isCollision;//충돌 했는지
    public Crewmate2D.State dir; // 방향
    public Vector2 startV; // 출발 지점
    public float stackDistance; // 이동한 거리
    private final static float SPEED = 250f; // 이동 속도
    private final static int RANGE = 150; // 총알 사거리

    public Bullet(World world, PlayScreen screen, Vector2 v, Crewmate2D.State crewmateState){
        super(screen.getAtlas().findRegion("Bullet"));
        isCollision = false;
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        setBounds(v.x+6.5f, v.y+7.25f, 9, 9);
        setRegion(new TextureRegion(getTexture(), 1,  1, 9, 9));
    }

    // 총알 이동
    public void update(float dt){
        if(dir.equals(Crewmate2D.State.UP)) {
            setPosition(this.getX(), this.getY() + SPEED * dt);
        }
        else if(dir.equals(Crewmate2D.State.DOWN)){
            setPosition(this.getX(), this.getY()-SPEED * dt);
        }
        else if(dir.equals(Crewmate2D.State.LEFT)){
            setPosition(this.getX()-SPEED * dt, this.getY());
        }
        else if(dir.equals(Crewmate2D.State.RIGHT)){
            setPosition(this.getX()+SPEED * dt, this.getY());
        }
        stackDistance += SPEED * dt;
    }

    public boolean distanceOverCheck(){
        return stackDistance > RANGE;
    } // 거리가 RANGE 이상 넘어가면 삭제

}
