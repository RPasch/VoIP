
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

     /**
     * This class starts the gui and connects sockets
     */
public class ReceiverThread extends Thread{
    private static String userIPtoCall;
    
    public static inCallGui incallgui;
    
    public ReceiverThread(String userIPtoCall){
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN RECEIVER THREAD");
        connectSockets();
        incallgui = new inCallGui();
        incallgui.show();
    }
    
    public static void connectSockets(){
        
    }
    
}
