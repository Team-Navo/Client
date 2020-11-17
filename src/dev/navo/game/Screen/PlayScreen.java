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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Bullet;
import dev.navo.game.Sprites.Crewmate2D;
import dev.navo.game.Tools.B2WorldCreator;
import dev.navo.game.Tools.Util;

import java.util.ArrayList;

public class PlayScreen implements Screen {
    private NavoGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private Texture background;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate2D myCrewmate;
    private ArrayList<Crewmate2D> crewmates;

    private ArrayList<Bullet> bullets;

    private ArrayList<Rectangle> blocks;
    private Vector2 centerHP;

    ShapeRenderer shapeRenderer;

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static int maxSpeed = 80;
    //private static final int maxSpeed = 100;

    public PlayScreen(NavoGame game){
        atlas = new TextureAtlas("Image.atlas");
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
        blocks = new ArrayList<>();
        blocks = b2.getRecList();

        myCrewmate = new Crewmate2D(world, atlas, new Vector2(200, 500), "상민이", "Purple", Client.getInstance().getOwner());

        crewmates = new ArrayList<>();
        crewmates.add(myCrewmate);
        hud.addLabel(myCrewmate.getLabel());
        for(int i = 1 ; i <= 5 ; i++){
            Crewmate2D temp = new Crewmate2D(world, atlas, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i,"Red", "temp");
            crewmates.add(temp);
            hud.addLabel(temp.getLabel());
        }
        for(int i = 6 ; i <= 10 ; i++){
            Crewmate2D temp = new Crewmate2D(world, atlas, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i,"Blue", "temp");
            crewmates.add(temp);
            hud.addLabel(temp.getLabel());
        }
        for(int i = 11 ; i <= 15 ; i++){
            Crewmate2D temp = new Crewmate2D(world, atlas, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i,"Green", "temp");
            crewmates.add(temp);
            hud.addLabel(temp.getLabel());
        }
        for(int i = 16 ; i <= 20 ; i++){
            Crewmate2D temp = new Crewmate2D(world, atlas, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i,"Gray", "temp");
            crewmates.add(temp);
            hud.addLabel(temp.getLabel());
        }
        for(int i = 21 ; i <= 25 ; i++){
            Crewmate2D temp = new Crewmate2D(world, atlas, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20), "상민이" + i,"Purple", "temp");
            crewmates.add(temp);
            hud.addLabel(temp.getLabel());
        }
        bullets = new ArrayList<>();

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        Util.moveInputHandle(dt, myCrewmate, maxSpeed, moveSpeed);

        if(Gdx.input.isKeyJustPressed(Input.Keys.X) && myCrewmate.getAttackDelay() <= 0){
            bullets.add(new Bullet(world, this, new Vector2(myCrewmate.getX(), myCrewmate.getY()), myCrewmate.currentState)); // 총알 생성
            myCrewmate.setAttackDelay(0.3f);//공격 딜레이 설정
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            myCrewmate = crewmates.get((int)(Math.random() * crewmates.size()));
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new LobbyScreen(game));
        }
    }

    public void update(float dt){
        handleInput(dt);
        Util.frameSet(world);

        for(int i = 0; i< bullets.size() ; i++){
            if(bullets.get(i).check()) bullets.remove(i--);
        }

        Bullet bullet;
        Rectangle rect;
        for(int i = 0; i< bullets.size() ; i++) {
            bullet = bullets.get(i);
            for (int j = 0; j < blocks.size(); j++){
                rect = blocks.get(j);
                if (bullet.getX() >= rect.getX()-bullet.getWidth() && bullet.getX() <= rect.getX()+rect.getWidth())
                    if (bullet.getY() >= rect.getY()-bullet.getHeight() && bullet.getY() <= rect.getY()+rect.getHeight())
                        bullets.remove(i--);
            }
        }

        Crewmate2D crewmate;
        for(int i = 0; i< bullets.size() ; i++) {
            bullet = bullets.get(i);
            for (int j = 0; j < crewmates.size(); j++){
                crewmate = crewmates.get(j);
                if(!myCrewmate.equals(crewmate)){
                    if (bullet.getX() >= crewmate.getX()-bullet.getWidth() && bullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (bullet.getY() >= crewmate.getY()-bullet.getHeight() && bullet.getY() <= crewmate.getY()+crewmate.getHeight()) {
                            bullets.remove(i--);
                            crewmate.hit();
                        }
                }
            }
        }

        //c1.update(dt);
        for(int i = 0; i < crewmates.size() ; i++){
            Crewmate2D temp = crewmates.get(i);
            if(temp.getHP() == 0){
                world.destroyBody(temp.b2Body);
                hud.removeActor(temp.getLabel());
                crewmates.remove(i--);
                continue;
            }
            temp.update(dt);
        }
        for(Bullet b : bullets) {
            b.update(dt);
        }

        hud.showMessage("c1.velocity"+ myCrewmate.b2Body.getLinearVelocity().toString());


        gameCam.position.x = myCrewmate.b2Body.getPosition().x;
        gameCam.position.y = myCrewmate.b2Body.getPosition().y;
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
        myCrewmate.draw(game.batch);
        shapeRenderer.rect(centerHP.x ,centerHP.y, 50 * (myCrewmate.getHP() / myCrewmate.getMaxHP()), 10);

        for(Crewmate2D c : crewmates) {
            c.draw(game.batch);
            if(!c.equals(myCrewmate)) {
                shapeRenderer.rect(centerHP.x + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x) * 2,
                        centerHP.y + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y) * 2, 50 * (c.getHP() / c.getMaxHP()), 10);

                c.getLabel().setPosition(174 + (c.b2Body.getPosition().x - myCrewmate.b2Body.getPosition().x),
                        165 + (c.b2Body.getPosition().y - myCrewmate.b2Body.getPosition().y));

            }else{
                myCrewmate.getLabel().setPosition(174, 166);
            }
        }
        for(Bullet b : bullets)
            b.draw(game.batch);


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
