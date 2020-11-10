package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

public class LobbyScreen implements Screen {

    private NavoGame game;
    private Stage stage;

    private Skin skin;
    private Viewport viewport;

    private Texture background;

    private Label title;

    private TextButton startBtn;
    private TextButton backBtn;

    public LobbyScreen(final NavoGame game){
        this.game = game;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        background = new Texture("data/GameBack.png");

        title = new Label( "Navo Ground", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        title.setBounds(0, 500, 800, 40);
        title.setAlignment(Align.center);

        startBtn = new TextButton( "GAME START", skin );
        startBtn.setBounds(300, 200, 200, 32);

        backBtn = new TextButton( "BACK", skin );
        backBtn.setBounds(300, 160, 200, 32);

        startBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(400, 300);
                game.setScreen(new LoginScreen(game));
            }
        });
        stage.addActor(title);
        stage.addActor(startBtn);
        stage.addActor(backBtn);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
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
