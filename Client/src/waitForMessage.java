/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package rw354_tut1_client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Override
    public void run() {

        try {
            waitForMsg(chat);
        } catch (IOException ex) {
            ChatInterface.connected = false;
            JOptionPane.showMessageDialog(chat,"You are disconnected from the server.");
            
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
                default:
                    String who = anything;
                    String message = Client.receiveMsg();
                    chat.printMsg(message, who);
                    break;
            }

        }
    }

}
