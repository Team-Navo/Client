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
import dev.navo.game.Client.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Bullet;
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
    private TextureAtlas atlas, item;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate2D myCrewmate;
    private ArrayList<Crewmate2D> crewmates;

    private ArrayList<Bullet> myBullets;
    private ArrayList<Bullet> otherBullets;

    private ArrayList<Rectangle> blocks;
    private HpItem h1;
    private ArrayList<HpItem> hList;
    private SpeedItem s1;
    private ArrayList<SpeedItem> sList;
    private TrapItem t1;
    private ArrayList<TrapItem> tList;
    private ItemSample is1;
    private ArrayList<ItemSample> isList;


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

    public PlayScreen(NavoGame game) {
//        initAtlas();
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
        hud.addActor(myCrewmate.getLabel());

        crewmates = new ArrayList<>();

        recList = b2.getRecList();

        myBullets = new ArrayList<>();
        otherBullets = Room.getRoom().getBullets();
        initItem(); // 아이템 초기화
        createSideBlock(); //

        minimap = new TextureRegion(Images.minimap,
                (int)(myCrewmate.b2Body.getPosition().x) / 4 - gamePort.getScreenWidth() / 16,
                (int)(1280 - myCrewmate.b2Body.getPosition().y) / 4 - gamePort.getScreenHeight() / 16,
                gamePort.getScreenWidth() / 4 ,
                gamePort.getScreenHeight() / 4);
    }

//    private void initAtlas() {
//        atlas = new TextureAtlas("Image.atlas");
//        item = new TextureAtlas("Item.atlas");
//    }

    public void handleInput ( float dt){
        Util.moveInputHandle(myCrewmate, maxSpeed, moveSpeed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && myCrewmate.getAttackDelay() <= 0) {
            myBullets.add(new Bullet(world, this, new Vector2(myCrewmate.getX(), myCrewmate.getY()), myCrewmate.currentState)); // 총알 생성
            myCrewmate.setAttackDelay(0.3f);//공격 딜레이 설정
            // To DO : Client.getInstance().shoot(); 쏘는 방향, x, y, type
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new LobbyScreen(game));

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) isShowMinimap = !isShowMinimap;
            //z로 템줍
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
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

    public void update (float dt){
        handleInput(dt);
        Util.frameSet(world);
        myCrewmate.update(dt);
        for (int i = 0; i < myBullets.size(); i++) if (myBullets.get(i).distanceOverCheck()) myBullets.remove(i--);
        for (int i = 0; i < otherBullets.size(); i++) if (otherBullets.get(i).distanceOverCheck()) otherBullets.remove(i--);

        for(CrewmateMulti crewmateMulti : Room.getRoom().getCrewmates()) {
            crewmateMulti.update(dt);
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
        }for(int i = 0; i < otherBullets.size() ; i++){
            bullet = otherBullets.get(i);
            for (Rectangle block : blocks) {
                if (bullet.getX() >= block.getX() - bullet.getWidth() && bullet.getX() <= block.getX() + block.getWidth())
                    if (bullet.getY() >= block.getY() - bullet.getHeight() && bullet.getY() <= block.getY() + block.getHeight()) {
                        myBullets.remove(i--);
                        break;
                    }
            }
        }

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
                }
            }
        }

        for (int i = 0; i < crewmates.size(); i++) {
            Crewmate2D temp = crewmates.get(i);
            if (temp.getHP() == 0) {
                world.destroyBody(temp.b2Body);
                hud.removeActor(temp.getLabel());
                crewmates.remove(i--);
            }
        }

                                    //캐릭과 회복약 충돌 체크
//            HpItem hp;
//            for (int i = 0; i < hList.size(); i++) {
//                hp = hList.get(i);
//                for (int j = 0; j < cList.size(); j++) {
//                    crewmate = cList.get(j);
//                    if (crewmate.getX() >= hp.getX() - crewmate.getWidth() && crewmate.getX() <= hp.getX() + hp.getWidth())
//                        if (crewmate.getY() >= hp.getY() - crewmate.getHeight() && crewmate.getY() <= hp.getY() + hp.getHeight()) {
//                            hList.remove(i--);
//                            crewmate.heal();
//                        }
//                }
//            }

                                    //캐릭과 스피드약 충돌 체크
//            SpeedItem sp;
//            for (int i = 0; i < sList.size(); i++) {
//                sp = sList.get(i);
//                for (int j = 0; j < cList.size(); j++) {
//                    crewmate = cList.get(j);
//                    if (crewmate.getX() >= sp.getX() - crewmate.getWidth() && crewmate.getX() <= sp.getX() + sp.getWidth())
//                        if (crewmate.getY() >= sp.getY() - crewmate.getHeight() && crewmate.getY() <= sp.getY() + sp.getHeight()) {
//                            sList.remove(i--);
//                            crewmate.setSpeed(10);
//                        }
//                }
//            }

                                    //캐릭과 함정약 충돌 체크
        TrapItem tp;
//        for(int i = 0 ; i< tList.size() ; i++) {
//            tp = tList.get(i);
//            for (int j = 0; j < cList.size(); j++){
//                crewmate = cList.get(j);
//                if (crewmate.getX() >= tp.getX()-crewmate.getWidth() && crewmate.getX() <= tp.getX()+tp.getWidth())
//                    if (crewmate.getY() >= tp.getY()-crewmate.getHeight() && crewmate.getY() <= tp.getY()+tp.getHeight()) {
//                        tList.remove(i--);
//                        crewmate.hit();
//                    }
//            }
//        }

        for (Bullet b : myBullets) b.update(dt);
        for (Bullet b : otherBullets) b.update(dt);
        for (HpItem h : hList) h.update(dt);
        for (SpeedItem s : sList) s.update(dt);
        for (TrapItem t : tList) t.update(dt);
        for (ItemSample i : isList) i.update(dt);
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

        for (HpItem h : hList)
            h.draw(game.batch);

        for (SpeedItem s : sList)
            s.draw(game.batch);

        for (TrapItem t : tList)
            t.draw(game.batch);

        for (ItemSample i : isList)
            i.draw(game.batch);


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