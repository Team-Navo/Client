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

public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map){
        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
                Rectangle rect = ((RectangleMapObject)object).getRectangle();

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
}
