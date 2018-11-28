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
 * @author gn5r
 */
public class Sample {

    private static PCA9685 pca9685;
    private static ArduinoMega arduinoMega;
    private static BGMPlayer bgmPlayer;
    private static LoopBGM loopBGM;

    private static GpioController gpio;
    private static GpioPinDigitalOutput seaRED, seaWHITE, crabRED, OEPin;
    private static int level;

    private static int coral1;
    private static int coral2;
    private static int coral3;

    private static boolean coral1_flg;
    private static boolean coral2_flg;
    private static boolean coral3_flg;

    public static void main(String[] args) throws Exception {
        /*    初期化    */
        Init();

        while (true) {
            /*    海照明を赤点灯させる    */
            seaRED.high();
            System.out.println("BGM start");
            startBGM("Level_0");

            /*    ゲーム待機中    */
            while(true) if (arduinoMega.read() == 5) break;

            /*    ゲームが開始されたらBGMを停止し、ゲーム中BGMに切り替える    */
            Thread.sleep(1000);
            loopBGM.stopBGM();
            startBGM("game_mode");

            System.out.println("ゲーム結果受信待機中...");

            /*    Megaから0以外の値を受け取るまでループ    */
            while (true) {
                if (arduinoMega.read() != 0) {
                    level = arduinoMega.read() - 1;
                    break;
                }
            }

            /*    BGM停止、メイン演出へ移行    */
            Thread.sleep(1000);
            loopBGM.stopBGM();

            /*    OEピンlow、海照明を消灯    */
            OEPin.low();
            seaRED.low();

            Thread.sleep(500);
            mainPerform();

            /*    OEピンをHihgにして、サンゴLEDを消灯    */
            OEPin.high();
            System.out.println("次のゲームへ移行します。");
            Thread.sleep(2000);
        }
    }

    /*    初期化    */
    private static void Init() throws Exception {
        /*    GPIOのインスタンス取得    */
        gpio = GpioFactory.getInstance();

        /*    ArduinoMegaとI2C通信用のインスタンスを生成    */
        arduinoMega = new ArduinoMega();

        /*    サンゴLED用インスタンス生成    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);

        /*    照明用ピン        
            seaRED:海全体 白
            seaWHITE:海 赤
            crabRED:カニ本体 赤
         */
        seaRED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "Light1", PinState.LOW);
        seaRED.setShutdownOptions(true, PinState.LOW);

        seaWHITE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "Light2", PinState.LOW);
        seaWHITE.setShutdownOptions(true, PinState.LOW);

        crabRED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "crabLED1", PinState.LOW);
        crabRED.setShutdownOptions(true, PinState.LOW);

        /*    モータードライバのOEピン サンゴLEDリセット時に使用    */
        OEPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "OE", PinState.LOW);
        OEPin.setShutdownOptions(true, PinState.LOW);

        /*    サンゴLED用インスタンス生成    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);

        /*    サンゴ用LEDの値初期化    */
        resetCoralLED();

    }

    /*    メイン演出メソッド    */
    private static void mainPerform() throws Exception {

        System.out.println("main Performance");

        switch (Sample.level) {
            case 0:
                /*    レベルに応じたBGMの再生    */
                performBGM("Level_0");

                /*    照明点灯
                      海:赤
                      カニ:無点灯    */
                seaRED.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                }

                /*    照明消灯    */
                seaRED.low();
                break;

            case 1:
                /*    レベルに応じたBGMの再生    */
                performBGM("Level_1");

                /*    照明点灯
                      海:白
                      カニ:無点灯    */
                seaWHITE.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                    LEDON();
                }
                seaWHITE.low();
                break;

            case 2:

                /*    レベルに応じたBGMの再生    */
                performBGM("Level_2");

                /*    照明点灯
                      海:白
                      カニ:赤    */
                seaWHITE.high();
                crabRED.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                }
                seaWHITE.low();
                crabRED.low();

                break;

            case 3:
                /*    レベルに応じたBGMの再生    */
                performBGM("Level_3");

                /*    照明点灯
                      海:白
                      カニ:赤    */
                seaWHITE.high();
                crabRED.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                    LEDON();
                    Thread.sleep(50);
                }
                seaWHITE.low();
                crabRED.low();
                break;

            default:
                break;
        }

        /*    BGM停止    */
        bgmPlayer.stopBGM();

        /*    サンゴリセット    */
        resetCoralLED();
    }

    /*    サンゴLED点灯メソッド    */
    private static void LEDON() throws InterruptedException {

        System.out.println("Coral LED ON");

        switch (Sample.level) {
            case 1:
                Sample.servo_write(8, coral1);
                break;

            case 2:
                Sample.servo_write(8, coral1);
                Thread.sleep(50);
                Sample.servo_write(9, coral2);
                break;

            case 3:
                Sample.servo_write(8, coral1);
                Sample.servo_write(9, coral2);
                Sample.servo_write(10, coral3);
                break;
        }

        if (coral1 <= 4096 && coral1_flg == false) {
            coral1 += 128;
        } else if (coral1_flg == true) {
            coral1 -= 128;
        }
        if (coral1 >= 4096 - 128) {
            coral1_flg = true;
        }
        if (coral1 <= 0) {
            coral1_flg = false;
        }

        if (coral2 <= 4096 && coral2_flg == false) {
            coral2 += 128;
        } else if (coral2_flg == true) {
            coral2 -= 128;
        }
        if (coral2 >= 4096 - 128) {
            coral2_flg = true;
        }
        if (coral2 <= 0) {
            coral2_flg = false;
        }

        if (coral3 <= 4096 && coral3_flg == false) {
            coral3 += 256;
        } else if (coral3_flg == true) {
            coral3 -= 256;
        }
        if (coral3 >= 4096 - 256) {
            coral3_flg = true;
        }
        if (coral3 <= 0) {
            coral3_flg = false;
        }

        System.out.println("pwm1:" + coral1 + "\npwm2:" + coral2 + "\npwm3:" + coral3);
        System.out.println("b1:" + coral1_flg + "\nb2:" + coral2_flg + "\nb3:" + coral3_flg);
    }

    /*    モータドライバ書き込みメソッド    */
    public static void servo_write(int ch, int ang) {
        pca9685.setPWM(ch, 0, ang);
    }

    /*    初期状態、ゲーム中BGM再生    */
    private static void startBGM(String fileName) {
        loopBGM = new LoopBGM(fileName);
        loopBGM.musicPlay();
    }

    /*    演出BGM再生。Threadは毎回インスタンスを生成する    */
    private static void performBGM(String fileName) {
        bgmPlayer = new BGMPlayer(fileName);
        bgmPlayer.musicPlay();
    }

    /*    サンゴLEDリセットメソッド    */
    private static void resetCoralLED() {
        System.out.println("motor driver reset...");

        /*    値初期化    */
        coral1 = 0;
        coral2 = 1024;
        coral3 = 2048;

        coral1_flg = false;
        coral2_flg = false;
        coral3_flg = false;

    }

}
