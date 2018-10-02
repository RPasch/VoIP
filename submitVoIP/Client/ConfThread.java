
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

    /**
     * @param This thread starts the conference call and calls upon listeners and talkers
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
     /**
     * This method calls the method to connects the sockets of the caller and the receiver by informing the server
     */
    
    @Override
    public void run() {
        System.out.println("IN CALLER THREAD");
        connectSockets();

        while (Client.inConf) {
        }

    }
     /**
     * This method connects the sockets of th caller and the receiver by informing the serve
     */
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
