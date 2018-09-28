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
    private String userIPtoCall;
    private boolean isCaller;
    public ReceiverThread(String userIPtoCall , boolean isCaller){
        this.userIPtoCall = userIPtoCall;
        this.isCaller = isCaller;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALL THREAD");
        connectSockets(isCaller);
       
        
    
    }
    
    public static void connectSockets(boolean isCaller){
        if(isCaller){
            
               
           } else if(!isCaller){


           }
    
        
        Client.inCall = true;
    }
    
}
