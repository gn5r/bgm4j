/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author gn5r
 */
public class SEPlayer {

    private final AudioInputStream ais;
    private DataLine.Info dataLine;
    private final SourceDataLine se;
    private final byte[] data;

    /*    コンストラクタで音声ファイルを開く    */
    public SEPlayer(String fileName) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        System.out.println("音声データ読取り");

        ais = AudioSystem.getAudioInputStream(new File(fileName + ".wav"));

        AudioFormat format = ais.getFormat();
        dataLine = new DataLine.Info(SourceDataLine.class, format);

        se = (SourceDataLine) AudioSystem.getLine(dataLine);
        se.open();
        se.start();

        data = new byte[se.getBufferSize()];
    }

    /*    音声再生    */
    public void playSE() throws IOException {
        int size = -1;

        System.out.println("再生開始");

        while (true) {

            size = ais.read(data);
            if (size == -1) {
                break;
            }

            se.write(data, 0, size);
        }
    }

    /*    再生停止    */
    public void stopSE() {
        se.drain();
        se.stop();
        se.close();

        System.out.println("再生終了");
    }
}
