package dev.navo.game.Client;

import dev.navo.game.Buffer.EventBuffer;
import dev.navo.game.Buffer.InGameBuffer;
import dev.navo.game.Buffer.LoginBuffer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.json.simple.JSONObject;

import java.io.IOException;

@SuppressWarnings("unchecked")
public class Client {
    private static Client instance;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "5001"));
    Channel channel;

    String owner; //로그인 한 아이디

    //버퍼들
    EventBuffer eventBuffer = EventBuffer.getInstance();
    InGameBuffer inGameBuffer = InGameBuffer.getInstance();
    LoginBuffer loginBuffer = LoginBuffer.getInstance();

    public void setOwner(String owner) {
        this.owner = owner;
    }
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
    public boolean login(String id, String pw) throws IOException {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // LOGIN 1
        body.put("Function", "1");
        body.put("id", id);
        body.put("pw", pw);

        json.put("body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();

        return recvData.get("Function").equals("1") && recvData.get("result").equals("SUCCESS");
    }
    //회원가입
    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
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

        json.put("body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();

        return recvData.get("Function").equals("2") && recvData.get("result").equals("SUCCESS");
    }
    //아이디 찾기
    public String idFind(String name, String birth) throws IOException {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // ID FIND 3
        body.put("Function", "3");
        body.put("name", name);
        body.put("birth", birth);

        json.put("body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();


        if(recvData.get("Function").equals("3") && !recvData.get("result").equals("FAIL"))
            return recvData.get("result").toString();
        else
            return null;
    }
    //패스워드 찾기
    public String pwFind(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        json.put("Header", "Auth");

        // PW FIND 4
        body.put("Function", "4");
        body.put("id", id);
        body.put("name", name);

        json.put("body", body);

        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        JSONObject recvData = loginBuffer.get();

        if(recvData.get("Function").equals("4") && !recvData.get("result").equals("FAIL"))
            return recvData.get("result").toString();
        else
            return null;
    }


}

