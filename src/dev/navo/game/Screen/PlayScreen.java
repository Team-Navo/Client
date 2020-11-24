package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.tools.javac.jvm.Items;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.*;
import dev.navo.game.Tools.B2WorldCreator;

import java.util.ArrayList;

public class PlayScreen implements Screen {
    private NavoGame game;
    private TextureAtlas atlas, item, laser, effect;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private Texture background;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate c1;
    private ArrayList<Crewmate> cList;
    private HitEffect hit;
    private ArrayList<HitEffect> hitList;

    private HpItem h1;
    private ArrayList<HpItem> hList;
    private SpeedItem s1;
    private ArrayList<SpeedItem> sList;
    private TrapItem t1;
    private ArrayList<TrapItem> tList;
    private TrapItem t2;
    private ArrayList<TrapItem> tList2;
    private ItemSample is1;
    private ArrayList<ItemSample> isList;
    private Weapon w1;
    private ArrayList<Weapon> wList;

    private ArrayList<Bullet> bList;
    private ArrayList<RedBullet> rbList;
    private ArrayList<BlueBullet> bbList;
    private ArrayList<GreenBullet> gbList;

    private ArrayList<Rectangle> recList;
    private Vector2 centerHP;

    ShapeRenderer shapeRenderer;

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static final int maxSpeed = 100;
    public Body b2Body;

    public PlayScreen(NavoGame game){
        initAtlas();
        centerHP = new Vector2(375, 325);
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        background = new Texture("data/GameBack.png");
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapType);
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(200,1130, 0); // 200, 1130 = Left Top

        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        B2WorldCreator b2 = new B2WorldCreator(world, map);
        recList = new ArrayList<>();
        recList = b2.getRecList();

        c1 = new Crewmate(world, this, new Vector2(30, 10), "상민이");

        cList = new ArrayList<>();
        cList.add(c1);
        hud.addLabel(c1.getLabel());
        for(int i = 0 ; i < 5 ; i++){
            Crewmate temp = new Crewmate(world, this, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i);
            cList.add(temp);
            hud.addLabel(temp.getLabel());
        }
        hitList = new ArrayList<>();
        bList = new ArrayList<>();
        rbList = new ArrayList<>();
        bbList = new ArrayList<>();
        gbList = new ArrayList<>();

