package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

import java.io.IOException;


public class LoginScreen implements Screen {

    private static final String userId = "admin";
    private static final String userPw = "admin";
    private boolean isLogin;

    private TextField idField;
    private TextField pwField;

    private Texture background;

    private Label title;
    private Label idLabel;
    private Label pwLabel;

    private TextButton loginBtn;
    private TextButton signUpBtn;
    private TextButton IdPwFindBtn;

    private NavoGame game;
    private Stage stage;

    private Skin skin;
    private Viewport viewport;

    private Client client;

    public LoginScreen(final NavoGame game){
        isLogin = false;
        this.game = game;
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        client = Client.getInstance();
        TextField.TextFieldStyle textFieldStyle = skin.get(TextField.TextFieldStyle.class);

        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        background = new Texture("data/GameBack.png");

        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt"));

        title = new Label( "Navo Ground", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        title.setFontScale(0.8f);
        idField = new TextField("", textFieldStyle);
        pwField = new TextField("", textFieldStyle);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        idLabel = new Label("I   D : ", new Label.LabelStyle(f, Color.WHITE));
        pwLabel = new Label("PW : ", new Label.LabelStyle(f, Color.WHITE));
        loginBtn = new TextButton( "LOG IN", skin );
        signUpBtn = new TextButton( "SIGN UP", skin );
        IdPwFindBtn = new TextButton( "Forgot ID/PW", skin );

        idField.setBounds(125 , 150 , 150 , 25 );
        pwField.setBounds(125, 120 , 150 , 25 );
        idLabel.setBounds(80 , 150 , 45 , 25 );
        pwLabel.setBounds(80 , 120 , 45 , 25 );
        title.setBounds(0 , 185 , 400 , 25 );
        title.setAlignment(Align.center);
        loginBtn.setBounds(135 , 85 , 130 , 25 );
        signUpBtn.setBounds(135 , 50 , 130 , 25 );
        IdPwFindBtn.setBounds(135 , 15 , 130 , 25 );

        loginBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    if( client.login(idField.getText(), pwField.getText()) ){
                        Gdx.graphics.setWindowedMode(800 , 600 );
                        game.setScreen(new LobbyScreen(game));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        signUpBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new SignUpScreen(game));
            }
        });

        IdPwFindBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(new IdPwFindScreen(game));
            }
        });
        stage.addActor(idField);
        stage.addActor(pwField);
        stage.addActor(idLabel);
        stage.addActor(pwLabel);
        stage.addActor(title);
        stage.addActor(loginBtn);
        stage.addActor(signUpBtn);
        stage.addActor(IdPwFindBtn);

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
