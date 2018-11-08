/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author gn5r
 */
public class Sample {

    private static BGMPlayer bgmPlayer;
    private static boolean flag = true;

    private static PCA9685 pca9685;

    private static GpioController gpio;
    private static GpioPinDigitalInput arduinoPin;
    private static GpioPinDigitalOutput ledPin, light1, light2, light3;

    private static int level;

    public static void main(String[] args) throws Exception {
        Init();

        while (true) {
            BGMStart("level0");

            /*    ゲーム結果受信待機    */
            while (flag) {

                Thread.sleep(500);
            }

            bgmPlayer.stopBGM();

            mainPerform();
            motorReset();
        }

    }

    /*    初期化    */
    private static void Init() throws Exception {
        /*    GPIOのインスタンス取得    */
        gpio = GpioFactory.getInstance();

        /*    Arduinoから送られてくるキレイドのピン    */
        arduinoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
        arduinoPin.setShutdownOptions(true);
        arduinoPin.addListener(new InputArduinoListener());


        /*    演出用LEDピン    */
        ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "LED", PinState.LOW);
        ledPin.setShutdownOptions(true, PinState.LOW);

        /*    照明用ピン    */
        light1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Light1", PinState.LOW);
        light1.setShutdownOptions(true, PinState.LOW);

        light2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, "Light2", PinState.LOW);
        light2.setShutdownOptions(true, PinState.LOW);

        light3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "Light3", PinState.LOW);
        light3.setShutdownOptions(true, PinState.LOW);

        level = 0;

        /*    モータの初期位置を0にセット    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);
        motorReset();
    }

    /*    モーターリセット    */
    private static void motorReset() {
        for (int i = 0; i < 2; i++) {
            servo_write(i, 0);
        }
    }

    private static void servo_write(int ch, int ang) {
        ang = (int) map(ang, 0, 180, 150, 600);
        pca9685.setPWM(ch, 0, ang);
    }

    /*    Arduino IDEでのmap関数    */
    private static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static void setLevel(int level) {
        Sample.level = level;
    }

    private static void mainPerform() throws Exception {
        switch (Sample.level) {
            case 0:

                int servoAng = 0;
                /*    レベルに応じたBGMの再生    */

                /*    照明変更    */
                light1.high();
                light2.high();
                
                /*    BGMが終了するまで演出    */
                while (bgmPlayer.getSize() != -1);
                
                light1.toggle();
                light2.toggle();
                break;

            case 1:
                break;

            case 2:
                break;

            case 3:
                break;

            default:
                break;

            /*    BGM停止    */
        }
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
