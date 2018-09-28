
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallerThread extends Thread{
    private String userIPtoCall;
    private static boolean answered;
    
    public static ServerSocket callerSocket = null;
    public static Socket receiverSocket = null;
    private static int PORT_NUMBER = 7998;
    public static InputStream inStream;
    public static DataInputStream in;
    public static inCallGui incallgui;

    public CallerThread(String userIPtoCall){
        System.out.println("IN CALLER CONSTRUCTOR");
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALLER THREAD");
        connectSockets();
        incallgui = new inCallGui();
        incallgui.show();
        
    
    }
    
    public static void connectSockets(){
        try {

            
            Client.inCall = true;
        } catch (Exception ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   
    
}
