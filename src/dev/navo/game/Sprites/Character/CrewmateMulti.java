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
import dev.navo.game.Tools.FontGenerator;
import org.json.simple.JSONObject;

public class CrewmateMulti extends Sprite {

    public World world;

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

    //움직임 표현
    private int frameNum;

    private float drmX;
    private float drmY;

    private final static float frameDuration = 0.2F;

    //Getter
    public float getMaxHP() { return maxHP;}
    public float getHP() { return HP;}
    public String getName() { return name;}

    public Label getLabel(){
        return nameLabel;
    }

    //Setter
    public void hit() {
        if(HP != 0) this.HP--;
    }

    //Constructor
    public CrewmateMulti(TextureAtlas atlas, JSONObject crewmate) {
        super(atlas.findRegion(crewmate.get("color").toString()));
        this.world = new World(new Vector2(0, 0), true);

        this.owner = crewmate.get("owner").toString();
        this.name = crewmate.get("name").toString();
        this.color = crewmate.get("color").toString();

        this.maxHP = Integer.parseInt(crewmate.get("maxHP").toString());
        this.HP = Integer.parseInt(crewmate.get("HP").toString());

        this.drmX = Float.parseFloat(crewmate.get("drmX").toString());
        this.drmY = Float.parseFloat(crewmate.get("drmY").toString());

        nameLabel = new Label("Other", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        nameLabel.setWidth(50);
        nameLabel.setHeight(15);
        nameLabel.setFontScale(0.25f);
        nameLabel.setAlignment(Align.center);

        initFrame();

        setBounds( Integer.parseInt(crewmate.get("x").toString())
                , Integer.parseInt(crewmate.get("y").toString())
                , 20
                , 25);
        setRegion(crewmateFrontStand);
    }

    //캐릭터 움직임 애니메이션 초기화
    private void initFrame(){
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

    // 매 프레임마다 업데이트
    public void update(float dt){
        //DRM으로 이동하는 로직 추가
        setPosition(getX() + drmX
                ,getY() + drmY);
        setRegion(getFrame());
    }

    //받아온 정보로 초기화
    public void updateInfo(JSONObject crewmateJson) {
        setPosition(Integer.parseInt(crewmateJson.get("x").toString())
                ,Integer.parseInt(crewmateJson.get("y").toString()));

        this.maxHP = Integer.parseInt(crewmateJson.get("maxHP").toString());
        this.HP = Integer.parseInt(crewmateJson.get("HP").toString());

        this.frameNum = Integer.parseInt(crewmateJson.get("frameNum").toString());

        this.name = crewmateJson.get("name").toString();
        nameLabel.setText(name);
        this.color = crewmateJson.get("color").toString();

        this.drmX = Float.parseFloat(crewmateJson.get("drmX").toString());
        this.drmY = Float.parseFloat(crewmateJson.get("drmY").toString());

    }

    //번호로 프레임 설정하기
    public TextureRegion getFrame(){
        float dt = 0.2F * (frameNum % 4 - 1);
        if(frameNum/4 == 0){ //RIGHT
            if(frameNum%4 == 0) return crewmateRightStand; // 오른쪽인데 멈춰 있다면
            return (TextureRegion)crewmateRight.getKeyFrame(dt); // 오른쪽인데 움직이고 있다면
        }else if(frameNum / 4 == 1){ //LEFT
            if(frameNum%4 == 0) return crewmateLeftStand; // 왼쪽인데 멈춰 있다면
            return (TextureRegion)crewmateLeft.getKeyFrame(dt); // 오른쪽인데 움직이고 있다면
        }else if(frameNum / 4 == 2){ //DOWN
            if(frameNum%4 == 0) return crewmateFrontStand; // 앞쪽인데 멈춰 있다면
            return (TextureRegion)crewmateFront.getKeyFrame(dt); // 오른쪽인데 움직이고 있다면
        }else if(frameNum / 4 == 3){ // UP
            if(frameNum%4 == 0) return crewmateBackStand; // 뒤쪽인데 멈춰 있다면
            return (TextureRegion)crewmateBack.getKeyFrame(dt); // 오른쪽인데 움직이고 있다면
        }else{
            return crewmateFrontStand;
        }
    }
}
