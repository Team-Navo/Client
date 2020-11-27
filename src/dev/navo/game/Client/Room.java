package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Tools.Images;
import dev.navo.game.Tools.JsonParser;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class Room { // 게임 방

    private static Room room;

    public static Crewmate2D myCrewmate;

    int roomCode;
    ArrayList<CrewmateMulti> crewmates;

    public Room(){
        this.crewmates = new ArrayList<>();
    }

    public Crewmate2D getMyCrewmate(){
        return myCrewmate;
    }

    // 싱글톤 게터
    public static Room getRoom(){
        if(room == null){
            room = new Room();
        }
        return room;
    }

    public ArrayList<CrewmateMulti> getCrewmates() {
        return crewmates;
    }

    public int getRoomCode() {
        return roomCode;
    }


    public static void setMyCrewmate(Crewmate2D crewmate) {
        myCrewmate = crewmate;
        Client.getInstance().enter(crewmate.getCrewmateInitJson());
    }


    public void drawCrewmates(SpriteBatch batch, String user){
        for(CrewmateMulti crewmate : crewmates){
            if(!user.equals(crewmate.owner)) {
                crewmate.draw(batch);
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson){
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, crewmateJson);
        crewmates.add(temp);
    }

    public void roomUpdate(JSONObject roomInfo){

        if(this.roomCode == Integer.parseInt(roomInfo.get("code").toString()) ){
            JSONObject crewmatesJson = (JSONObject)roomInfo.get("crewmates");

            int size = Integer.parseInt(crewmatesJson.get("crewmates_size").toString());

            for(int i = 0 ; i < size ; i++){
                boolean isFine = false;
                JSONObject temp = (JSONObject)crewmatesJson.get("" + i);
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

    public void roomInit(String roomCode){
        this.roomCode =  Integer.parseInt(roomCode);

        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, Room.getRoom().getMyCrewmate().getCrewmateInitJson());
        crewmates.add(temp);
        System.out.println("Room 93 crewmates : " + getCrewmates());
    }

    public void roomNewUserEnter(JSONObject json) {
        int i = 0;
        while(json.get("" + i) != null) {
            JSONObject crewmate = (JSONObject)json.get("" + i);

            if(crewmate.get("owner").toString() != myCrewmate.owner) {
                CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, crewmate);
                crewmates.add(temp);
            }
            i++;
        }
    }
}