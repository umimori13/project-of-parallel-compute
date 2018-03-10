import javax.sound.sampled.*;
import javax.sound.sampled.spi.*;
import java.io.File;
import java.io.IOException;

public class Project1 {
    public static void main(String[] args) throws IOException {
        File outputFile = new File("recoder.wav");
        AudioFormat audioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F,
                false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                audioFormat);
        TargetDataLine targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem
                    .getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        targetDataLine.start();
        TargetDataLine finalTargetDataLine = targetDataLine;
        new Thread() {
            public void run() {
                AudioInputStream cin = new AudioInputStream(finalTargetDataLine);
                try {
                    AudioSystem.write(cin, AudioFileFormat.Type.WAVE,
                            outputFile);
                    System.out.println("over");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();
        System.out.println("begin");
        System.in.read();
        targetDataLine.close();
        }
    }

