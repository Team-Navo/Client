package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import dev.navo.game.Screen.PlayScreen;
import dev.navo.game.Sprites.Bullet;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import dev.navo.game.Sprites.Items.ItemGroup;
import dev.navo.game.Sprites.Weapon;
import dev.navo.game.Tools.Images;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class Room { // 게임 방


    private static Room room;
    int roomCode;

    String superUser;//방장 ID

    ArrayList<CrewmateMulti> crewmates;

    public static Crewmate2D myCrewmate;

    public boolean isStart = false;

    private ArrayList<Bullet> bullets = new ArrayList<>();

    private ArrayList<ItemGroup> items;
    private ArrayList<Weapon> weapons;

    private int size;
    public Room() {
        this.crewmates = new ArrayList<>();
        items = new ArrayList<>();
        weapons = new ArrayList<>();
    }
    // 싱글톤 게터
    public static Room getRoom() {
        if(room == null){
            room = new Room();
        }
        return room;
    }

    public ArrayList<Bullet> getBullets(){
        return bullets;
    }
    public ArrayList<ItemGroup> getItems(){
        return items;
    }
    public ArrayList<Weapon> getWeapons(){
        return weapons;
    }

    public boolean isSuperUser() {
        return myCrewmate.owner.equals(superUser);
    }

    public ArrayList<CrewmateMulti> getCrewmates() { return crewmates; }

    public Crewmate2D getMyCrewmate(){ return myCrewmate; }

    public int getRoomCode() { return roomCode; }

    // 나의 crewmate 기본 정보 생성 (owner, name)
    public static void setMyCrewmate(Crewmate2D crewmate) {
        myCrewmate = crewmate;
        Client.getInstance().enter(crewmate.getCrewmateEnterJson());
    }
    public void makeBullet(JSONObject json) {
        if(!myCrewmate.owner.equals(json.get("owner").toString())) {
            bullets.add(new Bullet(
                    PlayScreen.world, new Vector2(Float.parseFloat(json.get("x").toString())
                    ,Float.parseFloat(json.get("y").toString()))
                    ,Crewmate2D.State.valueOf(json.get("state").toString())
                    , Weapon.Type.valueOf(json.get("weapon").toString()))
            ); //Type.NORMAL을 받아오는 객체로 전달
        }
    }
    public void drawCrewmates(SpriteBatch batch, String user) {
        size = 0;
        for(CrewmateMulti crewmate : crewmates) {
            if(crewmate.getHP() > 0){
                size++;
                if(!user.equals(crewmate.owner){
                    crewmate.draw(batch);
                }
            }
        }
    }

    public void deleteUser(String owner) {
        for(int i=0;i<room.getCrewmates().size();i++) {
            if(crewmates.get(i).getOwner().equals(owner)) {
                System.out.println("Deleted :" +crewmates.get(i).owner);
                crewmates.remove(i);
            }
        }
    }

    public void addCrewmate(JSONObject crewmateJson){
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, crewmateJson);
        crewmates.add(temp);
    }

    // room 안에 있는 crewmate들 업데이트
    public void roomUpdate(JSONObject roomInfo) {
        int size = Integer.parseInt(roomInfo.get("crewmates_size").toString());
        for(int i = 0 ; i < size ; i++ ) {
            JSONObject temp=(JSONObject)roomInfo.get("" + i); //?
            for(CrewmateMulti crewmateMulti : crewmates) {
                if(crewmateMulti.owner.equals(temp.get("owner").toString()))
                    crewmateMulti.updateInfo(temp);
            }
        }
//        if (this.roomCode == Integer.parseInt(roomInfo.get("code").toString())) {
//            JSONObject crewmatesJson = (JSONObject) roomInfo.get("crewmates");
//            int size = Integer.parseInt(crewmatesJson.get("crewmates_size").toString());
//            for (int i = 0; i < size; i++) {
//                JSONObject temp = (JSONObject) crewmatesJson.get("" + i);
//                String owner = temp.get("owner").toString();
//                for (CrewmateMulti crewmate : crewmates) {
//                    if (crewmate.owner.equals(owner)) {
//                        crewmate.updateInfo(temp);
//                    }
//                }
//            }
//        }
    }

    // 나의 crewmate + 접속해 있던 crewmate 생성
    public void roomInit(JSONObject json) {
        this.roomCode = Integer.parseInt(json.get("roomCode").toString());
        JSONObject crewmatesJson = (JSONObject)json.get("Body");
        //this.superUser = json.get("super").toString(); //방장 이름 설정
        int i = 0;
        while(crewmatesJson.get("" + i) != null){
            CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, (JSONObject)crewmatesJson.get("" + i));
            crewmates.add(temp);
            i++;
        }
        this.superUser=json.get("Super").toString();
    }

    // 새로 접속하는 crewmate 생성
    public void roomNewUserEnter(JSONObject json) {
        CrewmateMulti temp = new CrewmateMulti(Images.mainAtlas, json);
        crewmates.add(temp);
    }

    public void changeUserColor(JSONObject json) {
        for (CrewmateMulti crewmateMulti : crewmates) {
            if (crewmateMulti.owner.equals(json.get("owner").toString())) {
                crewmateMulti.setColor(json.get("color").toString());
            }
        }
    }
    public void changeSuper(String superUser) { // 방장 바꾸는 메소드
        this.superUser = superUser;
    }

    public int getSize() {

    }
}