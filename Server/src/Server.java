//package rw354_tut1_server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    static OutputStream outFromServer;
    static DataOutputStream out;
    static InputStream inFromClient;
    static DataInputStream in;
    private static InputStream terminalIn = null;
    private static BufferedReader br = null;
    public static ConcurrentHashMap<String, SocketHandler> listOfUsers = new ConcurrentHashMap<>();
    public static ServerGui gui;
    public static ConcurrentHashMap<String, String> confCallUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int portNumber = 8000;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println(serverSocket);
        } catch (Exception e) {
            System.exit(0);
        }

        gui = new ServerGui();
        gui.show();

        SocketHandler sh = null;
        try {
            clientSocket = serverSocket.accept();

            inFromClient = clientSocket.getInputStream();
            in = new DataInputStream(inFromClient);
            outFromServer = clientSocket.getOutputStream();
            out = new DataOutputStream(outFromServer);

            out.writeUTF("");

            String username = in.readUTF();
            System.out.println("Welcome: " + username + " to the chat");
            gui.updateActivity(username + " joined the chat");
            gui.addUser(username);

            sh = new SocketHandler(username, clientSocket);

            Thread t = new Thread(sh);
            t.start();

            listOfUsers.put(username, sh);

        } catch (Exception e) {
            System.err.println(e);
        }

        ClientConnecter connector = new ClientConnecter(serverSocket, clientSocket);
        connector.start();

        try {

            outFromServer = clientSocket.getOutputStream();
            out = new DataOutputStream(outFromServer);

            String userList = getListOfUsers();
            out.writeUTF("&" + userList);

        } catch (Exception e) {
            System.err.println("SERVER: " + e);
        }

    }

    public static String getListOfUsers() {
        String userList = "";

        if (!listOfUsers.isEmpty()) {
            for (String key : listOfUsers.keySet()) {
                userList = userList + key + ",";
            }
        }

        if (userList != "") {
            userList = userList.substring(0, userList.length() - 1);
        }

        return userList;
    }

    public static void sendUserList(String userList) {
        OutputStream outFromServer = null;
        DataOutputStream out = null;

        for (Map.Entry<String, SocketHandler> pair : listOfUsers.entrySet()) {
            try {
                outFromServer = pair.getValue().getClientSocket().getOutputStream();//.getClientSocket().getOutputStream();
                out = new DataOutputStream(outFromServer);

                out.writeUTF("&" + userList);

            } catch (Exception e) {
                System.err.println("problem in sendUserList " + e);
            }
        }

    }

    public static void broadcast(String username, String message) {
        OutputStream outFromServer = null;
        DataOutputStream out = null;

        for (Map.Entry<String, SocketHandler> pair : listOfUsers.entrySet()) {
            try {
                outFromServer = pair.getValue().getClientSocket().getOutputStream();//.getClientSocket().getOutputStream();
                out = new DataOutputStream(outFromServer);
                out.writeUTF(username);
                out.writeUTF(message);
            } catch (Exception e) {
                System.err.println("problem in broadcast " + e);
            }
        }

    }

    public static void whisper(String usernameFrom, String usernameTo, String message) {
        OutputStream outFromServer = null;
        DataOutputStream out = null;

        for (Map.Entry<String, SocketHandler> pair : listOfUsers.entrySet()) {
            if (pair.getKey().equals(usernameTo) || pair.getKey().equals(usernameFrom)) {
                try {
                    outFromServer = pair.getValue().getClientSocket().getOutputStream();
                    out = new DataOutputStream(outFromServer);
                    out.writeUTF(usernameFrom + " > " + usernameTo);
                    out.writeUTF(message);
                } catch (Exception e) {
                    System.err.println("could not whisper : " + e);
                }
            }
        }

    }

    public static void sendCallRequest(String usernameFrom, String usernameTo) {

        OutputStream outFromServer = null;
        DataOutputStream out = null;

        System.out.println("LIST ::: " + listOfUsers);
        try {
            outFromServer = listOfUsers.get(usernameTo).getClientSocket().getOutputStream();
            out = new DataOutputStream(outFromServer);
            out.writeUTF("!");

            String ip = listOfUsers.get(usernameFrom).getClientSocket().getRemoteSocketAddress().toString().replace("/", "");
            ip = ip.substring(0, ip.length() - 5);

            out.writeUTF(ip);
            out.writeUTF(usernameFrom);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            outFromServer = listOfUsers.get(usernameFrom).getClientSocket().getOutputStream();
            out = new DataOutputStream(outFromServer);
            out.writeUTF("!");

            String ip = listOfUsers.get(usernameTo).getClientSocket().getRemoteSocketAddress().toString().replace("/", "");
            ip = ip.substring(0, ip.length() - 5);

            out.writeUTF(ip);
            out.writeUTF(usernameTo);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void sendCallResponse(String response, String username) {
        try {
            outFromServer = listOfUsers.get(username).getClientSocket().getOutputStream();
            out = new DataOutputStream(outFromServer);

            out.writeUTF(response);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendVoiceNote(byte[] voicenote, String username) {
        try {

            outFromServer = listOfUsers.get(username).getClientSocket().getOutputStream();
            out = new DataOutputStream(outFromServer);

            out.writeUTF("*");

            out.writeUTF(username);

            out.writeInt(voicenote.length);
            out.write(voicenote, 0, voicenote.length);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void broadcastVN(String username, byte[] voicenote) {
        OutputStream outFromServer = null;
        DataOutputStream out = null;

        for (Map.Entry<String, SocketHandler> pair : listOfUsers.entrySet()) {
            try {
                outFromServer = pair.getValue().getClientSocket().getOutputStream();//.getClientSocket().getOutputStream();
                out = new DataOutputStream(outFromServer);

                out.writeUTF("*");
                out.writeUTF(username);
                out.writeInt(voicenote.length);
                out.write(voicenote, 0, voicenote.length);

            } catch (Exception e) {
                System.err.println("problem in broadcast " + e);
            }
        }

    }

    public static void sendConfUsers(String users, String IPs) {
        try {
            for (Map.Entry<String, String> pair : confCallUsers.entrySet()) {
                try {
                    outFromServer = listOfUsers.get(pair.getKey()).getClientSocket().getOutputStream();//.getClientSocket().getOutputStream();
                    out = new DataOutputStream(outFromServer);

                    out.writeUTF("^");
                    out.writeUTF(users);
                    out.writeUTF(IPs);
                    
                } catch (Exception e) {
                    System.err.println("problem in broadcast " + e);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
