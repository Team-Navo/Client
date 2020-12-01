package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.Client.Client;
import dev.navo.game.Client.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.*;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Sprites.Items.HpItem;
import dev.navo.game.Sprites.Items.ItemSample;
import dev.navo.game.Sprites.Items.SpeedItem;
import dev.navo.game.Sprites.Items.TrapItem;
import dev.navo.game.Tools.B2WorldCreator;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Util;

import java.util.ArrayList;

public class PlayScreen implements Screen {

    private NavoGame game;
    private TextureAtlas atlas, item, laser, effect;;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public static World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate2D myCrewmate;
    private ArrayList<Crewmate2D> crewmates;
    private HitEffect hit; //추가
    private ArrayList<HitEffect> hitList; //추가

    private ArrayList<Bullet> myBullets;
    private ArrayList<Bullet> otherBullets;
    private ArrayList<RedBullet> myRedBullets; //추가
    private ArrayList<RedBullet> otherRedBullets; //추가
    private ArrayList<BlueBullet> myBlueBullets; //추가
    private ArrayList<BlueBullet> otherBlueBullets; //추가
    private ArrayList<GreenBullet> myGreenBullets; //추가
    private ArrayList<GreenBullet> otherGreenBullets; //추가
    private ArrayList<QuestBullet> myQuestBullets; // 추가
    private ArrayList<QuestBullet> otherQuestBullets; //추가

    private ArrayList<Rectangle> blocks;
    private HpItem h1;
    private ArrayList<HpItem> hList;
    private SpeedItem s1;
    private ArrayList<SpeedItem> sList;
    private TrapItem t1;
    private ArrayList<TrapItem> tList;
    private ItemSample is1;
    private ArrayList<ItemSample> isList;
    private ArrayList<Weapon1> wList1;  //추가
    private ArrayList<Weapon2> wList2;  //추가
    private ArrayList<Weapon3> wList3;  //추가


    private ArrayList<Rectangle> recList;
    private Vector2 centerHP;

    TextureRegion minimap;

    ShapeRenderer shapeRenderer;

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;

    boolean isShowMinimap = false;

    //Getter
//    public TextureAtlas getAtlas () {
//        return atlas;
//    }
//    public TextureAtlas getItemAtlas () {
//        return item;
//    }
    public TextureAtlas getAtlas(){
        return atlas;
    }   //추가
    public TextureAtlas getItemAtlas(){
        return item;
    }   //추가
    public TextureAtlas getLaserAtlas(){return laser;}  //추가
    public TextureAtlas getEffectAtlas(){return effect;}    //추가

    public PlayScreen(NavoGame game) {
        initAtlas(); //성경 추가
        this.game = game;
        shapeRenderer = new ShapeRenderer();
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, gameCam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapType);
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(200, 1130, 0); // 200, 1130 = Left Top

        centerHP = new Vector2(375, 325);
        hud = new Hud(game.batch);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        B2WorldCreator b2 = new B2WorldCreator(world, map);
        blocks = b2.getRecList();

        myCrewmate = Room.getRoom().getMyCrewmate();
        myCrewmate.setWorld(world);
        myCrewmate.colorSetting();
        hud.addActor(myCrewmate.getLabel());

        crewmates = new ArrayList<>();

        recList = b2.getRecList();

        myBullets = new ArrayList<>();
        otherBullets = Room.getRoom().getBullets();
        myRedBullets = new ArrayList<>(); //추가
//        otherRedBullets = Room.getRoom().getRedBullets(); 같은 형식으로 추가해야할거같음.
        myBlueBullets = new ArrayList<>(); //추가
//        otherBlueBullets = Room.getRoom().getBlueBullets(); 같은 형식으로 추가해야할거같음.
        myGreenBullets = new ArrayList<>(); //추가
//        otherGreenBullets = Room.getRoom().getGreenBullets(); 같은 형식으로 추가해야할거같음.
        myQuestBullets = new ArrayList<>(); //추가
//        otherQuestBullets = Room.getRoom().getQuestBullets(); 같은 형식으로 추가해야할거같음.

        hitList = new ArrayList<>();    //추가

        initItem(); // 아이템 초기화
        createSideBlock(); //

