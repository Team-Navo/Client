package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Sounds;
import dev.navo.game.Tools.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class LobbyScreen implements Screen {

    private NavoGame game;
    private Stage stage;

    private Viewport viewport;

    private Label title;

    private TextButton startBtn;
    private TextButton backBtn;

    Client client;

    public LobbyScreen(final NavoGame game){
        this.game = game;
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.client = Client.getInstance();

        initComponent();
        initActorOnStage();
        btnsAddListener();

    }

    private void initComponent(){
        title = new Label( "Navo Ground", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        title.setBounds(0, 500, 800, 40);
        title.setAlignment(Align.center);

        startBtn = new TextButton( "GAME START", Util.skin );
        startBtn.setBounds(300, 200, 200, 32);

        backBtn = new TextButton( "BACK", Util.skin );
        backBtn.setBounds(300, 160, 200, 32);
    }

    private void initActorOnStage(){
        stage.addActor(title);
        stage.addActor(startBtn);
        stage.addActor(backBtn);
    }

    private void btnsAddListener(){
        startBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    JSONObject roomInfo = client.enter(client.getOwner());
                    startBtn.clear();
                    backBtn.clear();
                    Sounds.wait.play(); // 대기실 입장 사운드
                    game.setScreen(new WaitScreen(game, roomInfo));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(400, 300);
                startBtn.clear();
                backBtn.clear();
                Sounds.click.play();
                game.setScreen(new LoginScreen(game));
                client.logout();
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(Images.background, 0, 0);
        game.batch.end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

    }
}
