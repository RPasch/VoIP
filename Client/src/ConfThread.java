
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 18214304
 */
public class ConfThread extends Thread {

    public List<String> userList;
    public List<String> ipList;

    public static DatagramSocket sendSocket = null;
    public static DatagramSocket receiveSocket = null;
    public static ConfTalkThread ConfTalkThread;
    public static ConfListenThread ConfListenThread;
    public static ArrayList<InetAddress> theirInets = new ArrayList<>();

    public ConfThread() {
        this.userList = waitForMessage.userList;
        this.ipList = waitForMessage.ipList;
    }

    @Override
    public void run() {
        System.out.println("IN CALLER THREAD");
        connectSockets();

        while (Client.inConf) {
        }

    }

    public static void connectSockets() {
        try {

            receiveSocket = new DatagramSocket(Client.LISTEN_PORT);
            sendSocket = new DatagramSocket(Client.TALK_PORT);

            ConfTalkThread = new ConfTalkThread(sendSocket, Client.LISTEN_PORT);
            ConfListenThread = new ConfListenThread(receiveSocket);
            ConfTalkThread.start();
            ConfListenThread.start();

            Client.inCall = true;
            Client.TALK_PORT = Client.TALK_PORT - 2;
            Client.LISTEN_PORT = Client.LISTEN_PORT - 2;
        } catch (Exception ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
