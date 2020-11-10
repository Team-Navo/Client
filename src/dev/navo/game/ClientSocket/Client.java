package dev.navo.game.ClientSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Client instance;
    private Socket clientSocket;
    BufferedReader in;
    PrintWriter out;

    String serverIPv4 = "127.0.0.1";
    int serverPort = 10002;


//    public static void main(String[] args) {
//        new Client();
//    }

    public static Client getInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }

    public Client() {
        connect();
        streamSetting();
        //dataSend();
        //dataRecv();
    }    

    public void connect() {
        try{
            clientSocket=new Socket(serverIPv4, serverPort);
            System.out.println("[Client] Connected to server");
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public void streamSetting() {
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }


    public void dataRecv() {
        new Thread(new Runnable() {
            boolean isThread = true;
            @Override
            public void run() {
                while(isThread)
                    try {
                        String recvData=in.readLine();
                        if(recvData.equals("/quit"))
                            isThread=false;
                        else
                            System.out.println("Server : " + recvData);
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        try {
                            if(clientSocket != null)
                                clientSocket.close();
                            in.close();
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                        isThread = false;
                    }
            }
        }).start();
    }

    public void dataSend() {
        new Thread(new Runnable() {
            Scanner input=new Scanner(System.in);
            boolean isThread=true;
            @Override
            public void run() {
                while(isThread)
                    try {
                        String sendData=input.nextLine();
                        if(sendData.equals("/quit"))
                            isThread=false;
                        else {
                            //String login = String.format("{\"Header\":%s,\"id\":%s,\"pw\":%s}", header, id, pw);
                            //System.out.println(login);
                            out.println(sendData);
                        }
                    }
                    catch (Exception e) {
                        System.out.println(e.toString());
                        try {
                            if(clientSocket != null)
                                clientSocket.close();
                            out.close();
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                        isThread = false;
                    }
            }
        }).start();
    }
    public boolean login(String id, String pw) throws IOException {
        String header = "\"LOGIN\"";
        String login = String.format("{\"Header\":%s,\"id\":%s,\"pw\":%s}", header, String.format("\"%s\"", id), String.format("\"%s\"", pw));
        System.out.println(login);
        out.println(login);
        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    public boolean create(String id, String pw, String name, String birth, String phone) throws IOException {
        String header = "\"CREATE\"";
        String create = String.format("{\"Header\":%s,\"id\":%s,\"pw\":%s,\"name\":%s,\"birth\":%s,\"phone\":%s}"
                , header
                , String.format("\"%s\"", id)
                , String.format("\"%s\"", pw)
                , String.format("\"%s\"", name)
                , String.format("\"%s\"", birth)
                , String.format("\"%s\"", phone));

        System.out.println(create);
        out.println(create);
        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    public boolean idFind(String name, String birth) throws IOException {
        String header = "\"ID\"";
        String id = String.format("{\"Header\":%s,\"name\":%s,\"birth\":%s}"
                , header
                , String.format("\"%s\"", name)
                , String.format("\"%s\"", birth));

        System.out.println(id);
        out.println(id);
        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }

    public boolean pwFind(String id, String name) throws IOException {
        String header = "\"ID\"";
        String pw = String.format("{\"Header\":%s,\"id\":%s,\"name\":%s}"
                , header
                , String.format("\"%s\"", id)
                , String.format("\"%s\"", name));

        System.out.println(pw);
        out.println(pw);
        String recvData= in.readLine();

        return recvData.equals("SUCCESS");
    }
}


