package dev.navo.game.Sprites.Character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Sprites.Weapon;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Sounds;
import org.json.simple.JSONObject;

public class Crewmate2D extends Sprite{

    public World world;
    public Body b2Body;

    //Image and animation
    private TextureRegion crewmateFrontStand;
    private TextureRegion crewmateBackStand;
    private TextureRegion crewmateRightStand;
    private TextureRegion crewmateLeftStand;
    private Animation crewmateFront;
    private Animation crewmateBack;
    private Animation crewmateLeft;
    private Animation crewmateRight;

    //캐릭터 정보
    public String owner;
    private String color;
    private String name;
    private Label nameLabel;
    private float maxHP;
    private float HP;
    private Weapon.Type weapon; //추가
    private int bulletQuantity; //추가
    private int weaponStack = 0; //추가
    private boolean isShoot = false; //추가

    private float stepDelay;
    private float attackDelay;
    private boolean isStop;
    private float stateTimer;

    private float drmX = 0;
    private float drmY = 0;

    private int maxSpeed;

    public int getMaxSpeed() {
        return maxSpeed;
    }
    public void setMaxSpeed(int maxSpeed) {
        Sounds.speed.play();
        this.maxSpeed = maxSpeed;
        if(this.maxSpeed<30) this.maxSpeed = 30;
    }
    public void setHpSpeed(int maxSpeed){
        this.maxSpeed = maxSpeed;
        if(this.maxSpeed<30) this.maxSpeed = 30;
        this.heal(); this.heal();
    }

    public enum State { UP, DOWN, LEFT, RIGHT };
    public State currentState;
    public State previousState;

    private final static float frameDuration = (float) 0.2;

    //Getter
    public float getAttackDelay(){
        return attackDelay;
    }
    public float getMaxHP() { return maxHP;}
    public float getHP() { return HP;}
    public Label getLabel(){
        return nameLabel;
    }
    public boolean getisShoot(){return isShoot;}    //추가
    public int getWeaponStack(){return weaponStack;} //추가
    public Weapon.Type getWeapon(){return weapon;}; //추가
    public String getMyColor(){return color;} //추가
    public int getBulletQuantity(){return bulletQuantity;} //추가

    //Setter
    public void setColor(String color) {
        this.color = color;
        initFrame();
    }
    public void hit(int damage) {
        if(this.HP - damage <= 0) this.HP = 0;
        else this.HP -= damage;
        Sounds.hit.play();
    }

    public void heal(){
        Sounds.hp.play();
        if(HP + 10 >= this.maxHP) this.HP = this.maxHP;
        else this.HP += 10;
    }
    public void setWorld(World world){
        this.world = world;
        defineCrewmate(new Vector2(this.getX(), this.getY()));
    }

    public void shooting(){//추가
        this.isShoot = true;
        if(!weapon.equals(Weapon.Type.NORMAL)){
            if( --this.bulletQuantity <= 0 )
               weapon = Weapon.Type.NORMAL;
        }
        setAttackDelay();
    }
    public void setWeapon(Weapon.Type weapon){ //추가
        this.weapon = weapon;
        Sounds.getGun.play();
        this.weaponStack++;

        if(this.weapon.equals(Weapon.Type.RED)){
            if(color.equals("Purple")) bulletQuantity = 25;
            else bulletQuantity = 15;
        }
        else {
            if(color.equals("Purple")) bulletQuantity = 60;
            else bulletQuantity = 40;
        }
    }

    public void setAttackDelay(){
        if(this.weapon.equals(Weapon.Type.RED)) attackDelay = 0.5f;
        else if(this.weapon.equals(Weapon.Type.BLUE)) attackDelay = 0.1f;
        else if(this.weapon.equals(Weapon.Type.GREEN) || weapon.equals(Weapon.Type.SUPER)) attackDelay = 0.2f;
        else if(color.equals("Blue")) attackDelay = 0.2f;
        else attackDelay = 0.4f;

    }
    public void setBulletQuantity(int bullet){
        this.bulletQuantity = bullet;
    }