        initItem();
        createSideBlock();

    }

    public void initAtlas(){
        atlas = new TextureAtlas("Image.atlas");
        item = new TextureAtlas("Item.atlas");
        laser = new TextureAtlas("laser.atlas");
        effect = new TextureAtlas("effect.atlas");
    }
    public void initItem(){
        h1 = new HpItem(world, this, new Vector2(0, 0));
        s1 = new SpeedItem(world, this, new Vector2(21, 0));
        t1 = new TrapItem(world, this, new Vector2(42, 0));
        is1 = new ItemSample(world, this, new Vector2(63, 0));
        hList = new ArrayList<>();
        sList = new ArrayList<>();
        tList = new ArrayList<>();
        tList2 = new ArrayList<>();
        isList = new ArrayList<>();
        wList = new ArrayList<>();
        hList.add(h1);
        sList.add(s1);
        tList.add(t1);
        isList.add(is1);

        //벽에 겹치지 않게 Hp약 생성
        for(int i = 0; i<30; i++){
            boolean check = true;
            HpItem hp = new HpItem(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (hp.getX() >= rect.getX()-hp.getWidth() && hp.getX() <= rect.getX()+rect.getWidth())
                    if (hp.getY() >= rect.getY()-hp.getHeight() && hp.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {hList.add(hp);} else i--;
        }

        //벽에 겹치지 않게 Speed약 생성
        for(int i = 0; i<30; i++){
            boolean check = true;
            SpeedItem sp = new SpeedItem(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (sp.getX() >= rect.getX()-sp.getWidth() && sp.getX() <= rect.getX()+rect.getWidth())
                    if (sp.getY() >= rect.getY()-sp.getHeight() && sp.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {sList.add(sp);}
            else i--;
        }

        //벽에 겹치지 않게 Trap약 생성
        for(int i = 0; i<10; i++){
            boolean check = true;
            TrapItem tp = new TrapItem(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (tp.getX() >= rect.getX()-tp.getWidth() && tp.getX() <= rect.getX()+rect.getWidth())
                    if (tp.getY() >= rect.getY()-tp.getHeight() && tp.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {tList.add(tp);}
            else i--;
        }
        for(int i = 0; i<30; i++){
            boolean check = true;
            TrapItem tp2 = new TrapItem(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (tp2.getX() >= rect.getX()-tp2.getWidth() && tp2.getX() <= rect.getX()+rect.getWidth())
                    if (tp2.getY() >= rect.getY()-tp2.getHeight() && tp2.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {tList2.add(tp2);}
            else i--;
        }

        //벽에 겹치지 않게 z습득약 생성
        for(int i = 0; i<30; i++){
            boolean check = true;
            ItemSample is = new ItemSample(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (is.getX() >= rect.getX()-is.getWidth() && is.getX() <= rect.getX()+rect.getWidth())
                    if (is.getY() >= rect.getY()-is.getHeight() && is.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {isList.add(is);}
            else i--;
        }
        for(int i = 0; i<30; i++){
            boolean check = true;
            Weapon wp = new Weapon(world, this, new Vector2((int)(Math.random()*1560)+20, (int)(Math.random()*960)+20));
            for (int j = 0; j < recList.size(); j++){
                Rectangle rect = recList.get(j);
                if (wp.getX() >= rect.getX()-wp.getWidth() && wp.getX() <= rect.getX()+rect.getWidth())
                    if (wp.getY() >= rect.getY()-wp.getHeight() && wp.getY() <= rect.getY()+rect.getHeight())
                        check = false;
            }
            if(check) {wList.add(wp);}
            else i--;
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
    public TextureAtlas getAtlas(){
        return atlas;
    }
    public TextureAtlas getItemAtlas(){
        return item;
    }
    public TextureAtlas getLaserAtlas(){return laser;}
    public TextureAtlas getEffectAtlas(){return effect;}

    @Override
    public void show() {

    }


    public void handleInput(float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && c1.b2Body.getLinearVelocity().y  < c1.getSpeed()){
            c1.b2Body.applyLinearImpulse(new Vector2(0, moveSpeed), c1.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)  && c1.b2Body.getLinearVelocity().y  > -c1.getSpeed()){
            c1.b2Body.applyLinearImpulse(new Vector2(0, -moveSpeed), c1.b2Body.getWorldCenter(), true);
        }else if(c1.b2Body.getLinearVelocity().y < 0){
            if(c1.b2Body.getLinearVelocity().y >= -10)
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x, 0);
            else
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x, c1.b2Body.getLinearVelocity().y+10);
        }else if(c1.b2Body.getLinearVelocity().y > 0){
            if(c1.b2Body.getLinearVelocity().y <= 10)
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x, 0);
            else
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x, c1.b2Body.getLinearVelocity().y-10);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && c1.b2Body.getLinearVelocity().x  > -c1.getSpeed()){
            c1.b2Body.applyLinearImpulse(new Vector2(-moveSpeed, 0), c1.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)  && c1.b2Body.getLinearVelocity().x  < c1.getSpeed()){
            c1.b2Body.applyLinearImpulse(new Vector2(moveSpeed, 0), c1.b2Body.getWorldCenter(), true);
        }else if(c1.b2Body.getLinearVelocity().x < 0){
            if(c1.b2Body.getLinearVelocity().x >= -10)
                c1.b2Body.setLinearVelocity(0, c1.b2Body.getLinearVelocity().y);
            else
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x+10, c1.b2Body.getLinearVelocity().y);
        }else if(c1.b2Body.getLinearVelocity().x > 0){
            if(c1.b2Body.getLinearVelocity().x <= 10)
                c1.b2Body.setLinearVelocity(0, c1.b2Body.getLinearVelocity().y);
            else
                c1.b2Body.setLinearVelocity(c1.b2Body.getLinearVelocity().x-10, c1.b2Body.getLinearVelocity().y);
        }

        //공격!!!!!
        if(Gdx.input.isKeyJustPressed(Input.Keys.X) && c1.getAttackDelay() <= 0){
            attack();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            c1 = cList.get((int)(Math.random() * cList.size()));
        }
        //z로 템줍
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            tp2Check(cList,tList2);
            isCheck(cList,isList);
            wpCheck(cList,wList);
        }
        //v로 무기 버리기
        if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            c1.setWeapon(0);
        }
    }
    private void attack(){
        if(c1.getWeapon()==0){
            bList.add(new Bullet(world, this, new Vector2(c1.getX(), c1.getY()), c1.currentState)); // 총알 생성
            c1.setAttackDelay(0.3f);//공격 딜레이 설정

        }
        else if(c1.getWeapon()==1){
            rbList.add(new RedBullet(world,this, new Vector2(c1.getX(), c1.getY()),c1.currentState));
            c1.setAttackDelay(0.5f);
            c1.shootDown();
            if(c1.getShoot()==0)
                c1.setWeapon(0);
        }
        else if(c1.getWeapon()==2){
            bbList.add(new BlueBullet(world,this, new Vector2(c1.getX(), c1.getY()),c1.currentState));
            c1.setAttackDelay(0.1f);
            c1.shootDown();
            if(c1.getShoot()==0)
                c1.setWeapon(0);
        }
        else if(c1.getWeapon()==3){
            gbList.add(new GreenBullet(world,this, new Vector2(c1.getX(), c1.getY()),c1.currentState));
            c1.setAttackDelay(0.1f);
            c1.shootDown();
            if(c1.getShoot()==0)
                c1.setWeapon(0);
        }
    }

    //습득 체크
    private void isCheck(ArrayList<Crewmate> cList, ArrayList<ItemSample> isList){
        ItemSample is;
        Crewmate crewmate;
        for(int i = 0 ; i< isList.size() ; i++) {
            is = isList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= is.getX()-crewmate.getWidth() && crewmate.getX() <= is.getX()+is.getWidth())
                    if (crewmate.getY() >= is.getY()-crewmate.getHeight() && crewmate.getY() <= is.getY()+is.getHeight()) {
                        isList.remove(i--);
                        crewmate.heal();
                        crewmate.setWeapon(1);
                        crewmate.setShoot(15);
                    }
            }
        }
    }
    private void tp2Check(ArrayList<Crewmate> cList, ArrayList<TrapItem> tList2){
        TrapItem tp2;
        Crewmate crewmate;
        for(int i = 0 ; i< tList2.size() ; i++) {
            tp2 = tList2.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= tp2.getX()-crewmate.getWidth() && crewmate.getX() <= tp2.getX()+tp2.getWidth())
                    if (crewmate.getY() >= tp2.getY()-crewmate.getHeight() && crewmate.getY() <= tp2.getY()+tp2.getHeight()) {
                        tList2.remove(i--);
                        crewmate.setWeapon(3);
                        crewmate.setShoot(40);
                    }
            }
        }
    }
    private void wpCheck(ArrayList<Crewmate> cList, ArrayList<Weapon> wList){
        Weapon wp;
        Crewmate crewmate;
        for(int i = 0 ; i< wList.size() ; i++) {
            wp = wList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= wp.getX()-crewmate.getWidth() && crewmate.getX() <= wp.getX()+wp.getWidth())
                    if (crewmate.getY() >= wp.getY()-crewmate.getHeight() && crewmate.getY() <= wp.getY()+wp.getHeight()) {
                        wList.remove(i--);
                        crewmate.setWeapon(2);
                        crewmate.setShoot(40);
                    }
            }
        }
    }

    public void update(float dt){
        handleInput(dt);
        bulletUpdate();
        itemUpdate();
        for(int i = 0; i<hitList.size(); i++){
            HitEffect hit = hitList.get(i);
            if(hit.getStateTimer() >= hit.getFrameDuration()*4)
                hitList.remove(i);
        }
        world.step(1/60f, 6, 2);

        //c1.update(dt);
        for(int i = 0 ; i < cList.size() ; i++){
            Crewmate temp = cList.get(i);
            if(temp.getHP() == 0){
                world.destroyBody(temp.b2Body);
                hud.removeActor(temp.getLabel());
                cList.remove(i--);
                continue;
            }

            temp.update(dt);
        }
        for(HitEffect hit : hitList){
            hit.update(dt);
        }
        for(Bullet b : bList) {
            b.update(dt);
        }

        for(RedBullet rb : rbList){
            rb.update(dt);
        }
        for(BlueBullet bb : bbList){
            bb.update(dt);
        }
        for(GreenBullet gb : gbList){
            gb.update(dt);
        }

        for(HpItem h : hList){
            h.update(dt);
        }
        for(SpeedItem s : sList){
            s.update(dt);
        }
        for(TrapItem t : tList){
            t.update(dt);
        }
        for(TrapItem t2 : tList2)
            t2.update(dt);

        for(ItemSample i : isList){
            i.update(dt);
        }

        for(Weapon w : wList){
            w.update(dt);
        }

        hud.showMessage("c1.attackDelay"+ c1.getAttackDelay());


        gameCam.position.x = c1.b2Body.getPosition().x;
        gameCam.position.y = c1.b2Body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);
    }

    // 800 x 600 해상도 기준
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background,0 ,0 );
        game.batch.end();

        renderer.render();
        b2dr.render(world, gameCam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();

        shapeRenderer.rect(centerHP.x ,centerHP.y, 50 * (c1.getHP() / c1.getMaxHP()), 10);

        for(Bullet b : bList)
            b.draw(game.batch);

        for(RedBullet rb : rbList)
            rb.draw(game.batch);

        for(BlueBullet bb : bbList)
            bb.draw(game.batch);

        for(GreenBullet gb : gbList)
            gb.draw(game.batch);

        for(HpItem h : hList)
            h.draw(game.batch);

        for(SpeedItem s : sList)
            s.draw(game.batch);

        for(TrapItem t : tList)
            t.draw(game.batch);

        for(TrapItem t2 : tList2)
            t2.draw(game.batch);

        for(ItemSample i : isList)
            i.draw(game.batch);

        for(Weapon w : wList)
            w.draw(game.batch);

        c1.draw(game.batch);
        for(Crewmate c : cList) {
            c.draw(game.batch);
            if(!c.equals(c1)) {
                shapeRenderer.rect(centerHP.x + (c.b2Body.getPosition().x - c1.b2Body.getPosition().x) * 2,
                        centerHP.y + (c.b2Body.getPosition().y - c1.b2Body.getPosition().y) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);

                c.getLabel().setPosition(174 + (c.b2Body.getPosition().x - c1.b2Body.getPosition().x),
                        165 + (c.b2Body.getPosition().y - c1.b2Body.getPosition().y));

            }else{
                c1.getLabel().setPosition(174, 166);
            }
        }
        for(HitEffect hit : hitList)
            hit.draw(game.batch);

        game.batch.end();

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }
    public void itemUpdate(){
        //캐릭과 회복약 충돌 체크
        Crewmate crewmate;
        HpItem hp;
        for(int i = 0 ; i< hList.size() ; i++) {
            hp = hList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= hp.getX()-crewmate.getWidth() && crewmate.getX() <= hp.getX()+hp.getWidth())
                    if (crewmate.getY() >= hp.getY()-crewmate.getHeight() && crewmate.getY() <= hp.getY()+hp.getHeight()) {
                        hList.remove(i--);
                        crewmate.heal();
                    }
            }
        }

        //캐릭과 스피드약 충돌 체크
        SpeedItem sp;
        for(int i = 0 ; i< sList.size() ; i++) {
            sp = sList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= sp.getX()-crewmate.getWidth() && crewmate.getX() <= sp.getX()+sp.getWidth())
                    if (crewmate.getY() >= sp.getY()-crewmate.getHeight() && crewmate.getY() <= sp.getY()+sp.getHeight()) {
                        sList.remove(i--);
                        crewmate.setSpeed(10);
                    }
            }
        }
        //캐릭과 함정약 충돌 체크
        TrapItem tp;
        for(int i = 0 ; i< tList.size() ; i++) {
            tp = tList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if (crewmate.getX() >= tp.getX()-crewmate.getWidth() && crewmate.getX() <= tp.getX()+tp.getWidth())
                    if (crewmate.getY() >= tp.getY()-crewmate.getHeight() && crewmate.getY() <= tp.getY()+tp.getHeight()) {
                        tList.remove(i--);
                        crewmate.hit();
                        crewmate.hit();
                    }
            }
        }
    }
    public void bulletUpdate(){
        //총알 거리 초과 체크
        for(int i = 0 ; i< bList.size() ; i++){
            if(bList.get(i).check()) bList.remove(i--);
        }
        for(int i = 0 ; i< rbList.size() ; i++){
            if(rbList.get(i).check()) rbList.remove(i--);
        }
        for(int i = 0 ; i< bbList.size() ; i++){
            if(bbList.get(i).check()) bbList.remove(i--);
        }
        for(int i = 0; i< gbList.size(); i++){
            if(gbList.get(i).check()) gbList.remove(i--);
        }


        //총알과 벽 충돌체크
        Bullet bullet;
        Rectangle rect;
        for(int i = 0 ; i< bList.size() ; i++) {
            bullet = bList.get(i);
            for (int j = 0; j < recList.size(); j++){
                rect = recList.get(j);
                if (bullet.getX() >= rect.getX()-bullet.getWidth() && bullet.getX() <= rect.getX()+rect.getWidth())
                    if (bullet.getY() >= rect.getY()-bullet.getHeight() && bullet.getY() <= rect.getY()+rect.getHeight())
                        bList.remove(i--);
            }
        }
        RedBullet redBullet;
        for(int i = 0 ; i< rbList.size() ; i++) {
            redBullet = rbList.get(i);
            for (int j = 0; j < recList.size(); j++){
                rect = recList.get(j);
                if (redBullet.getX() >= rect.getX()-redBullet.getWidth() && redBullet.getX() <= rect.getX()+rect.getWidth())
                    if (redBullet.getY() >= rect.getY()-redBullet.getHeight() && redBullet.getY() <= rect.getY()+rect.getHeight())
                        rbList.remove(i--);
            }
        }
        BlueBullet blueBullet;
        for(int i = 0 ; i< bbList.size() ; i++) {
            blueBullet = bbList.get(i);
            for (int j = 0; j < recList.size(); j++){
                rect = recList.get(j);
                if (blueBullet.getX() >= rect.getX()-blueBullet.getWidth() && blueBullet.getX() <= rect.getX()+rect.getWidth())
                    if (blueBullet.getY() >= rect.getY()-blueBullet.getHeight() && blueBullet.getY() <= rect.getY()+rect.getHeight())
                        bbList.remove(i--);
            }
        }
        GreenBullet greenBullet;
        for(int i = 0 ; i< gbList.size() ; i++) {
            greenBullet = gbList.get(i);
            for (int j = 0; j < recList.size(); j++){
                rect = recList.get(j);
                if (greenBullet.getX() >= rect.getX()-greenBullet.getWidth() && greenBullet.getX() <= rect.getX()+rect.getWidth())
                    if (greenBullet.getY() >= rect.getY()-greenBullet.getHeight() && greenBullet.getY() <= rect.getY()+rect.getHeight())
                        gbList.remove(i--);
            }
        }
        //총알과 캐릭터 충돌체크
        Crewmate crewmate;
        for(int i = 0 ; i< bList.size() ; i++) {
            bullet = bList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if(!c1.equals(crewmate)){
                    if (bullet.getX() >= crewmate.getX()-bullet.getWidth() && bullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (bullet.getY() >= crewmate.getY()-bullet.getHeight() && bullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
                            bList.remove(i--);
                            crewmate.hit();
                            hitList.add(new HitEffect(world,this,new Vector2(crewmate.getX(),crewmate.getY())));
                        }
                }
            }
        }
        for(int i = 0 ; i< rbList.size() ; i++) {
            redBullet = rbList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if(!c1.equals(crewmate)){
                    if (redBullet.getX() >= crewmate.getX()-redBullet.getWidth() && redBullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (redBullet.getY() >= crewmate.getY()-redBullet.getHeight() && redBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
                            rbList.remove(i--);
                            crewmate.hit();
                            crewmate.hit();
                            hitList.add(new HitEffect(world,this,new Vector2(crewmate.getX(),crewmate.getY())));
                        }
                }
            }
        }
        for(int i = 0 ; i< bbList.size() ; i++) {
            blueBullet = bbList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if(!c1.equals(crewmate)){
                    if (blueBullet.getX() >= crewmate.getX()-blueBullet.getWidth() && blueBullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (blueBullet.getY() >= crewmate.getY()-blueBullet.getHeight() && blueBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
                            bbList.remove(i--);
                            crewmate.hit();
                            hitList.add(new HitEffect(world,this,new Vector2(crewmate.getX(),crewmate.getY())));
                        }
                }
            }
        }
        for(int i = 0 ; i< gbList.size() ; i++) {
            greenBullet = gbList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if(!c1.equals(crewmate)){
                    if (greenBullet.getX() >= crewmate.getX()-greenBullet.getWidth() && greenBullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (greenBullet.getY() >= crewmate.getY()-greenBullet.getHeight() && greenBullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
                            gbList.remove(i--);
                            crewmate.hit();
                            hitList.add(new HitEffect(world,this,new Vector2(crewmate.getX(),crewmate.getY())));
                        }
                }
            }
        }
    }
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