        minimap = new TextureRegion(Images.minimap,
                (int)(myCrewmate.b2Body.getPosition().x) / 4 - gamePort.getScreenWidth() / 16,
                (int)(1280 - myCrewmate.b2Body.getPosition().y) / 4 - gamePort.getScreenHeight() / 16,
                gamePort.getScreenWidth() / 4 ,
                gamePort.getScreenHeight() / 4);
    }


    public void initAtlas(){    //성경 추가
        atlas = new TextureAtlas("Image.atlas");
        item = new TextureAtlas("Item.atlas");
        laser = new TextureAtlas("laser.atlas");
        effect = new TextureAtlas("effect.atlas");
    }

    public void handleInput ( float dt){
        Util.moveInputHandle(myCrewmate, maxSpeed, moveSpeed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && myCrewmate.getAttackDelay() <= 0) {
            myBullets.add(new Bullet(world, new Vector2(myCrewmate.getX(), myCrewmate.getY()), myCrewmate.currentState)); // 총알 생성
            Client.getInstance().shoot(myCrewmate.getX(),myCrewmate.getY(),myCrewmate.currentState);
            myCrewmate.setAttackDelay(0.3f);//공격 딜레이 설정
            attack();
            // To DO : Client.getInstance().shoot(); 쏘는 방향, x, y, type
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new LobbyScreen(game));

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) isShowMinimap = !isShowMinimap;

        if (Gdx.input.isKeyJustPressed(Input.Keys.V)){
            myCrewmate.setWeapon(0);
        }

            //z로 템줍
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            weapon1Get(myCrewmate, wList1); //추가
            weapon2Get(myCrewmate, wList2); //추가
            weapon3Get(myCrewmate, wList3); //추가
            ItemSample is;
            for(int i = 0 ; i< isList.size() ; i++) {
                is = isList.get(i);
                if (myCrewmate.getX() >= is.getX() - myCrewmate.getWidth() && myCrewmate.getX() <= is.getX() + is.getWidth()) {
                    if (myCrewmate.getY() >= is.getY() - myCrewmate.getHeight() && myCrewmate.getY() <= is.getY() + is.getHeight()) {
                        isList.remove(i--);
                        myCrewmate.heal();
                        myCrewmate.setMaxSpeed(myCrewmate.getMaxSpeed() + 10);
                        break;
                    }
                }
            }

        }
    }
    public void weapon1Get(Crewmate2D myCrewmate, ArrayList<Weapon1> wList1){ //추가
        Weapon1 w1;
        Crewmate2D crewmate = myCrewmate;
        for(int i = 0 ; i< wList1.size() ; i++) {
            w1 = wList1.get(i);
            if (crewmate.getX() >= w1.getX()-crewmate.getWidth() && crewmate.getX() <= w1.getX()+w1.getWidth())
                if (crewmate.getY() >= w1.getY()-crewmate.getHeight() && crewmate.getY() <= w1.getY()+w1.getHeight()) {
                    wList1.remove(i--);
                    crewmate.setWeapon(1);
                    crewmate.setBulletMany(15);
                    if(crewmate.getMyColor().equals("Purple"))
                        crewmate.setBulletMany(25);
                    break;
            }
        }
    }
    public void weapon2Get(Crewmate2D myCrewmate, ArrayList<Weapon2> wList2){ //추가
        Weapon2 w2;
        Crewmate2D crewmate = myCrewmate;
        for(int i = 0 ; i< wList2.size() ; i++) {
            w2 = wList2.get(i);
            if (crewmate.getX() >= w2.getX()-crewmate.getWidth() && crewmate.getX() <= w2.getX()+w2.getWidth())
                if (crewmate.getY() >= w2.getY()-crewmate.getHeight() && crewmate.getY() <= w2.getY()+w2.getHeight()) {
                    wList2.remove(i--);
                    crewmate.setWeapon(2);
                    crewmate.setBulletMany(40);
                    if(crewmate.getMyColor().equals("Purple"))
                        crewmate.setBulletMany(60);
                    break;
                }
        }
    }
    public void weapon3Get(Crewmate2D myCrewmate, ArrayList<Weapon3> wList3){ //추가
        Weapon3 w3;
        Crewmate2D crewmate = myCrewmate;
        for(int i = 0 ; i< wList3.size() ; i++) {
            w3 = wList3.get(i);
            if (crewmate.getX() >= w3.getX()-crewmate.getWidth() && crewmate.getX() <= w3.getX()+w3.getWidth())
                if (crewmate.getY() >= w3.getY()-crewmate.getHeight() && crewmate.getY() <= w3.getY()+w3.getHeight()) {
                    wList3.remove(i--);
                    crewmate.setWeapon(3);
                    crewmate.setBulletMany(40);
                    if(crewmate.getMyColor().equals("Purple"))
                        crewmate.setBulletMany(60);

                }
        }
    }

    public void attack(){
        if(myCrewmate.getWeapon()==0){
            myBullets.add(new Bullet(world, this, new Vector2(myCrewmate.getX(), myCrewmate.getY()), myCrewmate.currentState)); // 총알 생성
            myCrewmate.setAttackDelay(0.4f);//공격 딜레이 설정
            myCrewmate.shooting();
            if(myCrewmate.getMyColor().equals("Blue"))
                myCrewmate.setAttackDelay(0.2f);

        }
        else if(myCrewmate.getWeapon()==1){
            myRedBullets.add(new RedBullet(world,this, new Vector2(myCrewmate.getX(), myCrewmate.getY()),myCrewmate.currentState));
            myCrewmate.setAttackDelay(0.5f);
            myCrewmate.bulletManyDown();
            myCrewmate.shooting();
            if(myCrewmate.getBulletMany()==0)
                myCrewmate.setWeapon(0);
        }
        else if(myCrewmate.getWeapon()==2){
            myBlueBullets.add(new BlueBullet(world,this, new Vector2(myCrewmate.getX(), myCrewmate.getY()),myCrewmate.currentState));
            myCrewmate.setAttackDelay(0.1f);
            myCrewmate.bulletManyDown();
            myCrewmate.shooting();
            if(myCrewmate.getBulletMany()==0)
                myCrewmate.setWeapon(0);
        }
        else if(myCrewmate.getWeapon()==3){
            myGreenBullets.add(new GreenBullet(world,this, new Vector2(myCrewmate.getX(), myCrewmate.getY()),myCrewmate.currentState));
            myCrewmate.setAttackDelay(0.2f);
            myCrewmate.bulletManyDown();
            myCrewmate.shooting();
            if(myCrewmate.getBulletMany()==0)
                myCrewmate.setWeapon(0);
        }
        else if(myCrewmate.getWeapon()==4){
            myQuestBullets.add(new QuestBullet(world,this, new Vector2(myCrewmate.getX(), myCrewmate.getY()),myCrewmate.currentState));
            myCrewmate.setAttackDelay(0.2f);
            myCrewmate.bulletManyDown();
            myCrewmate.shooting();
            if(myCrewmate.getBulletMany()==0)
                myCrewmate.setWeapon(0);
        }
    }

    public void update (float dt){
        handleInput(dt);
        Util.frameSet(world);
        myCrewmate.update(dt);

        //추가
        if(!myCrewmate.getisShoot()&&myCrewmate.getWeaponStack()>=10){
            myCrewmate.setWeapon(4);
            myCrewmate.setBulletMany(60);
        }

        for (int i = 0; i < myBullets.size(); i++) if (myBullets.get(i).distanceOverCheck()) myBullets.remove(i--);
        for (int i = 0; i < otherBullets.size(); i++) if (otherBullets.get(i).distanceOverCheck()) otherBullets.remove(i--);

        //추가. RedBullet
        for (int i = 0; i < myRedBullets.size(); i++) if (myRedBullets.get(i).distanceOverCheck()) myRedBullets.remove(i--);
//        for (int i = 0; i < otherRedBullets.size(); i++) if (otherRedBullets.get(i).distanceOverCheck()) otherRedBullets.remove(i--);

        //추가. BlueBullet
        for (int i = 0; i < myBlueBullets.size(); i++) if (myBlueBullets.get(i).distanceOverCheck()) myBlueBullets.remove(i--);
//        for (int i = 0; i < otherBlueBullets.size(); i++) if (otherBlueBullets.get(i).distanceOverCheck()) otherBlueBullets.remove(i--);

        //추가. GreenBullet
        for (int i = 0; i < myGreenBullets.size(); i++) if (myGreenBullets.get(i).distanceOverCheck()) myGreenBullets.remove(i--);
//        for (int i = 0; i < otherGreenBullets.size(); i++) if (otherGreenBullets.get(i).distanceOverCheck()) otherGreenBullets.remove(i--);

        //추가. QuestBullet
        for (int i = 0; i < myQuestBullets.size(); i++) if (myQuestBullets.get(i).distanceOverCheck()) myQuestBullets.remove(i--);
//        for (int i = 0; i < otherQuestBullets.size(); i++) if (otherQuestBullets.get(i).distanceOverCheck()) otherQuestBullets.remove(i--);



        for(CrewmateMulti crewmateMulti : Room.getRoom().getCrewmates()) {
            crewmateMulti.update(dt);
        }
        //추가. 충돌 이펙트 한번 보여주고 제거
        for(int i = 0; i<hitList.size(); i++){
            HitEffect hit = hitList.get(i);
            if(hit.getStateTimer() >= hit.getFrameDuration()*4)
                hitList.remove(i);
        }


        //총알과 벽 충돌체크
        Bullet bullet;
        for(int i = 0; i < myBullets.size() ; i++){
            bullet = myBullets.get(i);
            for (Rectangle block : blocks) {
                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth())
                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
                        myBullets.remove(i--);
                        break;
                    }
            }
        }
