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
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.*;
import dev.navo.game.Tools.B2WorldCreator;

import java.util.ArrayList;

public class PlayScreen implements Screen {
    private NavoGame game;
    private TextureAtlas atlas, item;

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

    private HpItem h1;
    private ArrayList<HpItem> hList;
    private SpeedItem s1;
    private ArrayList<SpeedItem> sList;
    private TrapItem t1;
    private ArrayList<TrapItem> tList;

    private ArrayList<Bullet> bList;

    private ArrayList<Rectangle> recList;
    private Vector2 centerHP;

    ShapeRenderer shapeRenderer;

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static final int maxSpeed = 100;

    public PlayScreen(NavoGame game){
        atlas = new TextureAtlas("Image.atlas");
        item = new TextureAtlas("Item.atlas");
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

        c1 = new Crewmate(world, this, new Vector2(200, 100), "상민이");

        cList = new ArrayList<>();
        cList.add(c1);
        hud.addLabel(c1.getLabel());
        for(int i = 0 ; i < 5 ; i++){
            Crewmate temp = new Crewmate(world, this, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i);
            cList.add(temp);
            hud.addLabel(temp.getLabel());
        }
        bList = new ArrayList<>();

        h1 = new HpItem(world, this, new Vector2(0, 0));
        s1 = new SpeedItem(world, this, new Vector2(21, 0));
        t1 = new TrapItem(world, this, new Vector2(42, 0));

        hList = new ArrayList<>();
        sList = new ArrayList<>();
        tList = new ArrayList<>();
        hList.add(h1);
        sList.add(s1);
        tList.add(t1);

        //벽에 겹치지 않게 Hp약 생성
        for(int i = 0; i<100; i++){
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
        for(int i = 0; i<100; i++){
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
        for(int i = 0; i<100; i++){
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
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
    public TextureAtlas getItemAtlas(){
        return item;
    }

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

        if(Gdx.input.isKeyJustPressed(Input.Keys.X) && c1.getAttackDelay() <= 0){
            bList.add(new Bullet(world, this, new Vector2(c1.getX(), c1.getY()), c1.currentState)); // 총알 생성
            c1.setAttackDelay(0.3f);//공격 딜레이 설정
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            c1 = cList.get((int)(Math.random() * cList.size()));
        }
    }

    public void update(float dt){
        handleInput(dt);


        for(int i = 0 ; i< bList.size() ; i++){
            if(bList.get(i).check()) bList.remove(i--);
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
                        }
                }
            }
        }

        //캐릭과 회복약 충돌 체크
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
                    }
            }
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
        for(Bullet b : bList) {
            b.update(dt);
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
        c1.draw(game.batch);
        shapeRenderer.rect(centerHP.x ,centerHP.y, 50 * (c1.getHP() / c1.getMaxHP()), 10);

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
        for(Bullet b : bList)
            b.draw(game.batch);

        for(HpItem h : hList)
            h.draw(game.batch);

        for(SpeedItem s : sList)
            s.draw(game.batch);

        for(TrapItem t : tList)
            t.draw(game.batch);

        game.batch.end();

        shapeRenderer.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
