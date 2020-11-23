package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Buffer.InGameBuffer;
import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Tools.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.scene.chart.ScatterChart;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

@SuppressWarnings("unchecked")
public class Client {
    private static Client instance;
    static final String HOST = System.getProperty("host", "yjpcpa.ddns.net");
    static final int PORT = Integer.parseInt(System.getProperty("port", "1120"));
    Channel channel;

    String owner; //로그인 한 아이디

    //버퍼들
    EventBuffer eventBuffer = EventBuffer.getInstance();
    InGameBuffer inGameBuffer = InGameBuffer.getInstance();
    LoginBuffer loginBuffer = LoginBuffer.getInstance();

    public void setOwner(String owner) { this.owner = owner; }
    public String getOwner() { return this.owner; }

    public Client(){

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new dev.navo.game.Client.ClientInitializer());

            channel = bootstrap.connect(HOST, PORT).sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
        finally {
            System.out.println("설정 종료");
        }
    }

    // 싱글톤 객체
    public static Client getInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }

    //로그인
    public boolean login(String id, String pw) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // LOGIN 1
        body.put("Function", "1");
        body.put("id", id);
        body.put("pw", pw);

        json.put("Body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n"); // writeAndFlush: 내부적으로 채널에 데이터 기록(write) + 기록된 데이터를 서버로 전송(flush)
        JSONObject recvData = loginBuffer.get();

        return recvData.get("Function").equals("1") && recvData.get("result").equals("SUCCESS");
    }
    //회원가입
    public boolean create(String id, String pw, String name, String birth, String phone) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // SIGN UP 2
        body.put("Function", "2");
        body.put("id", id);
        body.put("pw", pw);
        body.put("name", name);
        body.put("birth", birth);
        body.put("phone", phone);

        json.put("Body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();

        return recvData.get("Function").equals("2") && recvData.get("result").equals("SUCCESS");
    }
    //아이디 찾기
    public String idFind(String name, String birth) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // ID FIND 3
        body.put("Function", "3");
        body.put("name", name);
        body.put("birth", birth);

        json.put("Body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();


        if(recvData.get("Function").equals("3") && !recvData.get("result").equals("FAIL"))
            return recvData.get("result").toString();
        else
            return null;
    }
    //패스워드 찾기
    public String pwFind(String id, String name) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // PW FIND 4
        body.put("Function", "4");
        body.put("id", id);
        body.put("name", name);

        json.put("Body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();

        if(recvData.get("Function").equals("4") && !recvData.get("result").equals("FAIL"))
            return recvData.get("result").toString();
        else
            return null;
    }

    // 로그아웃
   /*public void logout(){
        JSONObject json = new JSONObject();
        json.put("Header", "LOGOUT");

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
    }*/

    //처음 게임 입장할 때
    public void enter(JSONObject crewmateJson) {
        JSONObject json = new JSONObject();
        json.put("Header", "Event");

        JSONObject body = new JSONObject();
        body.put("Function", "5");
        body.put("crewmate", crewmateJson);
        json.put("Body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
    }


    // 업데이트 정보 발신
    public void updateSender(final Crewmate2D user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject header = new JSONObject();
                    header.put("Header", "InGame");

                    JSONObject body = new JSONObject();
                    body.put("Function", "UPDATE");
                    body.put("update", user.getCrewmateInitJson());

                    header.put("Body", body);

                    channel.writeAndFlush(header.toJSONString() + "\n");
                    System.out.println("[Client updateSender] : " + header.toJSONString());

                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 업데이트 정보 수신
    public void updateReceiver(final Room room, final World world, final TextureAtlas atlas, final Hud hud)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject recvData = InGameBuffer.getInstance().get();

                try {
                    JSONObject json = JsonParser.createJson(recvData.get("update").toString());
                    System.out.println("[Client updateReceiver Body] : " + json);

                    if(recvData != null) {
                        System.out.println("[Client roomUpdate] : " + json.toJSONString());
                        room.roomUpdate(json, world, atlas, hud);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}