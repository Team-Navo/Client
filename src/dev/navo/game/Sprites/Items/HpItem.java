//package dev.navo.game.Sprites.Items;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.utils.Align;
//import com.badlogic.gdx.utils.Array;
//import dev.navo.game.Screen.PlayScreen;
//import dev.navo.game.Tools.FontGenerator;
//import dev.navo.game.Tools.Images;
//
//public class HpItem  extends Sprite {
//
//    private final static Vector2 regionV = new Vector2(1, 12);
//
//    public World world;
//    public Body b2Body;
//    public HpItem(World world, PlayScreen screen, Vector2 v){
////        super(screen.getItemAtlas().findRegion("pill_red"));
//        super(Images.item.findRegion("pill_red")); //images.item이 안 돼. 왜?
//        this.world = world;
//        setBounds(v.x, v.y, 15, 14);
//        setRegion(new TextureRegion(getTexture(), 1, 2, 22 ,21));
//    }
//
//    public void update(float dt){
//        setPosition(this.getX(), this.getY());
//    }
//}
