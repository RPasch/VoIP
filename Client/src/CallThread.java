/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 18214304
 */
public class CallThread extends Thread{
    private String userIPtoCall;
    
    public CallThread(String userIPtoCall){
        this.userIPtoCall = userIPtoCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALL THREAD");
        connectSockets();
        
    
    }
    
    public static void connectSockets(){
    
    
        
        Client.inCall = true;
    }
    
}
