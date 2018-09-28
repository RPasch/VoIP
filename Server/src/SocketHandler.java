//package rw354_tut1_server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
                    } else {
                        Server.broadcast(username, message);
                    }
                } else if (toUser.equals("@")) {
                    
                    Server.listOfUsers.remove(message);
                    if (!Server.listOfUsers.isEmpty()) {
                        Server.broadcast("#", message); //who disconnected
                        Server.sendUserList(Server.getListOfUsers());
                    } else {
                        System.out.println("No more users connected");
                    }
                    in.close();
                    inFromClient.close();
                } else if(toUser.equals("!")) {
                    //call code goes here
                    String usernameTo = message;
                    Server.sendCallRequest(username, usernameTo);
                    
                } else {
                    if (message.equals("+") || message.equals("-")){
                        Server.sendCallResponse(message, toUser);
                    } else if (message.equals("*")) {
                        int len = in.readInt();
                        byte[] voicenote = new byte[len];
                        in.readFully(voicenote, 0, len);
                        Server.sendVoiceNote(voicenote, toUser);
                    } else {
                        Server.whisper(username, toUser, message);
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
