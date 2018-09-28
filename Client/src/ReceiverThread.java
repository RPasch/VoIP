
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
public class ReceiverThread extends Thread{
    private static String userIPtoCall;
    private boolean answerCall;
    
    static OutputStream outToCaller;
    static DataOutputStream out;
    static InputStream inFromCaller;
    static DataInputStream in;
    static Socket clientCaller;
    public static inCall incall;
    
    public ReceiverThread(String userIPtoCall){
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN RECEIVER THREAD");
        connectSockets(answerCall);
        incall = new inCall();
        incall.show();
    }
    
    public static void connectSockets(boolean answerCall){
        
        try {
            
        } catch (Exception ex) {
            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        Client.inCall = true;

    
    }
    
}
