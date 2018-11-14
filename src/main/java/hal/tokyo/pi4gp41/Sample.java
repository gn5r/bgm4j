/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author gn5r
 */
public class Sample {

    private static PCA9685 pca9685;
    private static ArduinoMega arduinoMega;
    private static BGMPlayer bgmPlayer;

    private static GpioController gpio;
    private static GpioPinDigitalOutput light1, light2, light3, light4;
    private static int level;

    public static void main(String[] args) throws Exception {
        Init();
        int n = 0;
        boolean b = false;

        for (int i = 0; i < 16; i++) {
            servo_write(i, n);
        }

        Thread.sleep(1000);

        while (true) {

            System.out.println("光の強さ:" + String.valueOf(n));
            servo_write(0, n);

            if (n <= 180 && b == false) {
                n = n + 10;
            } else if (b == true) {
                n = n - 10;
            }

            if (n >= 180) {
                b = true;
            }
            if (n <= -50) {
                b = false;
            }

            Thread.sleep(50);
        }

//        while (true) {
//            BGMStart("level0");
//
//            /*    ゲーム結果受信待機    */
//            while (true) {
//                if (arduinoMega.read() != -1) {
//                    level = arduinoMega.read();
//                    break;
//                }
//                Thread.sleep(500);
//            }
//
//            bgmPlayer.stopBGM();
//
//            mainPerform();
//        }
    }

    /*    初期化    */
    private static void Init() throws Exception {
        /*    GPIOのインスタンス取得    */
        gpio = GpioFactory.getInstance();

        /*    ArduinoMegaとI2C通信用のインスタンスを生成    
        
                 引数はレジスタアドレス
        
         */
        arduinoMega = new ArduinoMega(0x41);

        /*    照明用ピン    
        
        light1:ブース全体 白
        light2:ブース全体 赤
        light3:カニ本体 白
        light4:カニ本体赤
        
         */
        light1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "Light1", PinState.LOW);
        light1.setShutdownOptions(true, PinState.LOW);

        light2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "Light2", PinState.LOW);
        light2.setShutdownOptions(true, PinState.LOW);

        light3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Light3", PinState.LOW);
        light3.setShutdownOptions(true, PinState.LOW);

        light4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Light4", PinState.LOW);
        light4.setShutdownOptions(true, PinState.LOW);

        /*    サンゴLED用インスタンス生成    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);

    }

    /*    メイン演出メソッド    */
    private static void mainPerform() throws Exception {
        switch (Sample.level) {
            case 0:

                /*    レベルに応じたBGMの再生    */
                BGMStart("level0");

                /*    照明点灯    */
                light1.high();
                light2.high();

                /*    BGMが終了するまで演出    */
                while (bgmPlayer.getSize() != -1) {
                    ;
                }

                /*    照明消灯    */
                light1.low();
                light2.low();
                break;

            case 1:
                int fishGroup1 = 0;
                int fishGroup2 = 0;

                /*    レベルに応じたBGMの再生    */
                BGMStart("level1");

                /*    照明点灯    */
                light2.high();

                /*    BGMが終了するまで演出    */
                while (bgmPlayer.getSize() != -1) {
                    fishGroup1 += 10;
                    fishGroup2 += 20;

                    Thread.sleep(500);
                }

                light2.low();

                break;

            case 2:
                break;

            case 3:
                break;

            default:
                break;

        }

        /*    BGM停止    */
        bgmPlayer.stopBGM();
    }

    /*    サンゴLED点灯パターンメソッド    */
    private static void coral_LED() {

    }

    /*    引数にピン番号 角度    */
    private static void servo_write(int ch, int ang) {
        ang = (int) map(ang, 0, 360, 150, 600);
        pca9685.setPWM(ch, 0, ang);
    }

    /*    Arduino IDEでのmap関数    */
    private static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /*    BGM再生。Threadは毎回インスタンスを生成する    */
    private static void BGMStart(String fileName) {
        bgmPlayer = new BGMPlayer(fileName);
        bgmPlayer.musicPlay();
    }

    /*    次の曲へ変更。古いインスタンスを使いBGMをストップさせないと例外が発生    */
    private static void nextPlay() {

        bgmPlayer.stopBGM();
        BGMStart("Level0");

    }

}
