
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class ConfTalkThread extends Thread {

    private byte[] tempBuffer;
    private DatagramSocket socket;
    private int port;
    private int BUFFER_SIZE = 4000;
    public AudioFormat audioFormat;
    public TargetDataLine targetDataLine;

    public ConfTalkThread(DatagramSocket sendSocket, int LISTEN_PORT) {
        this.socket = sendSocket;
        this.port = LISTEN_PORT;
    }

    @Override
    public void run() {
        try {

            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            tempBuffer = new byte[BUFFER_SIZE];

            while (Client.inConf) {
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                if (cnt > 0) {
                    for (InetAddress ia : ConfThread.theirInets) {
                        if (!ia.equals(Client.client.getLocalAddress().getHostAddress())) {
                            System.out.println(Client.client.getLocalAddress().getHostAddress());
                            System.out.println(ia);
                            DatagramPacket outPacket = new DatagramPacket(tempBuffer, tempBuffer.length, ia, this.port);
                            this.socket.send(outPacket);
                        }
                    }
                }
            }
            this.socket.close();

        } catch (Exception ex) {
            Logger.getLogger(CallerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }//end getAudioFormat
}
