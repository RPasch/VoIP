//package rw354_tut1_server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHandler implements Runnable {

    private String username;
    private Socket clientSocket;
    InputStream inFromClient;
    DataInputStream in;
    
    public SocketHandler(String username, Socket clientSocket) {
        this.username = username;
        this.clientSocket = clientSocket;
    }
    
    

    @Override
    public void run() {
        System.out.println("new client thread created");        
        try {
            inFromClient = clientSocket.getInputStream(); 
            in = new DataInputStream(inFromClient);
        } catch (Exception e) {
            System.out.println("could not open stream for this client "+e);
        }
        
        while(true){
            try {
                String toUser = in.readUTF();
                String message = in.readUTF();
                System.out.println("+++++ toUser ::: "+toUser);
                System.out.println("----- message ::: "+message);
                
                if (toUser.equals("All")) {
                    if (message.equals("*")) {
                        int len = in.readInt();
                        byte[] voicenote = new byte[len];
                        in.readFully(voicenote, 0, len);
                        
                        Server.broadcastVN(username, voicenote);
                        
                        Server.gui.updateActivity(username+" broadcasted a voice note");
                    } else {
                        Server.broadcast(username, message);
                        Server.gui.updateActivity(username+" broadcasted a message");
                    }
                } else if (toUser.equals("@")) {
                    
                    Server.listOfUsers.remove(message);
                    if (!Server.listOfUsers.isEmpty()) {
                        Server.broadcast("#", message); //who disconnected
                        Server.sendUserList(Server.getListOfUsers());
                    } else {
                        System.out.println("No more users connected");
                        Server.gui.updateActivity("No more users connected");
                    }
                    in.close();
                    inFromClient.close();
                    
                    Server.gui.removeUser();
                    Server.gui.updateActivity(message+" disconnected");
                    
                } else if(toUser.equals("!")) {
                    //call code goes here
                    String usernameTo = message;
                    Server.sendCallRequest(username, usernameTo);
                    
                    Server.gui.updateActivity(username + " is calling " + usernameTo);
                } else if (toUser.equals("^")) {
                   String connectingUsername = message;
                   String connectingIP = Server.listOfUsers.get(connectingUsername).getClientSocket().getRemoteSocketAddress().toString().replace("/", "");
                   connectingIP = connectingIP.substring(0, connectingIP.length() - 5);
                   
                   Server.confCallUsers.put(connectingUsername, connectingIP);
                   
                   String userlist = "";
                   String ipList = "";
                   
                   for (Map.Entry<String, String> pair : Server.confCallUsers.entrySet()) {
                       userlist = userlist + pair.getKey()+",";
                       ipList = ipList + pair.getValue()+",";
                   }
                   userlist = userlist.substring(0, userlist.length() - 1);
                   ipList = ipList.substring(0, ipList.length()-1);
                   
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println(userlist+"\n"+ipList);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");                    
                   
                   Server.sendConfUsers(userlist, ipList);
                           
                } else {
                    if (message.equals("+") || message.equals("-")){
                        Server.sendCallResponse(message, toUser);
                        if (message.equals("+")) Server.gui.updateActivity(username + " is chatting to " + toUser);
                        if (message.equals("-")) Server.gui.updateActivity(toUser + " declined a call from " + username);
                    } else if (message.equals("*")) {
                        int len = in.readInt();
                        byte[] voicenote = new byte[len];
                        in.readFully(voicenote, 0, len);
                        Server.sendVoiceNote(voicenote, toUser);
                        
                        Server.gui.updateActivity(username+" sent a voice note to "+toUser);
                    } else {
                        Server.whisper(username, toUser, message);
                        Server.gui.updateActivity(username + " whispered to " + toUser);
                    }
                }
            } catch (IOException ex) {
                System.err.println("THIS EXCEPTION ::: "+ex);
                //bc that user disconnected
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.err.println("could not disconnect " + e);
                }
                break;
            }
        }

    }
    
    public Socket getClientSocket(){
        return clientSocket;
    }
    
}
