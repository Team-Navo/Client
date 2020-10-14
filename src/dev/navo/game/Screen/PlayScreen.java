package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Bullet;
import dev.navo.game.Sprites.Crewmate;
import dev.navo.game.Tools.B2WorldCreator;

import java.util.ArrayList;

public class PlayScreen implements Screen {
    private NavoGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Crewmate c1;
    private ArrayList<Crewmate> cList;

    private ArrayList<Bullet> bList;

    private ArrayList<Rectangle> recList;


    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static final int maxSpeed = 100;

    public PlayScreen(NavoGame game){
        atlas = new TextureAtlas("Image.atlas");

        this.game = game;
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

        c1 = new Crewmate(world, this, new Vector2(200, 500));
        cList = new ArrayList<>();
        cList.add(c1);
        for(int i = 0 ; i < 5 ; i++){
            Crewmate temp = new Crewmate(world, this, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20));
            cList.add(temp);
        }
        bList = new ArrayList<>();

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && c1.b2Body.getLinearVelocity().y  < maxSpeed){
            c1.b2Body.applyLinearImpulse(new Vector2(0, moveSpeed), c1.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)  && c1.b2Body.getLinearVelocity().y  > -maxSpeed){
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

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && c1.b2Body.getLinearVelocity().x  > -maxSpeed){
            c1.b2Body.applyLinearImpulse(new Vector2(-moveSpeed, 0), c1.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)  && c1.b2Body.getLinearVelocity().x  < maxSpeed){
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

        if(Gdx.input.isTouched()) {
            c1 = cList.get((int)(Math.random() * cList.size()));
        }
    }

    public void update(float dt){
        handleInput(dt);


        for(int i = 0 ; i< bList.size() ; i++){
            if(bList.get(i).check()) bList.remove(i--);
        }

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

        Crewmate crewmate;
        for(int i = 0 ; i< bList.size() ; i++) {
            bullet = bList.get(i);
            for (int j = 0; j < cList.size(); j++){
                crewmate = cList.get(j);
                if(!c1.equals(crewmate)){
                    if (bullet.getX() >= crewmate.getX()-bullet.getWidth() && bullet.getX() <= crewmate.getX()+crewmate.getWidth())
                        if (bullet.getY() >= crewmate.getY()-bullet.getHeight() && bullet.getY() <= crewmate.getY()+crewmate.getHeight())
                            bList.remove(i--);
                }
            }
        }

        world.step(1/60f, 6, 2);

        //c1.update(dt);
        for(Crewmate c : cList) {
            c.update(dt);
        }
        for(Bullet b : bList) {
            b.update(dt);
        }


        hud.showMessage("c1.attackDelay"+ c1.getAttackDelay());
        gameCam.position.x = c1.b2Body.getPosition().x;
        gameCam.position.y = c1.b2Body.getPosition().y;

        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);


        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        c1.draw(game.batch);
        for(Crewmate c : cList)
            c.draw(game.batch);
        for(Bullet b : bList)
            b.draw(game.batch);
        game.batch.end();

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
