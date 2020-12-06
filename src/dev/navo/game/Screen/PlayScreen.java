package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import dev.navo.game.Sprites.Items.ItemGroup;
import dev.navo.game.Tools.B2WorldCreator;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Util;

import java.util.ArrayList;

public class PlayScreen implements Screen {

    private NavoGame game;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public static World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate2D myCrewmate;

    private ArrayList<HitEffect> hitList; //타격 이펙트 리스트

    private ArrayList<Bullet> myBullets; // 내가쏜 총알
    private ArrayList<Bullet> otherBullets; // 적이쏜 총알

    private ArrayList<Rectangle> blocks;

    private ArrayList<ItemGroup> itemList;
    private ArrayList<Weapon> wList;
    private ArrayList<Rectangle> recList;
    private Vector2 centerHP;

    ShapeRenderer shapeRenderer;

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;

    boolean isShowMinimap = false;

    public PlayScreen(NavoGame game) {
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

        recList = b2.getRecList();

        myBullets = new ArrayList<>();
        otherBullets = Room.getRoom().getBullets();
        //        otherWeaponBullets = Room.getRoom().getWeaponBullets(); //Room에 추가해야할거같음.

        hitList = new ArrayList<>();    //추가

        initItem(); // 아이템 초기화
        createSideBlock(); //
    }

    public void handleInput ( float dt){
        Util.moveInputHandle(myCrewmate, maxSpeed, moveSpeed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && myCrewmate.getAttackDelay() <= 0) {
            Client.getInstance().shoot(myCrewmate.getX(),myCrewmate.getY(),myCrewmate.currentState);
            attack();
            // To DO : Client.getInstance().shoot(); 쏘는 방향, x, y, type
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new LobbyScreen(game));

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) isShowMinimap = !isShowMinimap;

        //v로 무기 버리기
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)){
            myCrewmate.setWeapon(Weapon.Type.NORMAL);
        }
        //z로 템줍
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            weaponGet();
        }
    }

    public void weaponGet(){ //추가
        Weapon weapon;
        for(int i = 0 ; i< wList.size() ; i++) {
            weapon = wList.get(i);
            if (myCrewmate.getX() >= weapon.getX()- myCrewmate.getWidth() && myCrewmate.getX() <= weapon.getX()+weapon.getWidth())
                if (myCrewmate.getY() >= weapon.getY()- myCrewmate.getHeight() && myCrewmate.getY() <= weapon.getY()+weapon.getHeight()) {
                    wList.remove(i);
                    myCrewmate.setWeapon(weapon.getType());
                    break;
                }
        }
    }

    public void attack(){
        myCrewmate.shooting();

        myBullets.add(new Bullet( // 총알 생성
                world
                , new Vector2(myCrewmate.getX()
                , myCrewmate.getY())
                , myCrewmate.currentState
                , myCrewmate.getWeapon()
                )
        );
    }

    public void update (float dt){
        handleInput(dt);
        Util.frameSet(world);
        myCrewmate.update(dt);

        //퀘스트. 총알 발사하지 않고 무기 10개 모으기.
        if(!myCrewmate.getisShoot()&&myCrewmate.getWeaponStack()>=10){
            myCrewmate.setWeapon(Weapon.Type.SUPER);
        }

//        for (int i = 0; i < otherWeaponBullets.size(); i++) if(otherWeaponBullets.get(i).distanceOverCheck()) otherWeaponBullets.remove(i--);

        for(CrewmateMulti crewmateMulti : Room.getRoom().getCrewmates()) crewmateMulti.update(dt);

        //추가. 충돌 이펙트 한번 보여주고 제거
        for(int i = 0; i < hitList.size(); i++){
            HitEffect hit = hitList.get(i);
            if(hit.getStateTimer() >= hit.getFrameDuration()*4)
                hitList.remove(i--);
        }

        //총알과 벽 충돌체크
        Bullet bullet;
        for(int i = 0; i < myBullets.size() ; i++){
            bullet = myBullets.get(i);
            for (Rectangle block : blocks) {
                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth())
                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
                        if(!bullet.getType().equals(Weapon.Type.SUPER)){
                            myBullets.remove(i--);
                            break;
                        }
                    }
            }
        }
        //다른 총알 벽 충돌체크.
        for(int i = 0; i < otherBullets.size() ; i++){
            bullet = otherBullets.get(i);
            for (Rectangle block : blocks) {
                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth())
                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
                        if(!bullet.getType().equals(Weapon.Type.SUPER)){
                            otherBullets.remove(i--); //myBullets을 otherBullets로 바꿈
                            break;
                        }
                    }
            }
        }

        //상민
        //총알과 캐릭터 충돌체크
        for(int i = 0 ; i< otherBullets.size() ; i++) {
            bullet = otherBullets.get(i);
            if (bullet.getX() >= myCrewmate.getX() - bullet.getWidth() && bullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
                if (bullet.getY() >= myCrewmate.getY() - bullet.getHeight() && bullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
                    otherBullets.remove(i--);
                    myCrewmate.hit();
                    //추가. 이펙트 생성
                    hitList.add(new HitEffect(world,
                            new Vector2((myCrewmate.getX()-(myCrewmate.getX()-bullet.getX())/2)-3,
                                    (myCrewmate.getY()-(myCrewmate.getY()-bullet.getY())/2)-5)));
                    break;
                }
            }
        }


        // 추가. 무기총알과 캐릭터 충돌체크