//        for(int i = 0; i < otherBullets.size() ; i++){
//            bullet = otherBullets.get(i);
//            for (Rectangle block : blocks) {
//                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth()&&!myBullets.isEmpty())
//                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
//                        myBullets.remove(i--);
        }
        for(int i = 0; i < otherBullets.size() ; i++){
            bullet = otherBullets.get(i);
            for (Rectangle block : blocks) {
                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth())
                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
                        otherBullets.remove(i--); //myBullets을 otherBullets로 바꿈
                        break;
                    }
            }
        }
        //추가
        RedBullet redBullet;
        for(int i = 0; i < myRedBullets.size() ; i++){
            redBullet = myRedBullets.get(i);
            for (Rectangle block : blocks) {
                if (redBullet.getX() >= block.getX() - redBullet.getWidth() && redBullet.getX() <= block.getX() + block.getWidth())
                    if (redBullet.getY() >= block.getY() - redBullet.getHeight() && redBullet.getY() <= block.getY() + block.getHeight()) {
                        myRedBullets.remove(i--);
                        break;
                    }
            }
        }
//        for(int i = 0; i < otherRedBullets.size() ; i++){
//            redBullet = otherRedBullets.get(i);
//            for (Rectangle block : blocks) {
//                if (redBullet.getX() >= block.getX() - redBullet.getWidth() && redBullet.getX() <= block.getX() + block.getWidth())
//                    if (redBullet.getY() >= block.getY() - redBullet.getHeight() && redBullet.getY() <= block.getY() + block.getHeight()) {
//                        otherRedBullets.remove(i--);
//                        break;
//                    }
//            }
//        }
        //추가
        BlueBullet blueBullet;
        for(int i = 0; i < myBlueBullets.size() ; i++){
            blueBullet = myBlueBullets.get(i);
            for (Rectangle block : blocks) {
                if (blueBullet.getX() >= block.getX() - blueBullet.getWidth() && blueBullet.getX() <= block.getX() + block.getWidth())
                    if (blueBullet.getY() >= block.getY() - blueBullet.getHeight() && blueBullet.getY() <= block.getY() + block.getHeight()) {
                        myBlueBullets.remove(i--);
                        break;
                    }
            }
        }
