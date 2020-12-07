package dev.navo.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;
import dev.navo.game.Tools.FontGenerator;

public class Hud implements Disposable{
    public Stage stage;
    private Viewport viewport;
//    Label nameLabel;

    int count;
    public Label alive;
    public Label aliveUser;

    public Hud(SpriteBatch sb){
        count = 0;
        viewport = new FitViewport(NavoGame.V_WIDTH * 2, NavoGame.V_HEIGHT * 2, new OrthographicCamera());
        stage = new Stage(viewport, sb);


        alive = new Label("ALIVE", new Label.LabelStyle(FontGenerator.fontBold16, Color.BLACK));
        alive.setBounds(720, 610, 100, 30);
        alive.setAlignment(Align.center);

        aliveUser = new Label("1", new Label.LabelStyle(FontGenerator.font32, Color.BLACK));
        aliveUser.setBounds(720, 583, 100, 40);
        aliveUser.setAlignment(Align.center);

        stage.addActor(alive);
        stage.addActor(aliveUser);
    }

    public void addActor(Actor actor){
        stage.addActor(actor);
    }

    public <T> void removeActor(T actor){
        for(Actor temp : stage.getActors()){
            if(temp.equals(actor)){
                temp.remove();
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
