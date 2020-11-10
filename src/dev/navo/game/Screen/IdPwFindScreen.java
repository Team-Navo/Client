package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;

public class IdPwFindScreen implements Screen {


    // ID : 이름 생년월일
    private Label idFindLabel;
    private Label idNameLabel;
    private Label idBirthLabel;

    private TextField idNameField;
    private TextField idBirthField;

    private TextButton idFindBtn;

    // PW : ID 이름
    private Label pwFindLabel;
    private Label pwIdLabel;
    private Label pwNameLabel;

    private TextField pwIdField;
    private TextField pwNameField;
    private TextButton pwFindBtn;

    private TextButton backBtn;

    private Texture background;

    private NavoGame game;
    private Stage stage;

    private Skin skin;
    private Viewport viewport;

    private Client client;

    public IdPwFindScreen(final NavoGame game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        background = new Texture("data/GameBack.png");
        client = Client.getInstance();
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt"));

        idFindLabel = new Label("Find ID", new Label.LabelStyle(f, Color.WHITE));
        idNameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));
        idBirthLabel = new Label("B I R T H", new Label.LabelStyle(f, Color.WHITE));

        idNameField = new TextField("", skin);
        idBirthField = new TextField("", skin);

        idFindBtn  = new TextButton( "Find ID", skin );

        idFindLabel.setBounds(10, 260, 400, 25);
        idNameLabel.setBounds(20, 230, 50, 25);
        idNameField.setBounds(80, 230, 150, 25);

        idBirthLabel.setBounds(20, 200, 50, 25);
        idBirthField.setBounds(80, 200, 150, 25);

        idFindBtn = new TextButton( "Find ID", skin );
        idFindBtn.setBounds(250, 200, 80, 25);
        idFindBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {

            }
        });

        pwFindLabel = new Label("Find PW", new Label.LabelStyle(f, Color.WHITE));
        pwIdLabel = new Label("I       D", new Label.LabelStyle(f, Color.WHITE));
        pwNameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));

        pwIdField = new TextField("", skin);
        pwNameField = new TextField("", skin);

        pwFindLabel.setBounds(10, 160, 400, 25);
        pwIdLabel.setBounds(20, 130, 50, 25);
        pwIdField.setBounds(80, 130, 150, 25);

        pwNameLabel.setBounds(20, 100, 50, 25);
        pwNameField.setBounds(80, 100, 150, 25);



        pwFindBtn = new TextButton( "Find PW", skin );
        pwFindBtn.setBounds(250, 100, 80, 25);
        pwFindBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {

            }
        });


        backBtn = new TextButton( "BACK", skin );
        backBtn.setBounds(310, 10, 80, 25);
        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });

        stage.addActor(idFindLabel);
        stage.addActor(idNameLabel);
        stage.addActor(idBirthLabel);
        stage.addActor(idNameField);
        stage.addActor(idBirthField);
        stage.addActor(idFindBtn);

        stage.addActor(pwFindLabel);
        stage.addActor(pwIdLabel);
        stage.addActor(pwNameLabel);
        stage.addActor(pwIdField);
        stage.addActor(pwNameField);
        stage.addActor(pwFindBtn);

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
        game.batch.draw(background, 0 , 0 );
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
