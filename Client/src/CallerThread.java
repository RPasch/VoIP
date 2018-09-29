
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallerThread extends Thread {

    private static String myIP;
    private static String theirIP;
    private static boolean answered;

    public static DatagramSocket sendSocket = null;
    public static DatagramSocket receiveSocket = null;
    //private static int PORT_NUMBER = 7998;
    public static inCallGui incallgui;
    public static TalkThread talkThread;
    public static ListenerThread listenThread;

    public CallerThread(String myIP, String theirIP) {
        System.out.println("IN CALLER CONSTRUCTOR");
        this.myIP = myIP;
        this.theirIP = theirIP;
    }

    @Override
    public void run() {
        System.out.println("IN CALLER THREAD");
        connectSockets();
        incallgui = new inCallGui();
        incallgui.show();

        while (Client.inCall) {
        }

    }

    public static void connectSockets() {
        try {

            System.out.println("In Caller Thread: connectSockets " + theirIP.substring(0, theirIP.length() - 1));
            InetAddress theirInet = InetAddress.getByName(theirIP.substring(0, theirIP.length() - 1));

            receiveSocket = new DatagramSocket(Client.LISTEN_PORT);
            sendSocket = new DatagramSocket(Client.TALK_PORT);

            talkThread = new TalkThread(sendSocket, theirInet, Client.LISTEN_PORT);
            listenThread = new ListenerThread(receiveSocket);
            talkThread.start();
            listenThread.start();

            Client.inCall = true;
            Client.TALK_PORT = Client.TALK_PORT - 2;
            Client.LISTEN_PORT = Client.LISTEN_PORT - 2;
        } catch (Exception ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
