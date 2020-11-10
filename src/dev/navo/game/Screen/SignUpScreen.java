package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.ClientSocket.Client;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

import java.io.IOException;

public class SignUpScreen implements Screen {

    private TextField idField;
    private TextField pwField;
    private TextField nameField;
    private TextField birthField;
    private TextField phoneField;
    private TextField resultField;

    private Label singUpLabel;
    private Label idLabel;
    private Label pwLabel;
    private Label nameLabel;
    private Label birthLabel;
    private Label phoneLabel;
    private Label resultLabel;

    private TextButton submitBtn;
    private TextButton backBtn;
    private TextButton resultBtn;
    private Texture background;

    private NavoGame game;
    private Stage stage;

    private Skin skin;
    private Viewport viewport;

    private Client client;

    public SignUpScreen(final NavoGame game){
        this.game = game;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        background = new Texture("data/GameBack.png");
        client = Client.getInstance();
        viewport = new FitViewport(NavoGame.V_WIDTH , NavoGame.V_HEIGHT , new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        BitmapFont f = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt"));


        idField = new TextField("", skin);
        pwField = new TextField("", skin);
        pwField.setPasswordMode(true);
        pwField.setPasswordCharacter('*');
        nameField = new TextField("", skin);
        nameField.getStyle().font = f;
        birthField = new TextField("", skin);
        phoneField = new TextField("", skin);

        singUpLabel = new Label("SIGN UP", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        idLabel = new Label("I          D", new Label.LabelStyle(f, Color.WHITE));
        pwLabel = new Label("P        W", new Label.LabelStyle(f, Color.WHITE));
        nameLabel = new Label("N A M E", new Label.LabelStyle(f, Color.WHITE));
        birthLabel = new Label("B I R T H", new Label.LabelStyle(f, Color.WHITE));
        phoneLabel = new Label("P H O N E", new Label.LabelStyle(f, Color.WHITE));

        singUpLabel.setBounds(0, 240, 400, 25);
        singUpLabel.setAlignment(Align.center);

        int LabelStart = 80;
        int FieldStart = 150;
        idLabel.setBounds(LabelStart, 200, 50, 25);
        idField.setBounds(FieldStart, 200, 150, 25);

        pwLabel.setBounds(LabelStart, 170, 50, 25);
        pwField.setBounds(FieldStart, 170, 150, 25);

        nameLabel.setBounds(LabelStart, 140, 50, 25);
        nameField.setBounds(FieldStart, 140, 150, 25);

        birthLabel.setBounds(LabelStart, 110, 50, 25);
        birthField.setBounds(FieldStart, 110, 150, 25);

        phoneLabel.setBounds(LabelStart, 80, 50, 25);
        phoneField.setBounds(FieldStart, 80, 150, 25);


        resultField = new TextField("", skin);
        resultField.setBounds(40, 30, 320, 240);
        resultField.setDisabled(true);
        resultLabel = new Label("", new Label.LabelStyle(FontGenerator.font32, Color.WHITE));
        resultLabel.setBounds(40, 150, 320, 25);
        resultLabel.setAlignment(Align.center);
        resultBtn = new TextButton( "OK", skin );
        resultBtn.setBounds(160, 50, 80, 25);
        resultField.setVisible(false);
        resultLabel.setVisible(false);
        resultBtn.setVisible(false);

        resultBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                resultField.setVisible(false);
                resultLabel.setVisible(false);
                resultBtn.setVisible(false);


            }
        });

        backBtn = new TextButton( "BACK", skin );
        backBtn.setBounds(210, 40, 80, 25);
        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Gdx.graphics.setWindowedMode(400, 300);
                game.setScreen(new LoginScreen(game));
            }
        });

        submitBtn = new TextButton( "SUBMIT", skin );
        submitBtn.setBounds(110, 40, 80, 25);
        submitBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                try {
                    if (client.create(idField.getText(), pwField.getText(), nameField.getText(), birthField.getText(), phoneField.getText())){
                        resultLabel.setText("회원가입 성공!");
                        resultLabel.setVisible(true);
                        resultField.setVisible(true);
                        resultBtn.setVisible(true);
                    }else{
                        resultLabel.setText("회원가입 실패!");
                        resultLabel.setVisible(true);
                        resultField.setVisible(true);
                        resultBtn.setVisible(true);
                    }
                } catch (IOException e) {
                   e.printStackTrace();
                }
                idField.setText("");
                pwField.setText("");
                nameField.setText("");
                birthField.setText("");
                phoneField.setText("");

            }
        });

        stage.addActor(idField);
        stage.addActor(pwField);
        stage.addActor(nameField);
        stage.addActor(birthField);
        stage.addActor(phoneField);

        stage.addActor(singUpLabel);
        stage.addActor(idLabel);
        stage.addActor(pwLabel);
        stage.addActor(nameLabel);
        stage.addActor(birthLabel);
        stage.addActor(submitBtn);

        stage.addActor(backBtn);
        stage.addActor(phoneLabel);

        stage.addActor(resultField);
        stage.addActor(resultLabel);
        stage.addActor(resultBtn);
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
