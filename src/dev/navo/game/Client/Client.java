package dev.navo.game.Client;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import dev.navo.game.Buffer.LoginBuffer;
import dev.navo.game.Scenes.Hud;
import dev.navo.game.Sprites.Character.Crewmate2D;
import dev.navo.game.Sprites.Character.CrewmateMulti;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Client {
    private static Client instance;
    static final String HOST = System.getProperty("host", "yjpcpa.ddns.net");
    static final int PORT = Integer.parseInt(System.getProperty("port", "1120"));
    Channel channel;

    String owner; //로그인 한 아이디

    LoginBuffer loginBuffer = LoginBuffer.getInstance();

    Thread updateSend;
    Thread updateReceive;
    Thread eventHandler;

    boolean inGameThread = false;

    public void setIsInGameThread(boolean is){
        this.inGameThread = is;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public String getOwner() {
        return this.owner;
    }

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
        JSONObject parentJson = new JSONObject();
        JSONObject childJson = new JSONObject();

        // LOGIN 0
        parentJson.put("Header", "Auth");
        parentJson.put("Function", "0");

        childJson.put("id", id);
        childJson.put("pw", pw);

        parentJson.put("Body", childJson);

        System.out.println("[Client] : " + parentJson.toJSONString());
        channel.writeAndFlush(parentJson.toJSONString() + "\r\n");

        JSONObject recvJson = loginBuffer.get();
        JSONObject recvBody = (JSONObject)recvJson.get("Body");

        return recvJson.get("Function").equals("0") && recvBody.get("result").equals("1");
    }

    //회원가입
    public boolean create(String id, String pw, String name, String birth, String phone) {
        JSONObject parentJson = new JSONObject();
        JSONObject childJson = new JSONObject();

        // SIGN UP 2
        parentJson.put("Header", "Auth");
        parentJson.put("Function", "1");

        childJson.put("id", id);
        childJson.put("pw", pw);
        childJson.put("name", name);
        childJson.put("birth", birth);
        childJson.put("phone", phone);

        parentJson.put("Body", childJson);

        System.out.println("[Client] : " + parentJson.toJSONString());
        channel.writeAndFlush(parentJson.toJSONString() + "\r\n");

        JSONObject recvJson = loginBuffer.get();
        JSONObject recvBody = (JSONObject)recvJson.get("Body");

        return recvJson.get("Function").equals("1") && recvBody.get("result").equals("1");
    }

    //아이디 찾기
    public String idFind(String name, String birth) {
        JSONObject parentJson = new JSONObject();
        JSONObject childJson = new JSONObject();

        // ID FIND 3
        parentJson.put("Header", "Auth");
        parentJson.put("Function", "2");

        childJson.put("name", name);
        childJson.put("birth", birth);

        parentJson.put("Body", childJson);

        System.out.println("[Client] : " + parentJson.toJSONString());
        channel.writeAndFlush(parentJson.toJSONString() + "\r\n");

        JSONObject recvJson = loginBuffer.get();
        JSONObject recvBody = (JSONObject)recvJson.get("Body");


        if(recvJson.get("Function").equals("2") && !recvBody.get("result").equals("0"))
            return recvBody.get("result").toString();
        else
            return null;
    }

    //패스워드 찾기
    public String pwFind(String id, String name) {
        JSONObject parentJson = new JSONObject();
        JSONObject childJson = new JSONObject();

        // PW FIND 3
        parentJson.put("Header", "Auth");
        parentJson.put("Function", "3");

        childJson.put("id", id);
        childJson.put("name", name);

        parentJson.put("Body", childJson);

        System.out.println("[Client] : " + parentJson.toJSONString());
        channel.writeAndFlush(parentJson.toJSONString() + "\r\n");

        JSONObject recvJson = loginBuffer.get();
        JSONObject recvBody = (JSONObject)recvJson.get("Body");

        if(recvJson.get("Function").equals("3") && !recvBody.get("result").equals("0"))
            return recvBody.get("result").toString();
        else
            return null;
    }

    //처음 입장
    public void enter(JSONObject childJson) {
        JSONObject parentJson = new JSONObject();

        parentJson.put("Header", "Event");
        parentJson.put("Function", "0");  // ENTER 0
        parentJson.put("Body", childJson);

        channel.writeAndFlush(parentJson.toJSONString() + "\r\n");
    }

    //업데이트 보내기
    public void updateSender(final Crewmate2D user, final Room room) {
        System.out.println("updateSender set");
        updateSend = new Thread(new Runnable() {
            @Override
            public void run() {
                while(inGameThread){
                    JSONObject json = new JSONObject();
                    JSONObject body = new JSONObject();
                    json.put("Header", "Update");

                    // UPDATE 6
                    body.put("Function", "6");
                    body.put("code", room.getRoomCode());
                    body.put("crewmate", user.getCrewmateInitJson());

                    json.put("Body", body);
                    channel.writeAndFlush(json.toJSONString() + "\n");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        inGameThread = false;
                        e.toString();
                    }
                }
            }
        });

        updateSend.start();
        System.out.println("updateSender start");
    }

    /*
    // 업데이트 받기 --> Buffer를 JSON으로 변경
    public void updateReceiver(final Room room, final World world, final TextureAtlas atlas, final Hud hud) {
        updateReceive = new Thread(new Runnable() {
            @Override
            public void run() {
                while(inGameThread){
                    JSONObject roomJson = InGameBuffer.getInstance().get();

                    if(roomJson != null){
                        //System.out.println("UPDATE GET : " + roomJson.toJSONString());
                        room.roomUpdate(roomJson);
                    }
                }
            }
        });
        updateReceive.start();
        System.out.println("updateReceiver start");
    }
     */

    /*
    public void eventHandler(final Room room, final Hud hud) {
        eventHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                while(inGameThread){
                    final JSONObject event = EventBuffer.getInstance().get();
                    if(event != null){
                        System.out.println("Event Handler : " + event);
                        String function = event.get("Function").toString();
                        if(function.equals("5")){
                            EventBuffer.getInstance().put(event);
                            continue;
                        }

                        String owner = event.get("owner").toString();

                        if(owner != null){
                            for(CrewmateMulti multi : room.getCrewmates()){
                                if(multi.owner.equals(owner)){
                                    System.out.println("Event Handler, remove Crewmate " + multi.owner);
                                    room.getCrewmates().remove(multi);
                                    hud.removeActor(multi.getLabel());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
        eventHandler.start();
        System.out.println("eventHandler start");
    }

     */

    public void exit(int code) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Event");

        // EXIT 9
        body.put("Function", "9");
        body.put("owner", this.owner);
        body.put("code", code);

        json.put("Body", body);

        channel.writeAndFlush(json.toJSONString() + "\n");

        updateReceive.stop();
        eventHandler.stop();
        setIsInGameThread(false);
    }

}