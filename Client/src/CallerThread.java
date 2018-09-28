
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 18214304
 */
public class CallerThread extends Thread{
    private String userIPtoCall;
    private boolean answered;
    public CallerThread(String userIPtoCall){
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALL THREAD");
        connectSockets(answered);
       
        
    
    }
    
    public static void connectSockets(boolean isCaller){
       
        
        Client.inCall = true;
    }
    
   
    
}
