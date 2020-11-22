package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.JsonParser;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressWarnings("unchecked")
public class ClientSocket {

    private static ClientSocket instance;
    private Socket clientSocket;
    BufferedReader in;
    PrintWriter out;

    String owner;

    String serverIPv4 = "yjpcpa.ddns.net";
    int serverPort = 1120;

    //싱글톤 객체 접근자
    public static ClientSocket getInstance(){
        if(instance == null)
            instance = new ClientSocket();
        return instance;
    }

    public ClientSocket() {
        connect();
        streamSetting();
    }    

    //연결 설정
    public void connect() {
        try{
            clientSocket=new Socket(serverIPv4, serverPort);
            clientSocket.setTcpNoDelay(true);
            clientSocket.setSoLinger(false, 0);
            System.out.println("[Client] Connected to server");
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    //입출력 스트림 설정
    public void streamSetting() {
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getOwner() {
        return this.owner;
    }

    //업데이트
    public void update(final Crewmate2D user, final dev.navo.game.Client.Room room, final World world, final TextureAtlas atlas, final Hud hud) {
        new Thread(new Runnable() {
            int i = 0;
            boolean isThread=true;
            @Override
            public void run() {
                while(isThread){
                    JSONObject json = user.getCrewmateInitJson();
                    json.put("Header", "UPDATE");

                    out.println(json.toJSONString());

                    String result = null;
                    try {
                        result = in.readLine();
                        JSONObject roomJson = null;
                        if(result != null)
                            roomJson = JsonParser.createJson(result);
                        if(roomJson != null){
                            System.out.println(roomJson.toJSONString());
                            room.roomUpdate(roomJson, world, atlas, hud);
                            Thread.sleep(75);
                        }
                    } catch (IOException | ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // 로그인
    public boolean login(String id, String pw) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "LOGIN");
        json.put("id", id);
        json.put("pw", pw);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String recvData= in.readLine();
        return recvData.equals("SUCCESS");
    }

    //회원 가입
    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "CREATE");
        json.put("id", id);
        json.put("pw", pw);
        json.put("name", name);
        json.put("birth", birth);
        json.put("phone", phone);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    //아이디 찾기
    public String idFind(String name, String birth) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "ID");
        json.put("name", name);
        json.put("birth", birth);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    //패스워드 찾기
    public String pwFind(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "PW");
        json.put("id", id);
        json.put("name", name);

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    // 로그아웃
    public void logout(){
        JSONObject json = new JSONObject();
        json.put("Header", "LOGOUT");

        System.out.println(json.toJSONString());
        out.println(json.toJSONString());
    }

    //처음 게임 입장할 때
    public JSONObject enter(JSONObject crewmateJson) throws IOException, ParseException {
        JSONObject json = new JSONObject();
        json.put("Header", "Ingame");

        JSONObject body = new JSONObject();
        body.put("Funtion", "ENTER");
        body.put("crewmate", crewmateJson);
        json.put("body", body);


        System.out.println(json.toJSONString());
        out.println(json.toJSONString());

        String result = in.readLine();
        return JsonParser.createJson(result);
    }

}