//        for(int i = 0; i < otherBlueBullets.size() ; i++){
//            blueBullet = otherBlueBullets.get(i);
//            for (Rectangle block : blocks) {
//                if (blueBullet.getX() >= block.getX() - blueBullet.getWidth() && blueBullet.getX() <= block.getX() + block.getWidth())
//                    if (blueBullet.getY() >= block.getY() - blueBullet.getHeight() && blueBullet.getY() <= block.getY() + block.getHeight()) {
//                        otherBlueBullets.remove(i--);
//                        break;
//                    }
//            }
//        }
        //추가
        GreenBullet greenBullet;
        for(int i = 0; i < myGreenBullets.size() ; i++){
            greenBullet = myGreenBullets.get(i);
            for (Rectangle block : blocks) {
                if (greenBullet.getX() >= block.getX() - greenBullet.getWidth() && greenBullet.getX() <= block.getX() + block.getWidth())
                    if (greenBullet.getY() >= block.getY() - greenBullet.getHeight() && greenBullet.getY() <= block.getY() + block.getHeight()) {
                        myGreenBullets.remove(i--);
                        break;
                    }
            }
        }
//        for(int i = 0; i < otherGreenBullets.size() ; i++){
//            greenBullet = otherGreenBullets.get(i);
//            for (Rectangle block : blocks) {
//                if (greenBullet.getX() >= block.getX() - greenBullet.getWidth() && greenBullet.getX() <= block.getX() + block.getWidth())
//                    if (greenBullet.getY() >= block.getY() - greenBullet.getHeight() && greenBullet.getY() <= block.getY() + block.getHeight()) {
//                        otherGreenBullets.remove(i--);
//                        break;
//                    }
//            }
//        }


//        for(int i = 0; i < otherQuestBullets.size() ; i++){
//            questBullet = otherQuestBullets.get(i);
//            for (Rectangle block : blocks) {
//                if (questBullet.getX() >= block.getX() - questBullet.getWidth() && questBullet.getX() <= block.getX() + block.getWidth())
//                    if (questBullet.getY() >= block.getY() - questBullet.getHeight() && questBullet.getY() <= block.getY() + block.getHeight()) {
//                        otherQuestBullets.remove(i--);
//                        break;
//                    }
//            }
//        }

