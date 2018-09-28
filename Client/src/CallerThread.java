
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
    
    public CallerThread(String userIPtoCall){
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALL THREAD");
        connectSockets();
       
        
    
    }
    
    public static void connectSockets(){
        try {
            callerSocket = new ServerSocket(PORT_NUMBER);
            receiverSocket = callerSocket.accept();
            
            System.out.println("sockets made succesfully");
            
            inStream = receiverSocket.getInputStream();
            in = new DataInputStream(inStream);
            
            System.out.println("input streams made succesfully");
            
            answered = in.readBoolean();
            
            System.out.println("ANSWERED ??? " + answered);
            
            Client.inCall = true;
        } catch (IOException ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   
    
}
