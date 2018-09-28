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
    private boolean answerCall;
    
    public ReceiverThread(String userIPtoCall , boolean answerCall){
        this.userIPtoCall = userIPtoCall;
        this.answerCall = answerCall;
    }
    
    @Override
    public void run(){
        System.out.println("IN CALL THREAD");
        connectSockets(answerCall);
    
    }
    
    public static void connectSockets(boolean answerCall){
    
        
        Client.inCall = true;
    }
    
}
