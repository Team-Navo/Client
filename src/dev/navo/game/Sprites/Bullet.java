package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Sounds;

public class Bullet extends Sprite {

    protected World world;

    protected Crewmate2D.State dir; // 방향
    protected Vector2 startV; // 출발 지점
    protected float stackDistance; // 이동한 거리
    protected float speed = 250f; // 이동 속도
    protected int range = 150; // 총알 사거리
    protected Weapon.Type type;

    private String owner;
    public String getOwner(){
        return owner;
    }

    public Bullet(World world, Vector2 v, Crewmate2D.State crewmateState, Weapon.Type type, String owner, float sound){
        super(Images.bulletAtlas.findRegion("NormalBullet"));
        this.world = world;
        startV = v;
        dir = crewmateState;
        stackDistance = 0;
        this.type = type;
        this.owner = owner;
        if(sound > 0) Sounds.gunShotSound.play(sound * 0.7f);
        initFeature();
        initRegion();
    }
    public Weapon.Type getType(){return type;}

    private void initRegion(){
        if(type.equals(Weapon.Type.NORMAL)){
            setRegion(new TextureRegion(getTexture(), 2,  2, 11, 11));
            setBounds(startV.x+6.5f, startV.y+7.25f, 9, 9);
        }
        else if(type.equals(Weapon.Type.RED)){
            setRegion(new TextureRegion(getTexture(), 80,  16, 37, 37));
            setBounds(startV.x+2.5f, startV.y+4.0f, 17, 17);
        }
        else if(type.equals(Weapon.Type.BLUE)){
            setRegion(new TextureRegion(getTexture(), 41,  15, 37, 37));
            setBounds(startV.x+8.5f, startV.y+8.0f, 5, 5);
        }
        else if(type.equals(Weapon.Type.GREEN)||type.equals(Weapon.Type.SUPER)){
            setRegion(new TextureRegion(getTexture(), 2,  15, 37, 37));
            setBounds(startV.x+4.0f, startV.y+6.0f, 14, 14);
        }else{
            setRegion(new TextureRegion(getTexture(), 2,  2, 11, 11));
            setBounds(startV.x+6.5f, startV.y+7.25f, 9, 9);
        }
    }

    public void initFeature(){
        if(type.equals(Weapon.Type.RED)) {
            this.speed = 200;
            this.range = 200;
        }
        else if(type.equals(Weapon.Type.BLUE)){
            this.speed = 350;
            this.range = 100;
        }
        else if(type.equals(Weapon.Type.GREEN)){
            this.speed = 300;
            this.range = 250;
        }
        else{
            this.speed = 250;
            this.range = 150;
        }
    }

    // 총알 이동
    public boolean update(float dt){
        float frameDistance = speed * dt;
        if(dir.equals(Crewmate2D.State.UP)) {
            setPosition(this.getX(), this.getY() + frameDistance);
        }
        else if(dir.equals(Crewmate2D.State.DOWN)){
            setPosition(this.getX(), this.getY()- frameDistance);
        }
        else if(dir.equals(Crewmate2D.State.LEFT)){
            setPosition(this.getX()- frameDistance, this.getY());
        }
        else if(dir.equals(Crewmate2D.State.RIGHT)){
            setPosition(this.getX()+ frameDistance, this.getY());
        }
        stackDistance += frameDistance;
        return stackDistance > range; // 사거리 넘어가면 TRUE
    }
}


