package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class Room { // 게임 방

    public Room() { this.crewmates = new ArrayList<>(); }
    private static Room room;
    int roomCode;

    ArrayList<CrewmateMulti> crewmates;

    public static Crewmate2D myCrewmate;

    // 싱글톤 게터
    public static Room getRoom(){
        if(room == null){
            room = new Room();
        }
        return room;
    }

    public ArrayList<CrewmateMulti> getCrewmates() { return crewmates; }

    public Crewmate2D getMyCrewmate(){ return myCrewmate; }

    public int getRoomCode() { return roomCode; }

    // 나의 crewmate 기본 정보 생성 (owner, name)
    public static void setMyCrewmate(Crewmate2D crewmate) {
        myCrewmate = crewmate;
        Client.getInstance().enter(crewmate.getCrewmateEnterJson());
    }

    public void drawCrewmates(SpriteBatch batch, String user) {
        for(CrewmateMulti crewmate : crewmates) {
            if(!user.equals(crewmate.owner)) {
                crewmate.draw(batch);
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson){
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, crewmateJson);
        crewmates.add(temp);
    }

    // room 안에 있는 crewmate들 업데이트
    public void roomUpdate(JSONObject json) {

        if(this.roomCode == Integer.parseInt(json.get("roomCode").toString()) ){
            JSONObject body = (JSONObject)json.get("Body");

            int size = Integer.parseInt(body.get("crewmates_size").toString()); // crewmate 번호로 반복 -> 나중에 수정

            for(int i = 0 ; i < size ; i++) {
                boolean isFine = false;
                JSONObject temp = (JSONObject)body.get("" + i);
                String owner = temp.get("owner").toString();

                for(CrewmateMulti crewmate : crewmates) {
                    if(crewmate.owner.equals(owner)) {
                        crewmate.updateInfo(temp);
                        isFine = true;
                    }
                }
                if (!isFine)
                    addCrewmate(temp);
            }
        }
    }

    // 나의 crewmate + 접속해 있던 crewmate 생성
    public void roomInit(JSONObject json) {
        this.roomCode = Integer.parseInt(json.get("roomCode").toString());
        JSONObject crewmatesJson = (JSONObject)json.get("Body");

        int i = 0;
        while(crewmatesJson.get("" + i) != null){
            CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, (JSONObject)crewmatesJson.get("" + i));
            crewmates.add(temp);
            i++;
        }
    }

    // 새로 접속하는 crewmate 생성
    public void roomNewUserEnter(JSONObject json) {
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, json);
        crewmates.add(temp);
    }
}