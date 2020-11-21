package dev.navo.game.Buffer;

public class Buffer {
    private String data;
    private String loginData;
    private String inGameData;
    private String eventCareData;
    private boolean empty = true;
    public synchronized String get() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        empty = true;
        notifyAll();
        return data;
    }

    public synchronized void put(String data) {
//        while (!empty) {
//            try {
//                wait();
//            } catch (InterruptedException e) {
//            }
//        }
//        empty = false;
//        try {
//            JSONObject json=JsonParser.createJson(data);
//            switch((String)json.get("Header")) {
//                case "LOGIN":
//                    this.loginData=data;
//                    break;
//                case "inGame":
//                    this.inGameData=data;
//                    break;
//                case "eventCare":
//                    this.eventCareData=data;
//                    break;
//                default:
//                    this.data=data;
//            }
//        } catch(ParseException e) {}
//        finally {
//            notifyAll();
//        }
//        public synchronized void put(String data) {
            while (!empty) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            empty = false;
            this.data=data;
            notifyAll();
//        }
    }
}
