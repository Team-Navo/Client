package dev.navo.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.navo.game.NavoGame;
import dev.navo.game.Sprites.Character.Crewmate2D;

public class Util {
    // 캐릭터 움직임 처리
    public static void moveInputHandle(Crewmate2D crewmate, int maxSpeed, int moveSpeed){
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

    //60FPS 세팅
    public static void frameSet(World world){
        world.step(1/60f, 6, 2);
    }

    //컴포넌트 기본 스킨
    public static final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

}
