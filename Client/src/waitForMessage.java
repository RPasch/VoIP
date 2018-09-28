/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package rw354_tut1_client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
//import static rw354_tut1_client.Client.receiveMsg;

/**
 *
 * @author 18214304
 */
public class waitForMessage extends Thread {

    ChatInterface chat = null;

    public waitForMessage(ChatInterface chat) {
        this.chat = chat;

    }

    public static void createCallThread(String userIPtoCall, String userNametoCall) {
        boolean answered = false;
        if(!Client.madeCall){
            boolean answer = false;
            JDialog.setDefaultLookAndFeelDecorated(true);
            int response = JOptionPane.showConfirmDialog(null,userNametoCall +  " is calling.", "Answer?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                answered = false;
                try {
                    Client.sendMessage("-", userIPtoCall);
                    //ReceiverThread recvThread = new ReceiverThread(userIPtoCall , answered);
                    //recvThread.start();
                } catch (IOException ex) {
                    Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else if (response == JOptionPane.YES_OPTION) {
                try {
                    Client.sendMessage("+", userIPtoCall);
                    
                    answered = true;
                    ReceiverThread recvThread = new ReceiverThread(userIPtoCall);
                    recvThread.start();
                } catch (IOException ex) {
                    Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } else{
            try {
                Client.madeCall = false;
                String response = Client.in.readUTF();
                
                if (response.equals("+")) {
                    CallerThread callThread = new CallerThread(userIPtoCall);
                    callThread.start();
                } else {
                    JOptionPane.showMessageDialog(null, "she just ain't interested bro");
                }
                
            } catch (IOException ex) {
                Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void run() {

        try {
            waitForMsg(chat);
        } catch (IOException ex) {
            ChatInterface.connected = false;
            JOptionPane.showMessageDialog(chat, "You are disconnected from the server.");

        }

    }

    //An infinitw while loop the is looking for incoming messages
    //It checks the code of the message and based on that categorizes the follwoing message
    public static void waitForMsg(ChatInterface chat) throws IOException {

        String list_of_users = Client.receiveMsg();
        chat.addAllusers(list_of_users.substring(1, list_of_users.length()));
        while (true) {

            String anything = Client.receiveMsg();
            switch (anything.charAt(0)) {
                case '&':
                    String connectedUsr = anything.substring(1, anything.length());
                    chat.addAllusers(connectedUsr);
                    break;
                case '#':

                    String disconnectedUsr = Client.receiveMsg();
                    String list_of = Client.receiveMsg().substring(1);
                    chat.removeUsers(disconnectedUsr, list_of);
                    break;
                case '!':
                    String userIPtoCall = Client.receiveMsg();
                    String userNametoCall = Client.receiveMsg();
                    System.out.println("RECEIVED !!!!!!!!!!");
                    createCallThread(userIPtoCall, userNametoCall);
                default:
                    String who = anything;
                    String message = Client.receiveMsg();
                    chat.printMsg(message, who);
                    break;
            }

        }
    }

}
