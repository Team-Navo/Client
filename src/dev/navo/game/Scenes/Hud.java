package dev.navo.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;

public class Hud implements Disposable{
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label navoLabel;

    Label posLabel;
    Table table = new Table();

    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        navoLabel = new Label("Navo", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        posLabel = new Label("x = 100, y = 200", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        //table.add(navoLabel).expandX().padTop(10);
        //table.add(worldLabel).expandX().padTop(10);
        //table.add(timeLabel).expandX().padTop(10);
        //table.row();
       // table.add(scoreLabel).expandX();
        //table.add(levelLabel).expandX();
       // table.add(countdownLabel).expandX();
        table.row();
        table.add(posLabel).expandX();

        stage.addActor(table);
    }

    public void showMessage(String str){
        posLabel.setText(str);
        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