//        for(int i = 0 ; i< otherWeaponBullets.size() ; i++) {
//            weaponBullet = otherWeaponBullets.get(i);
//            if (weaponBullet.getX() >= myCrewmate.getX() - weaponBullet.getWidth() && weaponBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (weaponBullet.getY() >= myCrewmate.getY() - weaponBullet.getHeight() && weaponBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherWeaponBullets.remove(i--);
//                    if(weaponBullet.getType()==0) myCrewmate.hit(); //빨간무기는 딜이 두배
//                    myCrewmate.hit();
//                    hitList.add(new HitEffect( world, new Vector2(myCrewmate.getX(),myCrewmate.getY() )));
//                    break;
//                }
//            }
//        }
//        for(int i = 0 ; i< otherWeaponBullets.size() ; i++) {
//            weaponBullet = otherWeaponBullets.get(i);
//            if (weaponBullet.getX() >= myCrewmate.getX() - weaponBullet.getWidth() && weaponBullet.getX() <= myCrewmate.getX() + myCrewmate.getWidth()){
//                if (weaponBullet.getY() >= myCrewmate.getY() - weaponBullet.getHeight() && weaponBullet.getY() <= myCrewmate.getY() + myCrewmate.getHeight()) {
//                    otherWeaponBullets.remove(i--);
//                    myCrewmate.hit();
//                    //추가. 이펙트 생성
//                    hitList.add(new HitEffect(world,
//                            new Vector2((myCrewmate.getX()-(myCrewmate.getX()-weaponBullet.getX())/2)-3,
//                                    (myCrewmate.getY()-(myCrewmate.getY()-weaponBullet.getY())/2)-5)));
//                    break;
//                }
//            }
//        }


        //상민
        //추가. 아이템 습득체크
        ItemGroup it;
        for(int i = 0; i<itemList.size();i++){
            it = itemList.get(i);
            if (myCrewmate.getX() >= it.getX() - myCrewmate.getWidth() && myCrewmate.getX() <= it.getX() + it.getWidth())
                if (myCrewmate.getY() >= it.getY() - myCrewmate.getHeight() && myCrewmate.getY() <= it.getY() + it.getHeight()) {
                    itemList.remove(i--);
                    if(it.getType()==0)
                        myCrewmate.heal();
                    else if(it.getType()==1)
                        myCrewmate.setMaxSpeed(myCrewmate.getMaxSpeed()+10);
                    else if(it.getType()==2)
                        myCrewmate.hit();
                }
        }

        myBullets.removeIf(b -> b.update(dt)); // 내가 쏜 총알 체크

        otherBullets.removeIf(b -> b.update(dt)); // 상대가 쏜 총알 체크

//        for (WeaponBullet wb : otherWeaponBullets) wb.update(dt); //추후 남 무기 총알 구현시 필요
        for (HitEffect hit : hitList) hit.update(dt); //추가

        //hud.showMessage("c1.velocity" + myCrewmate.b2Body.getLinearVelocity().toString());

        gameCam.position.x = myCrewmate.b2Body.getPosition().x;
        gameCam.position.y = myCrewmate.b2Body.getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
    }
    //상민
    private void initItem () {
        itemList=new ArrayList<>();
        wList=new ArrayList<>();

        //상민
        //추가. 벽에 겹치지 않게 아이템 생성
        for(int i = 0; i<300; i++){
            boolean check = true;
            ItemGroup item = new ItemGroup(world,
                    new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20),
                    i%3);
            for(int j = 0; j<recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (item.getX() >= rect.getX() - item.getWidth() && item.getX() <= rect.getX() + rect.getWidth())
                    if (item.getY() >= rect.getY() - item.getHeight() && item.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if(check){itemList.add(item);}
            else i--;
        }

        //상민
        //추가. 벽에 겹치지 않게 무기 생성
        ArrayList<Weapon.Type> types = new ArrayList<>();
        types.add(Weapon.Type.BLUE);
        types.add(Weapon.Type.GREEN);
        types.add(Weapon.Type.RED);
        for(int i = 0; i< 300; i++){
            boolean check = true;
            Weapon.Type type = types.get(i % 3);
            Weapon weapon = new Weapon(world,
                    new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20),
                    type);
            for (int j = 0; j < recList.size(); j++) {
                Rectangle rect = recList.get(j);
                if (weapon.getX() >= rect.getX() - weapon.getWidth() && weapon.getX() <= rect.getX() + rect.getWidth())
                    if (weapon.getY() >= rect.getY() - weapon.getHeight() && weapon.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if (check) {
                wList.add(weapon);
            } else i--;
        }
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

        for (Bullet b : myBullets) b.draw(game.batch);

        for (Bullet b : otherBullets) b.draw(game.batch);

//        for (WeaponBullet wb : otherWeaponBullets) //추후 추가 필요
//            wb.draw(game.batch);

        for(ItemGroup it : itemList) //추가
            it.draw(game.batch);

        for(Weapon w : wList) //추가
            w.draw(game.batch);

        for (HitEffect hit : hitList) //추가
            hit.draw(game.batch);

        drawMinimap();

        game.batch.end();

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        if(!isShowMinimap) {
            hud.stage.draw();
        }
    }

    private void drawMinimap() {
        if(isShowMinimap) {
            game.batch.draw(Images.minimap, myCrewmate.b2Body.getPosition().x - 200, myCrewmate.b2Body.getPosition().y - 150);
            shapeRenderer.circle((myCrewmate.b2Body.getPosition().x / 2),
                    (myCrewmate.b2Body.getPosition().y / 2),
                    10
            );
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
}