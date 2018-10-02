/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import static java.nio.file.Files.list;
import static java.rmi.Naming.list;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.list;
import java.util.List;
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

    public static List<String> ipList;
    public static List<String> userList;

    ChatInterface chat = null;

    public waitForMessage(ChatInterface chat) {
        this.chat = chat;

    }

    /**
     * Checks if you want to play the voice note and starts the gui, if accepted
     * it will play the audio
     *
     * @params userIPtoCall the user's IP that you want to call
     * @param userNametoCall The user's name that you wish to call
     */
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

    /**
     * This method checks if you are calling or receiving and informs the server
     * It checks if you want to decline or accept the call and starts the
     * related gui
     *
     * @params userIPtoCall the user's IP that you want to call
     * @param userNametoCall The user's name that you wish to call
     */
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
                    CallerThread callThread = new CallerThread(Client.myIP, userIPtoCall);
                    callThread.start();
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
                    CallerThread callThread = new CallerThread(Client.myIP, userIPtoCall);
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
                    receiveVoiceNote(audioData, userFrom);
                    break;
                case '^':
                    System.out.println("in ^^^^^^^");
                    String userNames = Client.receiveMsg();
                    String ipAddresses = Client.receiveMsg();
                    userList = Arrays.asList(userNames.split(","));
                    ipList = Arrays.asList(ipAddresses.split(","));
                    confCallGui.updateList(userList);
                    convertIPtoInet();
                    break;
                case '~':
                    System.out.println("in ~~~~~~");
                    String userNamesExit = Client.receiveMsg();
                    String ipAddressesExit = Client.receiveMsg();
                    userList = Arrays.asList(userNamesExit.split(","));
                    ipList = Arrays.asList(ipAddressesExit.split(","));
                    confCallGui.updateList(userList);
                    convertIPtoInet();
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

    /**
     * Simply converts IP to INet address
     */
    public static void convertIPtoInet() {
        try {
            ArrayList<InetAddress> temp = new ArrayList<>();
            for (int i = 0; i < ipList.size(); i++) {
                temp.add(InetAddress.getByName(ipList.get(i)));
            }
            ConfThread.theirInets = temp;

        } catch (UnknownHostException ex) {
            Logger.getLogger(waitForMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
