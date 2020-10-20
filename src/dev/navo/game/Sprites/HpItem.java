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

public class HpItem  extends Sprite {

    private final static Vector2 regionV = new Vector2(1, 12);

    public World world;
    public Body b2Body;


    public HpItem(World world, PlayScreen screen, Vector2 v){
        super(screen.getItemAtlas().findRegion("pill_red"));
        this.world = world;
//        defineHpItem(v);
        setBounds(v.x, v.y, 22, 21);
        setRegion(new TextureRegion(getTexture(), 1, 2, 22 ,21));
    }

//    public void defineHpItem(Vector2 v){
//        BodyDef bDef = new BodyDef();
//        bDef.position.set(v.x,v.y);
//        bDef.type = BodyDef.BodyType.StaticBody;
//        b2Body = world.createBody(bDef);
//
//        FixtureDef fDef = new FixtureDef();
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(11, 11);
//
//        fDef.shape = shape;
//        b2Body.createFixture(fDef);
//    }
    public void update(float dt){
        setPosition(this.getX(), this.getY());
    }
}