    //생성자
    public Crewmate2D(World world, TextureAtlas atlas, Vector2 v, String owner, String name) {
        super(atlas.findRegion("Blue"));
        this.world = world;

        this.owner = owner;
        this.name = name;
        this.color = "Blue";
        colorSetting();
        this.maxHP = 100;
        this.HP = 100;
        this.weapon = Weapon.Type.NORMAL;

        nameLabel = new Label(name, new Label.LabelStyle(FontGenerator.font32, Color.BLUE));
        nameLabel.setWidth(100);
        nameLabel.setHeight(30);
        nameLabel.setFontScale(0.5f);
        nameLabel.setAlignment(Align.center);

        currentState = State.DOWN;
        previousState = State.DOWN;
        stateTimer = 0;
        attackDelay = 0f;
        initFrame();
        maxSpeed = 50;

        setBounds(v.x, v.y, 20, 25);
        setRegion(crewmateFrontStand);
    }
    //추가
    public void colorSetting(){
        if(color.equals("Red")){ //빨간 캐릭터 능력치
            maxHP = 150;
            HP = 150;
            maxSpeed = 70;
        }
        if(color.equals("Green")){ //초록 캐릭터 능력치
            maxSpeed = 100;
        }
    }
    //캐릭터 움직임 프레임 초기화
    public void initFrame(){
        int regionX = getImageStartX(color);
        Array<TextureRegion> frames = new Array<>();
        //Down animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX,  50+12, 20, 25));
        crewmateFront = new Animation(frameDuration, frames);
        frames.clear();

        //Up animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 75+12, 20, 25));
        crewmateBack = new Animation(frameDuration, frames);
        frames.clear();

        //Left animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 25+12, 20, 25));
        crewmateLeft = new Animation(frameDuration, frames);
        frames.clear();

        //Right animation create
        for(int i = 1 ; i < 4 ; i++)
            frames.add(new TextureRegion(getTexture(), 22*i+regionX, 12, 20, 25));
        crewmateRight = new Animation(frameDuration, frames);
        frames.clear();

        crewmateFrontStand = new TextureRegion(getTexture(), regionX, 50+12, 20, 25);
        crewmateBackStand = new TextureRegion(getTexture(), regionX, 75+12, 20, 25);
        crewmateLeftStand = new TextureRegion(getTexture(), regionX, 25+12, 20, 25);
        crewmateRightStand = new TextureRegion(getTexture(), regionX, 12, 20, 25);
    }

    //색별로 Atlas에서 어디 부분 가져올지 정하는 코드
    private int getImageStartX(String color) {
        switch (color) {
            case "Blue":
                return 1;
            case "Gray":
                return 91;
            case "Green":
                return 181;
            case "Purple":
                return 271;
            default: // Red
                return 361;
        }
    }

    // 2D 엔진 적용된 바디 생성
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

    // 업데이트
    public void update(float dt){
        if(attackDelay > 0) attackDelay -= dt;
        if(stepDelay > 0) stepDelay -= dt;
        drmX = b2Body.getLinearVelocity().x * dt; // 1초당 80만큼 움직임 = velocity.X = 80 * dt = 1 Frame 당 움직일 X 거리
        drmY = b2Body.getLinearVelocity().y * dt;// 1초당 80만큼 움직임 = velocity.Y = 80 * dt = 1 Frame 당 움직일 Y 거리
        setPosition(b2Body.getPosition().x - getWidth() /2 -1, b2Body.getPosition().y - getHeight() / 2);

        if(!isStop){
            if(stepDelay <= 0){
                Sounds.footstep.play();
                stepDelay = 0.3f;
            }
        }
        setRegion(getFrame(dt));

        //퀘스트. 총알 발사하지 않고 무기 10개 모으기.
        if(!getisShoot() && getWeaponStack()>=10) {
            setWeapon(Weapon.Type.SUPER); this.weaponStack = 9;
        }
    }

    //프레임 받아오기
    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        if(isStop){
            previousState = currentState;
            stateTimer = 0;
            switch(previousState){
                case UP:
                    region = crewmateBackStand;
                    break;
                case LEFT:
                    region = crewmateLeftStand;
                    break;
                case RIGHT:
                    region = crewmateRightStand;
                    break;
                default:
                    region = crewmateFrontStand;
                    break;
            }
            return region;
        }
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

    //현재 스테이트 업데이트
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
            return currentState;
        }
    }

    // 크루메이트 생성 정보
    @SuppressWarnings("unchecked")
    public JSONObject getCrewmateEnterJson() {
        JSONObject json = new JSONObject();

        json.put("owner", owner);
        json.put("name", name);
        json.put("color", color);

        return json;
    }

    //크루메이트 초기화 정보
    @SuppressWarnings("unchecked")
    public JSONObject getCrewmateInitJson(){
        JSONObject childJson = new JSONObject();

        childJson.put("owner", owner);
        childJson.put("name", name);
        childJson.put("color", color);


        childJson.put("x", getX());
        childJson.put("y", getY());
        childJson.put("drmX", drmX);
        childJson.put("drmY", drmY);
        childJson.put("frameNum", getFrameNum());

//        childJson.put("maxHP", maxHP);
        childJson.put("HP", HP);

        return childJson;
    }

    // 멀티에 사용할 프레임 번호 생성
    private int getFrameNum() {
        int move = (int)(stateTimer / 0.2F)+1;
        if(currentState == State.RIGHT){
            if(isStop)
                return 0;
            else
                return move;
        }else if(currentState == State.LEFT) {
            if(isStop)
                return 4;
            else
                return 4+move;
        }else if(currentState == State.DOWN) {
            if(isStop)
                return 8;
            else
                return 8+move;
        }else if(currentState == State.UP) {
            if(isStop)
                return 12;
            else
                return 12+move;
        }else{
            return 0;
        }
    }
}
