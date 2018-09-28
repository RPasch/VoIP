
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallerThread extends Thread{
    private static String myIP;
    private static String theirIP;
    private static boolean answered;
    
    public static DatagramSocket sendSocket = null;
    public static DatagramSocket receiveSocket = null;
    private static int PORT_NUMBER = 7998;
    public static inCallGui incallgui;
    public static TalkThread talkThread;
    public static ListenerThread listenThread;
    

    public CallerThread(String myIP, String theirIP){
        System.out.println("IN CALLER CONSTRUCTOR");
        this.myIP = myIP;
        this.theirIP = theirIP;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALLER THREAD");
        connectSockets();
        incallgui = new inCallGui();
        incallgui.show();
        
        while(Client.inCall) {}
    
    }
    
    public static void connectSockets(){
        try {
            int TALK_PORT = 7997;
            int LISTEN_PORT = 7997;
            
            System.out.println("************* "+theirIP.substring(0, theirIP.length()-1));
            InetAddress theirInet = InetAddress.getByName(theirIP.substring(0, theirIP.length()-1));
            
            receiveSocket = new DatagramSocket(LISTEN_PORT);
            sendSocket = new DatagramSocket(TALK_PORT);
            
            talkThread = new TalkThread(sendSocket, theirInet, LISTEN_PORT);
            listenThread = new ListenerThread(receiveSocket);
            talkThread.start();
            listenThread.start();
            
            Client.inCall = true;
        } catch (Exception ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   
    
}