//                Crewmate2D crewmate;
////                for (int i = 0; i < bullets.size(); i++) {
////                    bullet = bullets.get(i);
////                    for (int j = 0; j < crewmates.size(); j++) {
////                        crewmate = crewmates.get(j);
////                        if (!myCrewmate.equals(crewmate)) {
////                            if (bullet.getX() >= crewmate.getX() - bullet.getWidth() && bullet.getX() <= crewmate.getX() + crewmate.getWidth())
////                                if (bullet.getY() >= crewmate.getY() - bullet.getHeight() && bullet.getY() <= crewmate.getY() + crewmate.getHeight()) {
////                                    bullets.remove(i--);


        //총알과 캐릭터 충돌체크
        for(int i = 0 ; i< otherBullets.size() ; i++) {
            bullet = otherBullets.get(i);
            if (bullet.getX() >= myCrewmate.getX() - bullet.getWidth() && bullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
                if (bullet.getY() >= myCrewmate.getY() - bullet.getHeight() && bullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
                    otherBullets.remove(i--);
                    myCrewmate.hit();
                    //추가. 이펙트 생성
                    hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
                    break;
                }
            }
        }
        //추가. 타캐릭터 충돌 체크 로직. 정확하지 않아서 주석으로 처리.
//        for(int i = 0 ; i< myBullets.size() ; i++) {
//            bullet = myBullets.get(i);
//            for (int j = 0; j < crewmates.size(); j++){
//                Crewmate2D crewmate = crewmates.get(j);
//                if(!myCrewmate.equals(crewmate)){
//                    if (bullet.getX() >= crewmate.getX()-bullet.getWidth() && bullet.getX() <= crewmate.getX()+crewmate.getWidth())
//                        if (bullet.getY() >= crewmate.getY()-bullet.getHeight() && bullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
//                            myBullets.remove(i--);
//                            crewmate.hit();
//                            hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                            break;
//                        }
//                }
//            }
//        }
        // 추가. 빨간총알과 캐릭터 충돌체크
//        for(int i = 0 ; i< otherRedBullets.size() ; i++) {
//            redBullet = otherRedBullets.get(i);
//            if (redBullet.getX() >= myCrewmate.getX() - redBullet.getWidth() && redBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (redBullet.getY() >= myCrewmate.getY() - redBullet.getHeight() && redBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherRedBullets.remove(i--);
//                    myCrewmate.hit(); myCrewmate.hit();
//                    //추가. 이펙트 생성
//                    hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                    break;
//                }
//            }
//        }
//        for(int i = 0 ; i< myRedBullets.size() ; i++) {
//            redBullet = myRedBullets.get(i);
//            for (int j = 0; j < crewmates.size(); j++){
//                Crewmate2D crewmate = crewmates.get(j);
//                if(!myCrewmate.equals(crewmate)){
//                    if (redBullet.getX() >= crewmate.getX()-redBullet.getWidth() && redBullet.getX() <= crewmate.getX()+crewmate.getWidth())
//                        if (redBullet.getY() >= crewmate.getY()-redBullet.getHeight() && redBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
//                            myRedBullets.remove(i--);
//                            crewmate.hit(); crewmate.hit();
//                            hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                            break;
//                        }
//                }
//            }
//        }
        // 추가. 파란총알과 캐릭터 충돌체크
//        for(int i = 0 ; i< otherBlueBullets.size() ; i++) {
//            blueBullet = otherBlueBullets.get(i);
//            if (blueBullet.getX() >= myCrewmate.getX() - blueBullet.getWidth() && blueBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (blueBullet.getY() >= myCrewmate.getY() - blueBullet.getHeight() && blueBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherBlueBullets.remove(i--);
//                    myCrewmate.hit(); myCrewmate.hit();
//                    //추가. 이펙트 생성
//                    hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                    break;
//                }
//            }
//        }
//        for(int i = 0 ; i< myBlueBullets.size() ; i++) {
//            blueBullet = myBlueBullets.get(i);
//            for (int j = 0; j < crewmates.size(); j++){
//                Crewmate2D crewmate = crewmates.get(j);
//                if(!myCrewmate.equals(crewmate)){
//                    if (blueBullet.getX() >= crewmate.getX()-blueBullet.getWidth() && blueBullet.getX() <= crewmate.getX()+crewmate.getWidth())
//                        if (blueBullet.getY() >= crewmate.getY()-blueBullet.getHeight() && blueBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
//                            myBlueBullets.remove(i--);
//                            crewmate.hit(); crewmate.hit();
//                            hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                            break;
//                        }
//                }
//            }
//        }

        // 추가. 초록총알과 캐릭터 충돌체크
