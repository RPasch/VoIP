
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import static rw354_tut1_server.Server.inFromClient;
//import static rw354_tut1_server.Server.outFromServer;

/**
 * The class that connects clients that want to connect via voice note , voice
 * call or conference call
 */
public class ClientConnecter extends Thread {

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    OutputStream outFromServer;
    DataOutputStream out;
    InputStream inFromClient;
    DataInputStream in;

    public ClientConnecter(ServerSocket serverSocket, Socket clientSocket) {
        this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
    }

    /**
     * Opens sockets and data streams for the respective clients and responds It
     * updates the gui as well
     */
    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = serverSocket.accept();

                inFromClient = clientSocket.getInputStream();
                in = new DataInputStream(inFromClient);
                outFromServer = clientSocket.getOutputStream();
                out = new DataOutputStream(outFromServer);

                out.writeUTF(Server.getListOfUsers());
                String username = in.readUTF();

                SocketHandler sh = new SocketHandler(username, clientSocket);
                Thread t = new Thread(sh);
                t.start();

                Server.listOfUsers.put(username, sh);

                System.out.println("Welcome: " + username + " to the chat");
                Server.gui.updateActivity(username + " joined the chat");
                Server.gui.addUser(username);

                String userList = Server.getListOfUsers();
                Server.sendUserList(userList);
            } catch (Exception e) {
                System.err.println("SERVER2 " + e);
            }

        }
    }

}
