package dev.navo.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;
import dev.navo.game.Sprites.Crewmate;
import dev.navo.game.Tools.FontGenerator;

public class Hud implements Disposable{
    public Stage stage;
    private Viewport viewport;
//    Label nameLabel;

    int count;
    Label posLabel;
    Table table = new Table();
    //400 x 300 해상도 기준
    public Hud(SpriteBatch sb){
        count = 0;
        viewport = new FitViewport(NavoGame.V_WIDTH, NavoGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        table.top();
        table.setFillParent(true);

        posLabel = new Label("x = 100, y = 200", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.row();
        table.add(posLabel).expandX();

//        nameLabel = new Label("상민이", new Label.LabelStyle(FontGenerator.fontBold16, Color.WHITE));
//        nameLabel.setFontScale(0.6f);
//        nameLabel.setColor(Color.BLACK);
//        nameLabel.setBounds(174, 167,50, 15 );
//        nameLabel.setAlignment(Align.center);

//        stage.addActor(nameLabel);
        stage.addActor(table);
    }

    public void showMessage(String str){
        posLabel.setText(str);
    }

    public void addLabel(Label label){
        stage.addActor(label);
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