//        for(int i = 0 ; i< otherGreenBullets.size() ; i++) {
//            greenBullet = otherGreenBullets.get(i);
//            if (greenBullet.getX() >= myCrewmate.getX() - greenBullet.getWidth() && greenBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (greenBullet.getY() >= myCrewmate.getY() - greenBullet.getHeight() && greenBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherGreenBullets.remove(i--);
//                    myCrewmate.hit(); myCrewmate.hit();
//                    //추가. 이펙트 생성
//                    hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                    break;
//                }
//            }
//        }
//        for(int i = 0 ; i< myGreenBullets.size() ; i++) {
//            greenBullet = myGreenBullets.get(i);
//            for (int j = 0; j < crewmates.size(); j++){
//                Crewmate2D crewmate = crewmates.get(j);
//                if(!myCrewmate.equals(crewmate)){
//                    if (greenBullet.getX() >= crewmate.getX()-greenBullet.getWidth() && greenBullet.getX() <= crewmate.getX()+crewmate.getWidth())
//                        if (greenBullet.getY() >= crewmate.getY()-greenBullet.getHeight() && greenBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
//                            myGreenBullets.remove(i--);
//                            crewmate.hit(); crewmate.hit();
//                            hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                            break;
//                        }
//                }
//            }
//        }
        // 추가. 퀘스트총알과 캐릭터 충돌체크
//        for(int i = 0 ; i< otherQuestBullets.size() ; i++) {
//            questBullet = otherQuestBullets.get(i);
//            if (questBullet.getX() >= myCrewmate.getX() - questBullet.getWidth() && questBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (questBullet.getY() >= myCrewmate.getY() - questBullet.getHeight() && questBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherQuestBullets.remove(i--);
//                    myCrewmate.hit(); myCrewmate.hit();
//                    //추가. 이펙트 생성
//                    hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                    break;
//                }
//            }
//        }
//        for(int i = 0 ; i< myQuestBullets.size() ; i++) {
//            questBullet = myQuestBullets.get(i);
//            for (int j = 0; j < crewmates.size(); j++){
//                Crewmate2D crewmate = crewmates.get(j);
//                if(!myCrewmate.equals(crewmate)){
//                    if (questBullet.getX() >= crewmate.getX()-questBullet.getWidth() && questBullet.getX() <= crewmate.getX()+crewmate.getWidth())
//                        if (questBullet.getY() >= crewmate.getY()-questBullet.getHeight() && questBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
//                            myQuestBullets.remove(i--);
//                            crewmate.hit(); crewmate.hit();
//                            hitList.add(new HitEffect(world,this,new Vector2(myCrewmate.getX(),myCrewmate.getY())));
//                            break;
//                        }
//                }
//            }
//        }

        for (int i = 0; i < crewmates.size(); i++) {
            Crewmate2D temp = crewmates.get(i);
            if (temp.getHP() == 0) {
                world.destroyBody(temp.b2Body);
                hud.removeActor(temp.getLabel());
                crewmates.remove(i--);
            }
        }
            //추가. 내 캐릭과 회복약 충돌 체크
            HpItem hp;
            for (int i = 0; i < hList.size(); i++) {
                hp = hList.get(i);
                if (myCrewmate.getX() >= hp.getX() - myCrewmate.getWidth() && myCrewmate.getX() <= hp.getX() + hp.getWidth())
                    if (myCrewmate.getY() >= hp.getY() - myCrewmate.getHeight() && myCrewmate.getY() <= hp.getY() + hp.getHeight()) {
                        hList.remove(i--);
                        myCrewmate.heal();
                    }

            }

            //추가. 내 캐릭과 스피드약 충돌 체크
            SpeedItem sp;
            for (int i = 0; i < sList.size(); i++) {
                sp = sList.get(i);
                if (myCrewmate.getX() >= sp.getX() - myCrewmate.getWidth() && myCrewmate.getX() <= sp.getX() + sp.getWidth())
                    if (myCrewmate.getY() >= sp.getY() - myCrewmate.getHeight() && myCrewmate.getY() <= sp.getY() + sp.getHeight()) {
                        sList.remove(i--);
                        myCrewmate.setMaxSpeed(myCrewmate.getMaxSpeed()+10);
                }
            }


            //추가. 내 캐릭과 스피드약 충돌 체크
            TrapItem tp;
             for(int i = 0 ; i< tList.size() ; i++) {
                tp = tList.get(i);
                if (myCrewmate.getX() >= tp.getX()-myCrewmate.getWidth() && myCrewmate.getX() <= tp.getX()+tp.getWidth())
                    if (myCrewmate.getY() >= tp.getY()-myCrewmate.getHeight() && myCrewmate.getY() <= tp.getY()+tp.getHeight()) {
                        tList.remove(i--);
                        myCrewmate.hit();
            }
        }

        for (Bullet b : myBullets) b.update(dt);
        for (Bullet b : otherBullets) b.update(dt);
        for (RedBullet rb : myRedBullets) rb.update(dt);    //추가
