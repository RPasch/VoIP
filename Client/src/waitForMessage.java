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

    public static void receiveVoiceNote(byte[] audioData, String userFrom) {
        JDialog.setDefaultLookAndFeelDecorated(true);
        System.out.println("Received a VN from " + userFrom);
        int response = JOptionPane.showConfirmDialog(null, "Play?", "Received a VN from " + userFrom,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            try {

            } catch (Exception ex) {
                Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (response == JOptionPane.YES_OPTION) {
            RecordAndPlay RaP = new RecordAndPlay(1);
            RaP.playAudio(audioData);
        }

    }

    public static void createCallThread(String userIPtoCall, String userNametoCall) {
        boolean answered = false;
        if (!Client.madeCall) {
            boolean answer = false;
            JDialog.setDefaultLookAndFeelDecorated(true);
            int response = JOptionPane.showConfirmDialog(null, userNametoCall + " is calling.", "Answer?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                answered = false;
                try {
                    Client.sendMessage("-", userNametoCall);
                    //ReceiverThread recvThread = new ReceiverThread(userIPtoCall , answered);
                    //recvThread.start();
                } catch (Exception ex) {
                    Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (response == JOptionPane.YES_OPTION) {
                try {

                    Client.sendMessage("+", userNametoCall);

                    System.out.println("I JUST SENT TO THIS IP ::: " + userIPtoCall);

                    answered = true;
                    ReceiverThread recvThread = new ReceiverThread(userIPtoCall);
                    recvThread.start();
                } catch (Exception ex) {
                    Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else {
            try {
                Client.madeCall = false;
                String response = Client.receiveMsg();

                System.out.println("RESPONSE ::: " + response);

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
            System.err.println(ex);

        }

    }

    //An infinitw while loop the is looking for incoming messages
    //It checks the code of the message and based on that categorizes the follwoing message
    public static void waitForMsg(ChatInterface chat) throws IOException {

        String list_of_users = Client.receiveMsg();
        chat.addAllusers(list_of_users.substring(1, list_of_users.length()));
        while (true) {

            String anything = Client.receiveMsg();
            System.out.println(anything + "     ++++++++++++++++++++++++++++++++++++++++++++++++++");

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
                    break;
                case '*':
                    System.out.println("in asteriks");
                    String userFrom = Client.receiveMsg();
                    System.out.println(userFrom + "userFrom");
                    byte[] audioData = Client.receiveAudioData();
//                    String weird = Client.receiveMsg();
//                    String weird1 = Client.receiveMsg();
//                    String weird2 = Client.receiveMsg();
//                    System.out.println(weird + " weird");
//                    System.out.println(weird1 + " weird1");
//                    System.out.println(weird2 + " weird2");
//                    System.out.println("RECEIVED !!!!!!!!!!");
//                    String weird11 = Client.receiveMsg();
//                    String weird12 = Client.receiveMsg();
//                    String weird22 = Client.receiveMsg();
//                    System.out.println(weird11 + " weird");
//                    System.out.println(weird12 + " weird1");
//                    System.out.println(weird22 + " weird2");
//                    String weird23 = Client.receiveMsg();
//                    String weird13 = Client.receiveMsg();
//                    String weird33 = Client.receiveMsg();
//                    System.out.println(weird23 + " weird");
//                    System.out.println(weird13 + " weird1");
//                    System.out.println(weird33 + " weird2");
                    receiveVoiceNote(audioData, userFrom);
                    break;
                default:
                    System.out.println("in defualt");
                    String who = anything;
                    String message = Client.receiveMsg();
                    chat.printMsg(message, who);
                    break;
            }

        }
    }

}
