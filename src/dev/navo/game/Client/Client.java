package dev.navo.game.Client;

import dev.navo.game.Buffer.Buffer;
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
    Buffer buffer;
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
    public boolean login(String id, String pw) {

        JSONObject body = new JSONObject();
        body.put("Header", "1");
        body.put("id", id);
        body.put("pw", pw);

        JSONObject header = new JSONObject();
        header.put("Auth", "1");
        header.put("Body", body.toJSONString());

        System.out.println(header.toJSONString());
        channel.writeAndFlush(header.toJSONString() + "\n");

        String recvData = buffer.get();
        return recvData.equals("SUCCESS");
    }

    // SIGN UP
    public boolean create(String id, String pw, String name, String birth, String phone) {

        JSONObject body = new JSONObject();
        body.put("Header", "2");
        body.put("id", id);
        body.put("pw", pw);
        body.put("name", name);
        body.put("birth", birth);
        body.put("phone", phone);

        JSONObject header = new JSONObject();
        header.put("Auth", "2");
        header.put("Body", body.toJSONString());

        System.out.println(header.toJSONString());
        channel.writeAndFlush(header.toJSONString() + "\n");

        String recvData = buffer.get();
        return recvData.equals("SUCCESS");
    }

    // FIND ID
    public String idFind(String name, String birth) {

        JSONObject body = new JSONObject();
        body.put("Header", "3");
        body.put("name", name);
        body.put("birth", birth);

        JSONObject header = new JSONObject();
        header.put("Auth", "3");
        header.put("Body", header.toJSONString());

        System.out.println(header.toJSONString());
        channel.writeAndFlush(header.toJSONString() + "\n");

        String result = buffer.get();

        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }

    // FIND PW
    public String pwFind(String id, String name) {

        JSONObject json = new JSONObject();
        json.put("Header", "4");
        json.put("id", id);
        json.put("name", name);

        JSONObject header = new JSONObject();
        header.put("Auth", "4");
        header.put("Body", header.toJSONString());

        System.out.println(header.toJSONString());
        channel.writeAndFlush(header.toJSONString() + "\n");

        String result = buffer.get();

        if(result.equals("FAIL"))
            return null;
        else
            return result;
    }
}
