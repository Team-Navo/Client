package dev.navo.game.Tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.ArrayList;

public class B2WorldCreator {

    private ArrayList<Rectangle> recList;

    public B2WorldCreator(World world, TiledMap map){ // 벽 만드는 클래스
        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;
        recList = new ArrayList<>();

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){ // Tmx에서 3번째(Object) 가져와서 자바 객체로 생성
                Rectangle rect = ((RectangleMapObject)object).getRectangle();

                recList.add(rect);

                bDef.type = BodyDef.BodyType.StaticBody;
                bDef.position.set(rect.getX() + rect.getWidth() / 2,
                        rect.getY() + rect.getHeight() / 2);

                body = world.createBody(bDef);

                shape.setAsBox(rect.getWidth()/2,
                        rect.getHeight()/2);
                fDef.shape = shape;
                body.createFixture(fDef);
        }
    }
    public ArrayList<Rectangle> getRecList() {
        return recList;
    }
}