//        for (RedBullet rb : otherRedBullets) rb.update(dt); //추가
        for (BlueBullet bb : myBlueBullets) bb.update(dt);    //추가
//        for (BlueBullet bb : otherBlueBullets) bb.update(dt); //추가
        for (GreenBullet gb : myGreenBullets) gb.update(dt);    //추가
//        for (GreenBullet gb : otherGreenBullets) gb.update(dt); //추가
        for (QuestBullet qb : myQuestBullets) qb.update(dt);    //추가
//        for (QuestBullet qb : otherQuestBullets) qb.update(dt); //추가
        for (HpItem h : hList) h.update(dt);
        for (SpeedItem s : sList) s.update(dt);
        for (TrapItem t : tList) t.update(dt);
        for (ItemSample i : isList) i.update(dt);
        for (Weapon1 w1 : wList1) w1.update(dt); //추가
        for (Weapon2 w2 : wList2) w2.update(dt); //추가
        for (Weapon3 w3 : wList3) w3.update(dt); //추가
        for (HitEffect hit : hitList) hit.update(dt); //추가

        //hud.showMessage("c1.velocity" + myCrewmate.b2Body.getLinearVelocity().toString());

        gameCam.position.x = myCrewmate.b2Body.getPosition().x;
        gameCam.position.y = myCrewmate.b2Body.getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
    }

    private void initItem () {
        h1 = new HpItem(world, this, new Vector2(0, 0));
        s1 = new SpeedItem(world, this, new Vector2(21, 0));
        t1 = new TrapItem(world, this, new Vector2(42, 0));
        is1 = new ItemSample(world, this, new Vector2(63, 0));

        hList = new ArrayList<>();
        sList = new ArrayList<>();
        tList = new ArrayList<>();
        isList = new ArrayList<>();
        wList1 = new ArrayList<>(); //추가
        wList2 = new ArrayList<>(); //추가
        wList3 = new ArrayList<>(); //추가
        hList.add(h1);
        sList.add(s1);
        tList.add(t1);
        isList.add(is1);

        //벽에 겹치지 않게 Hp약 생성
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            HpItem hp = new HpItem(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (hp.getX() >= rect.getX() - hp.getWidth() && hp.getX() <= rect.getX() + rect.getWidth())
                    if (hp.getY() >= rect.getY() - hp.getHeight() && hp.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                hList.add(hp);
            } else i--;
        }

        //벽에 겹치지 않게 Speed약 생성
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            SpeedItem sp = new SpeedItem(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (sp.getX() >= rect.getX() - sp.getWidth() && sp.getX() <= rect.getX() + rect.getWidth())
                    if (sp.getY() >= rect.getY() - sp.getHeight() && sp.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                sList.add(sp);
            } else i--;
        }

        //벽에 겹치지 않게 Trap약 생성
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            TrapItem tp = new TrapItem(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (tp.getX() >= rect.getX() - tp.getWidth() && tp.getX() <= rect.getX() + rect.getWidth())
                    if (tp.getY() >= rect.getY() - tp.getHeight() && tp.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                tList.add(tp);
            } else i--;
        }
        //벽에 겹치지 않게 z습득약 생성
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            ItemSample is = new ItemSample(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (is.getX() >= rect.getX() - is.getWidth() && is.getX() <= rect.getX() + rect.getWidth())
                    if (is.getY() >= rect.getY() - is.getHeight() && is.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                isList.add(is);
            } else i--;
        }
        //추가
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            Weapon1 weapon1 = new Weapon1(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (weapon1.getX() >= rect.getX() - weapon1.getWidth() && weapon1.getX() <= rect.getX() + rect.getWidth())
                    if (weapon1.getY() >= rect.getY() - weapon1.getHeight() && weapon1.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                wList1.add(weapon1);
            } else i--;
        }
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            Weapon2 weapon2 = new Weapon2(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (weapon2.getX() >= rect.getX() - weapon2.getWidth() && weapon2.getX() <= rect.getX() + rect.getWidth())
                    if (weapon2.getY() >= rect.getY() - weapon2.getHeight() && weapon2.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                wList2.add(weapon2);
            } else i--;
        }
        for (int i = 0; i < 100; i++) {
            boolean check = true;
            Weapon3 weapon3 = new Weapon3(world, this, new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20));
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (weapon3.getX() >= rect.getX() - weapon3.getWidth() && weapon3.getX() <= rect.getX() + rect.getWidth())
                    if (weapon3.getY() >= rect.getY() - weapon3.getHeight() && weapon3.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                wList3.add(weapon3);
            } else i--;
        }

    }

    private void createSideBlock() {
        Body b2Body;
        BodyDef bDef = new BodyDef();
        bDef.position.set(0, 640);
        bDef.type = BodyDef.BodyType.StaticBody;
        b2Body = world.createBody(bDef);
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 640);
        fDef.shape = shape;
        b2Body.createFixture(fDef);

        bDef.position.set(1600, 640);
        b2Body = world.createBody(bDef);
        b2Body.createFixture(fDef);

        bDef.position.set(800, 0);
        b2Body = world.createBody(bDef);
        shape.setAsBox(800, 0.1f);
        fDef.shape = shape;
        b2Body.createFixture(fDef);

        bDef.position.set(800, 1280);
        b2Body = world.createBody(bDef);
        b2Body.createFixture(fDef);
    }


    // 800 x 600 해상도 기준
    @Override
    public void render ( float delta){

        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        Images.renderBackground(delta, game.batch);
        game.batch.end();

        renderer.render();
        b2dr.render(world, gameCam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        Room.getRoom().drawCrewmates(NavoGame.getGame().batch, myCrewmate.owner);
        myCrewmate.draw(game.batch);
        if(!isShowMinimap)
            shapeRenderer.rect(centerHP.x, centerHP.y, 50 * (myCrewmate.getHP() / myCrewmate.getMaxHP()), 10);

        myCrewmate.getLabel().setPosition(174, 166);

//        for (Crewmate2D c : crewmates) {
//            c.draw(game.batch);
//            if (!c.equals(myCrewmate)) {
//                shapeRenderer.rect(centerHP.x + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x) * 2,
//                        centerHP.y + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);
//
//                c.getLabel().setPosition(174 + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x),
//                        165 + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y));
//
//            } else {
//                myCrewmate.getLabel().setPosition(174, 166);
//            }
//        }

        for (Bullet b : myBullets)
            b.draw(game.batch);

        for (Bullet b : otherBullets)
            b.draw(game.batch);

        for (RedBullet rb : myRedBullets)  //추가
            rb.draw(game.batch);

//        for (RedBullet rb : otherRedBullets)   //추가
//            rb.draw(game.batch);

        for (BlueBullet bb : myBlueBullets)  //추가
            bb.draw(game.batch);

//        for (BlueBullet bb : otherBlueBullets)   //추가
//            bb.draw(game.batch);

        for (GreenBullet gb : myGreenBullets)  //추가
            gb.draw(game.batch);

//        for (GreenBullet gb : otherGreenBullets)   //추가
//            gb.draw(game.batch);

        for (QuestBullet qb : myQuestBullets)   //추가
            qb.draw(game.batch);

//        for (QuestBullet qb : otherQuestBullets)   //추가
//            qb.draw(game.batch);

        for (HpItem h : hList)
            h.draw(game.batch);

        for (SpeedItem s : sList)
            s.draw(game.batch);

        for (TrapItem t : tList)
            t.draw(game.batch);

        for (ItemSample i : isList)
            i.draw(game.batch);

        for(Weapon1 w1 : wList1) //추가
            w1.draw(game.batch);

        for(Weapon2 w2 : wList2) //추가
            w2.draw(game.batch);

        for(Weapon3 w3 : wList3) //추가
            w3.draw(game.batch);

        for (HitEffect hit : hitList) //추가
            hit.draw(game.batch);


        if(isShowMinimap) {
            game.batch.draw(Images.minimap, myCrewmate.b2Body.getPosition().x - 200, myCrewmate.b2Body.getPosition().y - 150);
            shapeRenderer.circle((myCrewmate.b2Body.getPosition().x / 2),
                    (myCrewmate.b2Body.getPosition().y / 2),
                    10
                    );
        }
        game.batch.end();

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        if(!isShowMinimap) {
            hud.stage.draw();
        }
    }
    @Override
    public void resize ( int width, int height){
        gamePort.update(width, height);
    }

    @Override
    public void dispose () {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    @Override
    public void hide () {

    }

    @Override
    public void resume () {

    }

    @Override
    public void pause () {

    }
    @Override
    public void show () {
    }
}