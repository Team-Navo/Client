package dev.navo.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class Laser {

    //laser physical characteristics
    public float movementSpeed; //world units per second

    //position and dimensions
    public Rectangle boundingBox;

    //graphics
    public TextureRegion textureRegion;
    public Texture texture;

    public Laser(float movementSpeed, float xCentre, float yCentre, float width, float height, Texture texture) {
        this.movementSpeed = movementSpeed;
        this.boundingBox = new Rectangle(xCentre - width/2,yCentre,width,height);
        this.texture = texture;
    }
    public void draw(Batch batch){
        batch.draw(textureRegion,  boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

    }
}
