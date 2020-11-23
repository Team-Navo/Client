package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

// 게임 방
public class Room {

    int roomCode;
    ArrayList<CrewmateMulti> crewmates;

    public Room(World world, TextureAtlas atlas, JSONObject roomInfo) throws ParseException {
        crewmates = new ArrayList<>();

        JSONObject body = JsonParser.createJson(roomInfo.get("Body").toString());
        this.roomCode = Integer.parseInt(body.get("code").toString());

        JSONObject crewmatesJson = (JSONObject)body.get("crewmates");

        int i = 0;
        while(crewmatesJson.get("" + i) != null){
            crewmates.add(new CrewmateMulti(world, atlas,(JSONObject)crewmatesJson.get("" + i++)));
        }
    }

    public void drawCrewmates(SpriteBatch batch, String user){
        for(CrewmateMulti crewmate : crewmates)
            if(!user.equals(crewmate.owner)) {
                crewmate.draw(batch);
            }
    }

    public ArrayList<CrewmateMulti> getCrewmates(){
        return crewmates;
    }

    public int getRoomCode(){
        return roomCode;
    }

    public void roomUpdate(JSONObject roomInfo, World world, TextureAtlas atlas, Hud hud){

        if(this.roomCode == Integer.parseInt(roomInfo.get("code").toString()) ){
            JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");

            int size = Integer.parseInt(crewmatesJson.get("crewmates_size").toString());

            for(int i = 0 ; i < size ; i++){
                boolean isFine = false;

                JSONObject temp = (JSONObject)crewmatesJson.get("" + i);
                String owner = temp.get("owner").toString();

                for(CrewmateMulti crewmate : crewmates) {
                    if(crewmate.owner.equals(owner)) {
                        crewmate.updateInfo(temp); // 받아온 정보로 초기화
                        isFine = true;
                    }
                }

                if (!isFine)
                    addCrewmate(temp, world, atlas, hud);
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson, World world, TextureAtlas atlas, Hud hud){
        CrewmateMulti temp = new CrewmateMulti(world, atlas, crewmateJson);
        crewmates.add(temp);
        hud.stage.addActor(temp.getLabel());
    }
}
