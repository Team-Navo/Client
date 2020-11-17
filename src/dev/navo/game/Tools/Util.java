package dev.navo.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Sprites.Crewmate2D;

public class Util {

    public static void moveInputHandle(float dt, Crewmate2D crewmate, int maxSpeed, int moveSpeed){
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && crewmate.b2Body.getLinearVelocity().y  < maxSpeed){
            crewmate.b2Body.applyLinearImpulse(new Vector2(0, moveSpeed), crewmate.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)  && crewmate.b2Body.getLinearVelocity().y  > -maxSpeed){
            crewmate.b2Body.applyLinearImpulse(new Vector2(0, -moveSpeed), crewmate.b2Body.getWorldCenter(), true);
        }else if(crewmate.b2Body.getLinearVelocity().y < 0){
            if(crewmate.b2Body.getLinearVelocity().y >= -10)
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x, 0);
            else
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x, crewmate.b2Body.getLinearVelocity().y+10);
        }else if(crewmate.b2Body.getLinearVelocity().y > 0){
            if(crewmate.b2Body.getLinearVelocity().y <= 10)
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x, 0);
            else
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x, crewmate.b2Body.getLinearVelocity().y-10);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && crewmate.b2Body.getLinearVelocity().x  > -maxSpeed){
            crewmate.b2Body.applyLinearImpulse(new Vector2(-moveSpeed, 0), crewmate.b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)  && crewmate.b2Body.getLinearVelocity().x  < maxSpeed){
            crewmate.b2Body.applyLinearImpulse(new Vector2(moveSpeed, 0), crewmate.b2Body.getWorldCenter(), true);
        }else if(crewmate.b2Body.getLinearVelocity().x < 0){
            if(crewmate.b2Body.getLinearVelocity().x >= -10)
                crewmate.b2Body.setLinearVelocity(0, crewmate.b2Body.getLinearVelocity().y);
            else
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x+10, crewmate.b2Body.getLinearVelocity().y);
        }else if(crewmate.b2Body.getLinearVelocity().x > 0){
            if(crewmate.b2Body.getLinearVelocity().x <= 10)
                crewmate.b2Body.setLinearVelocity(0, crewmate.b2Body.getLinearVelocity().y);
            else
                crewmate.b2Body.setLinearVelocity(crewmate.b2Body.getLinearVelocity().x-10, crewmate.b2Body.getLinearVelocity().y);
        }
    }

    public static void frameSet(World world){
        world.step(1/60f, 6, 2);
    }
}
