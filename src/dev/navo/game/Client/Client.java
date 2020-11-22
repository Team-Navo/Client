package dev.navo.game.Client;

import dev.navo.game.Buffer.Buffer;
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
    static final String HOST = System.getProperty("host", "yjpcpa.ddns.net");
    static final int PORT = Integer.parseInt(System.getProperty("port", "1120"));
    Channel channel;
    Buffer buffer;
    Buffer inputBuffer;
    String owner; //로그인 한 아이디
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getOwner() {
        return this.owner;
    }
    public Client(){
        buffer = new Buffer();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new dev.navo.game.Client.ClientInitializer(buffer));

            channel = bootstrap.connect(HOST, PORT).sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
        finally {
            System.out.println("설정 종료");
        }
    }
    public static Client getInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }
    // SIGN IN
    public boolean login(String id, String pw) throws IOException {

        JSONObject dupJson = new JSONObject();
        dupJson.put("Header", "1");
        dupJson.put("id", id);
        dupJson.put("pw", pw);

        JSONObject mainJson = new JSONObject();
        mainJson.put("Auth", "1");
        mainJson.put("Body", dupJson.toJSONString());

        System.out.println(mainJson.toJSONString());
        channel.writeAndFlush(mainJson.toJSONString() + "\n");

        String recvData = buffer.get();
        return recvData.equals("SUCCESS");
    }
    // SIGN UP
    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "2");
        json.put("id", id);
        json.put("pw", pw);
        json.put("name", name);
        json.put("birth", birth);
        json.put("phone", phone);
        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        String recvData = buffer.get();
        return recvData.equals("SUCCESS");
    }
    // FIND ID
    public String idFind(String name, String birth) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "3");
        json.put("name", name);
        json.put("birth", birth);
        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        String result = buffer.get();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }
    // FIND PW
    public String pwFind(String id, String name) throws IOException {
        JSONObject json = new JSONObject();
        json.put("Header", "4");
        json.put("id", id);
        json.put("name", name);
        System.out.println(json.toJSONString());
        channel.writeAndFlush(json.toJSONString() + "\n");
        String result = buffer.get();
        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }
}

