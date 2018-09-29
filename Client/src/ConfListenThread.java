
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class ConfListenThread extends Thread {

    private DatagramSocket socket;
    private byte[] tempBuffer;

    private int BUFFER_SIZE = 4000;
    public static boolean keepPlay;

    public static void setKeepPlay(boolean x) {
        keepPlay = x;
    }
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    public ConfListenThread(DatagramSocket socket) {
        this.socket = socket;
        this.tempBuffer = new byte[BUFFER_SIZE];
    }

    @Override
    public void run() {
        try {

            DatagramPacket inPacket;
            keepPlay = true;

            while (Client.inCall) {//use to be keepPlay
                inPacket = new DatagramPacket(tempBuffer, tempBuffer.length);
                this.socket.receive(inPacket);

                byte[] audioData = inPacket.getData();

                InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
                AudioFormat audioFormat = getAudioFormat();
                audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                int cnt;

                while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {

                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }
                }
                sourceDataLine.drain();
                sourceDataLine.close();

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
    }

}
