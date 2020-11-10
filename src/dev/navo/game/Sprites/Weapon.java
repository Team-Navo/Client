package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Weapon extends InteractiveTileObject{

    public Weapon(World world, TiledMap map, Rectangle bounds){
        super(world, map, bounds);
        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

        body = world.createBody(bDef);

        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
        fDef.shape = shape;
        body.createFixture(fDef);
    }
}
