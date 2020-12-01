package dev.navo.game.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.Client.Client;
import dev.navo.game.Client.Room;
import dev.navo.game.NavoGame;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.Sounds;
import dev.navo.game.Tools.Util;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class InfoScreen implements Screen {

    private NavoGame game;
    private Stage stage;

    private TextButton backBtn;

    private Label moveKeyLabel;
    private Label zKeyLabel;
    private Label xKeyLabel;
    private Label vKeyLabel;
    private Label mKeyLabel;

    private String[] fileNames = {
            "core/assets/txt/blue.txt",
            "core/assets/txt/green.txt",
            "core/assets/txt/purple.txt",
            "core/assets/txt/red.txt"
    };

    private ArrayList<Label> explainLabels;

    public InfoScreen(final NavoGame game){
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        explainLabels = new ArrayList<>();

        initComponent();

        try {
            setExplain();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        initActorOnStage();
        btnsAddListener();
    }

    private void initComponent(){
        int keyLabelStartX = -50;
        moveKeyLabel = new Label( "이동", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        moveKeyLabel.setFontScale(0.9f);
        moveKeyLabel.setBounds(208 + keyLabelStartX, 360, 100, 30);

        zKeyLabel = new Label( "무기습득", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        zKeyLabel.setFontScale(0.9f);
        zKeyLabel.setBounds(380 + keyLabelStartX, 360, 100, 30);

        xKeyLabel = new Label( "공격", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        xKeyLabel.setFontScale(0.9f);
        xKeyLabel.setBounds(505 + keyLabelStartX, 360, 100, 30);

        vKeyLabel = new Label( "무기드랍", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        vKeyLabel.setFontScale(0.9f);
        vKeyLabel.setBounds(580 + keyLabelStartX, 360, 100, 30);

        mKeyLabel = new Label( "미니맵", new Label.LabelStyle(FontGenerator.font32, Color.WHITE ));
        mKeyLabel.setFontScale(0.9f);
        mKeyLabel.setBounds(695 + keyLabelStartX, 360, 100, 30);


        for(int i = 0 ; i < fileNames.length ; i++){
            Label temp = new Label( "", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE ));
            temp.setBounds(130, 280 - i * 70, 250, 40);
            temp.setWrap(true);
            explainLabels.add(temp);
        }

        backBtn = new TextButton( "BACK", Util.skin );
        backBtn.setBounds(700, 20, 80, 32);
    }

    private void setExplain() throws FileNotFoundException, UnsupportedEncodingException {
        for(int i = 0 ; i < fileNames.length ; i++){
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileNames[i]), "UTF8"));
            String data;
            StringBuilder buffer = new StringBuilder();

            try{
                while ((data = in.readLine()) != null) {
                    buffer.append(data);
                }
                explainLabels.get(i).setText(buffer);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void initActorOnStage(){
        stage.addActor(moveKeyLabel);
        stage.addActor(zKeyLabel);
        stage.addActor(xKeyLabel);
        stage.addActor(vKeyLabel);
        stage.addActor(mKeyLabel);

        for(Label label : explainLabels)
            stage.addActor(label);

        stage.addActor(backBtn);
    }

    private void btnsAddListener(){

        backBtn.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                backBtn.clear();
                Sounds.click.play();
                game.setScreen(new LobbyScreen(game));
                dispose();
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
        Images.renderBackground(delta, game.batch);

        keysDraw();
        crewmateDraw();
        game.batch.end();
        stage.draw();
    }

    private void keysDraw(){
        int keyStartX = 50;
        int keyStartY = -30;
        game.batch.draw(Images.key[0], 100 + keyStartX, 500 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[1], 100 + keyStartX, 430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[2], 170 + keyStartX, 430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[3], 30 + keyStartX,  430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);

        game.batch.draw(Images.key[4], 300 + keyStartX,  430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[5], 400 + keyStartX,  430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[6], 500 + keyStartX,  430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
        game.batch.draw(Images.key[7], 600 + keyStartX,  430 + keyStartY, 0, 0, 72, 72, 0.8f, 0.8f, 0);
    }

    private void crewmateDraw(){
        game.batch.draw(Images.header[0], 70, 270, 0, 0, 20, 25, 2, 2, 0);
        game.batch.draw(Images.header[2], 70, 200, 0, 0, 20, 25, 2, 2, 0);
        game.batch.draw(Images.header[3], 70, 130, 0, 0, 20, 25, 2, 2, 0);
        game.batch.draw(Images.header[4], 70, 60, 0, 0, 20, 25, 2, 2, 0);
    }
    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height);
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
