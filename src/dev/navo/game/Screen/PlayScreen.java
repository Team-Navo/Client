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
import dev.navo.game.Tools.Sounds;
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

    private ArrayList<ItemGroup> items;
    private ArrayList<Weapon> weapons;

    private Vector2 centerHP;

    ShapeRenderer shapeRenderer;
    ShapeRenderer lineRenderer;

    private String mapType = "map/Navo16.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;

    private static float radius = 500;
    private boolean isShowMinimap = false;
    private boolean isMagneticSoundPlay = false;

    private float magneticDelay = 0;
    private Vector2 centerOfMagnetic;
    public PlayScreen(NavoGame game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
        lineRenderer = new ShapeRenderer();
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, gameCam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapType);
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(200, 1130, 0); // 200, 1130 = Left Top

        centerHP = new Vector2(375, 345);
        hud = new Hud(game.batch);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        B2WorldCreator b2 = new B2WorldCreator(world, map);
        blocks = b2.getRecList();
        createSideBlock();

        myCrewmate = Room.getRoom().getMyCrewmate();
        myCrewmate.setWorld(world);
        myCrewmate.colorSetting();
        myCrewmate.getLabel().setPosition(174*2, 176*2);
        hud.addActor(myCrewmate.getLabel());

        myBullets = new ArrayList<>();
        otherBullets = Room.getRoom().getBullets();
        items = Room.getRoom().getItems();
        weapons = Room.getRoom().getWeapons();

        for(CrewmateMulti c : Room.getRoom().getCrewmates())
            hud.addActor(c.getLabel());

        hitList = new ArrayList<>();    //추가

        initItem(); // 아이템 초기화
    }

    //상민
    private void initItem () {
        //상민
        //추가. 벽에 겹치지 않게 아이템 생성
        for(int i = 0 ; i < 12 ; i++){
            boolean check = true;
            ItemGroup item = new ItemGroup(world,
                    new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20),
                    i % 3);
            for (Rectangle rect : blocks) {
                if (item.getX() >= rect.getX() - item.getWidth() && item.getX() <= rect.getX() + rect.getWidth())
                    if (item.getY() >= rect.getY() - item.getHeight() && item.getY() <= rect.getY() + rect.getHeight())
                        check = false;
            }
            if(check){
                items.add(item);}
            else i--;
        }

        //상민
        //추가. 벽에 겹치지 않게 무기 생성
        ArrayList<Weapon.Type> types = new ArrayList<>();
        types.add(Weapon.Type.BLUE);
        types.add(Weapon.Type.GREEN);
        types.add(Weapon.Type.RED);
        for(int i = 0 ; i < 8 ; i++){
            boolean check = true;
            Weapon.Type type = types.get(i % 3);
            Weapon weapon = new Weapon(world,
                    new Vector2((int) (Math.random() * 1560) + 20, (int) (Math.random() * 960) + 20),
                    type);
            for (Rectangle rect : blocks) {
                if (weapon.getX() >= rect.getX() - weapon.getWidth() && weapon.getX() <= rect.getX() + rect.getWidth())
                    if (weapon.getY() >= rect.getY() - weapon.getHeight() && weapon.getY() <= rect.getY() + rect.getHeight()) {
                        check = false;
                    }
            }
            if (check) {
                weapons.add(weapon);
            } else i--;
        }
    }

    public void handleInput (float dt){
        Util.moveInputHandle(myCrewmate, maxSpeed, moveSpeed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && myCrewmate.getAttackDelay() <= 0) {
            Client.getInstance().shoot(myCrewmate.getX(),myCrewmate.getY(),myCrewmate.currentState,myCrewmate.getWeapon());
            attack();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new LobbyScreen(game));

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) isShowMinimap = !isShowMinimap;

        //v로 무기 버리기
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) myCrewmate.setWeapon(Weapon.Type.NORMAL);

        //z로 템줍
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) weaponGet();

    }

    public void weaponGet(){ //추가
        Weapon weapon;
        for(int i = 0; i< weapons.size() ; i++) {
            weapon = weapons.get(i);
            assert weapon != null;

            if (myCrewmate.getX() >= weapon.getX()- myCrewmate.getWidth() && myCrewmate.getX() <= weapon.getX()+weapon.getWidth())
                if (myCrewmate.getY() >= weapon.getY()- myCrewmate.getHeight() && myCrewmate.getY() <= weapon.getY()+weapon.getHeight()) {
                    weapons.remove(i--);
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

        if(radius >= 0) radius -= dt * 3; // 자기장 줄이기
        if(magneticDelay > 0) magneticDelay -= dt; // 자기장에 맞는거 체크

        centerOfMagnetic = new Vector2(1600 - myCrewmate.b2Body.getPosition().x * 2 + NavoGame.V_WIDTH
                , 1280 - myCrewmate.b2Body.getPosition().y * 2 + NavoGame.V_HEIGHT); // 자기장 중간 지점 업데이트

        myCrewmate.update(dt); // 내캐릭터 위치 업데이트
        for(CrewmateMulti crewmateMulti : Room.getRoom().getCrewmates()) crewmateMulti.update(dt); // 크루메이트들 위치 업데이트

        collisionCheck(); // 충돌 체크

        myBullets.removeIf(b -> b.update(dt)); // 내가 쏜 총알 체크
        otherBullets.removeIf(b -> b.update(dt)); // 상대가 쏜 총알 체크
        hitList.removeIf(hit -> hit.update(dt)); // 충돌 이펙트 체크



        gameCam.position.x = myCrewmate.b2Body.getPosition().x;
        gameCam.position.y = myCrewmate.b2Body.getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
    }

    private void collisionCheck(){
        // 자기장 체크
        Vector2 magneticChecker = new Vector2(centerOfMagnetic.x - 400
                , centerOfMagnetic.y - 300); // 자기장이랑 내 위치 비교
        if(magneticChecker.len() >= (radius * 4)){
            if(magneticDelay <= 0){
                myCrewmate.hit(10);
                magneticDelay = 1;
            }
            if(!isMagneticSoundPlay){
                isMagneticSoundPlay = true;
                Sounds.magnetic.play();
            }
        }else{
            if(isMagneticSoundPlay){
                isMagneticSoundPlay = false;
                Sounds.magnetic.pause();
            }
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
            for(CrewmateMulti crewmateMulti : Room.getRoom().getCrewmates()){
                if(!crewmateMulti.owner.equals(myCrewmate.owner)){
                    if (bullet.getX() >= crewmateMulti.getX() - bullet.getWidth() && bullet.getX() <= crewmateMulti.getX() + crewmateMulti.getWidth()){
                        if (bullet.getY() >= crewmateMulti.getY() - bullet.getHeight() && bullet.getY() <= crewmateMulti.getY() + crewmateMulti.getHeight()) {
                            myBullets.remove(i--);
                            //추가. 이펙트 생성
                            hitList.add(new HitEffect(world,
                                    new Vector2((crewmateMulti.getX()-(crewmateMulti.getX()-bullet.getX())/2)-3,
                                            (crewmateMulti.getY()-(crewmateMulti.getY()-bullet.getY())/2)-5)));
                            break;
                        }
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
                    if(bullet.getType().equals(Weapon.Type.RED))
                        myCrewmate.hit(20);
                    else if(bullet.getType().equals(Weapon.Type.GREEN))
                        myCrewmate.hit(15);
                    else if(bullet.getType().equals(Weapon.Type.BLUE))
                        myCrewmate.hit(7);
                    else
                        myCrewmate.hit(10);

                    //추가. 이펙트 생성
                    hitList.add(new HitEffect(world,
                            new Vector2((myCrewmate.getX()-(myCrewmate.getX()-bullet.getX())/2)-3,
                                    (myCrewmate.getY()-(myCrewmate.getY()-bullet.getY())/2)-5)));
                    break;
                }
            }
        }
        //상민
        //추가. 아이템 습득체크
        ItemGroup it;
        for(int i = 0; i < items.size() ; i++){
            it = items.get(i);
            if (myCrewmate.getX() >= it.getX() - myCrewmate.getWidth() && myCrewmate.getX() <= it.getX() + it.getWidth())
                if (myCrewmate.getY() >= it.getY() - myCrewmate.getHeight() && myCrewmate.getY() <= it.getY() + it.getHeight()) {
                    items.remove(i--);
                    if(it.getType()==0)
                        myCrewmate.heal();
                    else if(it.getType()==1)
                        myCrewmate.setMaxSpeed(myCrewmate.getMaxSpeed()+10);
                    else if(it.getType()==2)
                        myCrewmate.setHpSpeed(myCrewmate.getMaxSpeed()-10);
                }
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
//        b2dr.render(world, gameCam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        lineRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        lineRenderer.setColor(Color.WHITE);

        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();

        Room.getRoom().drawCrewmates(NavoGame.getGame().batch, myCrewmate.owner);
        myCrewmate.draw(game.batch);

        if(!isShowMinimap) drawStatus(); // 캐릭터 이름이랑 HP 바 그리는거

        for (Bullet b : myBullets) b.draw(game.batch);
        for (Bullet b : otherBullets) b.draw(game.batch);
        for (HitEffect hit : hitList) //추가
            hit.draw(game.batch);

        for(ItemGroup it : items) //추가
            it.draw(game.batch);

        for(Weapon w : weapons) //추가
            w.draw(game.batch);


        drawMinimap();

        game.batch.end();
        shapeRenderer.end();
        lineRenderer.end();


        //game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.showAliveUser(Room.getRoom().getCrewmates().size());
        hud.position.setText("X : " + myCrewmate.getX() + ", Y : " + myCrewmate.getY());
        if(!isShowMinimap) hud.stage.draw();
    }

    private void drawStatus() {
        shapeRenderer.rect(centerHP.x, centerHP.y, 50 * (myCrewmate.getHP() / myCrewmate.getMaxHP()), 10);

        for (CrewmateMulti c : Room.getRoom().getCrewmates()) {
            if (!c.owner.equals(myCrewmate.owner)) {
                shapeRenderer.rect(centerHP.x + 22f + (c.getX() - myCrewmate.b2Body.getPosition().x) * 2,
                        centerHP.y + 26 + (c.getY() - myCrewmate.b2Body.getPosition().y) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);

                c.getLabel().setPosition((174 + 11f + (c.getX() - myCrewmate.b2Body.getPosition().x)) * 2,
                        (175 + 13 + (c.getY() - myCrewmate.b2Body.getPosition().y)) * 2);
            }
        }
    }

    private void drawMinimap() {
        if(isShowMinimap) {
            game.batch.draw(Images.minimap, myCrewmate.b2Body.getPosition().x - NavoGame.V_WIDTH / 2f // 미니맵
                    , myCrewmate.b2Body.getPosition().y - NavoGame.V_HEIGHT / 2f);
            shapeRenderer.circle(myCrewmate.b2Body.getPosition().x / 2, // 미니맵 그릴 때 내 캐릭터 위치
                    myCrewmate.b2Body.getPosition().y / 2,
                    10
            );
            lineRenderer.circle(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, radius); // 미니맵 그릴 때 자기장
        }else{
            lineRenderer.circle(centerOfMagnetic.x // 미니맵이 안 그려 질 때 자기장
                    ,  centerOfMagnetic.y
                    ,radius*4);
            lineRenderer.line(centerOfMagnetic.x, centerOfMagnetic.y, 400, 320); // 자기장 중간지점과 내 위치 확인선
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

    @Override
    public void dispose () {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
    @Override
    public void resize ( int width, int height){
        gamePort.update(width, height);
    }
    @Override
    public void pause () {
    }
    @Override
    public void show () {
    }
    @Override
    public void resume () {

    }
    @Override
    public void hide () {

    }
}