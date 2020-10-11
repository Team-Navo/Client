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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;
import dev.navo.game.Scenes.Hud;
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

    private String mapType = "Navo32.tmx";
    private static final int moveSpeed = 10;
    private static final int maxSpeed = 100;

    public PlayScreen(NavoGame game){
        atlas = new TextureAtlas("Crewmate.pack");

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

        new B2WorldCreator(world, map);
        c1 = new Crewmate(world, this, new Vector2(200, 500));
        cList = new ArrayList<>();
        cList.add(c1);
        for(int i = 0 ; i < 5 ; i++){
            Crewmate temp = new Crewmate(world, this, new Vector2((int)(Math.random()*1560) + 20, (int)(Math.random()*960) + 20));
            cList.add(temp);
        }
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


        if(Gdx.input.isTouched()) {
            c1 = cList.get((int)(Math.random() * cList.size()));
        }
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);

        //c1.update(dt);
        for(Crewmate c : cList)
            c.update(dt);

        hud.showMessage("x축 속도 : "+ c1.b2Body.getLinearVelocity().x + ", y축 속도" + c1.b2Body.getLinearVelocity().y);
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
