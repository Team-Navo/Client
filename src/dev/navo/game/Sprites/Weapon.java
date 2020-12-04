//package dev.navo.game.Sprites;
//
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.math.Polygon;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.physics.box2d.*;
//
//public class Weapon extends InteractiveTileObject{
//
//    public Weapon(World world, TiledMap map, Rectangle bounds){
//        super(world, map, bounds);
//        BodyDef bDef = new BodyDef();
//        FixtureDef fDef = new FixtureDef();
//        PolygonShape shape = new PolygonShape();
//
//        bDef.type = BodyDef.BodyType.StaticBody;
//        bDef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
//
//        body = world.createBody(bDef);
//
//        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
//        fDef.shape = shape;
//        body.createFixture(fDef);
//    }
//}
package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Tools.FontGenerator;
import dev.navo.game.Tools.Images;

public class Weapon extends Sprite {
    public World world;
    private int type;
    public int getType(){return type;}
    public Weapon(World world, Vector2 v, int type){
        super(Images.laserAtlas.findRegion("laserBlue"));
        //Images.mainAtlas.findRegion("Bullet"));
        this.type = type;
        this.world = world;
        setBounds(v.x, v.y, 14, 14);
        if(type==0)
            setRegion(new TextureRegion(getTexture(), 80,  2, 37, 37));
        else if(type==1)
            setRegion(new TextureRegion(getTexture(), 41,  2, 37, 37));
        else
            setRegion(new TextureRegion(getTexture(), 2,  2, 37, 37));
    }
    public void update(float dt){
        setPosition(this.getX(), this.getY());
    }
}