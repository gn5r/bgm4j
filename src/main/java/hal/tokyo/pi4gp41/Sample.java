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
    private static GpioPinDigitalOutput seaRED, seaWHITE, crabRED, crabWHITE;
    private static int level;

    private static int pwm1;
    private static int pwm2;
    private static int pwm3;

    private static boolean b1;
    private static boolean b2;
    private static boolean b3;

    public static void main(String[] args) throws Exception {
        /*    初期化    */
        Init();

        /*    サンゴLED用インスタンス生成    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);

        while (true) {
            startBGM("Level_0");
            System.out.println("ゲーム結果受信待機中...");

            while (true) {
                Thread.sleep(1000);
                if (arduinoMega.read() != 0) {
                    level = arduinoMega.read() - 1;
                    break;
                }
            }
            bgmPlayer.stopBGM();
            deleteBGM();
            Thread.sleep(1000);
            mainPerform();
            System.out.println("次のゲームへ移行します。");
            Thread.sleep(2000);
        }
    }

    /*    初期化    */
    private static void Init() throws Exception {
        /*    GPIOのインスタンス取得    */
        gpio = GpioFactory.getInstance();

        /*    ArduinoMegaとI2C通信用のインスタンスを生成
                 引数はレジスタアドレス
        
         */
        arduinoMega = new ArduinoMega();

        /*    照明用ピン    
        
        light1:ブース全体 白
        light2:ブース全体 赤
        light3:カニ本体 白
        light4:カニ本体 赤
        
         */
        seaRED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "Light1", PinState.LOW);
        seaRED.setShutdownOptions(true, PinState.LOW);

        seaWHITE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, "Light2", PinState.LOW);
        seaWHITE.setShutdownOptions(true, PinState.LOW);

        crabRED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "Light3", PinState.LOW);
        crabRED.setShutdownOptions(true, PinState.LOW);

        crabWHITE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "Light4", PinState.LOW);
        crabWHITE.setShutdownOptions(true, PinState.LOW);

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
                startBGM("Level_0");

                /*    照明点灯    海:赤*/
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
                startBGM("Level_1");

                /*    照明点灯
                      海:白
                      カニ:白    */
                seaWHITE.high();
                crabWHITE.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                    LEDON();
                }
                seaWHITE.low();
                crabWHITE.low();
                break;

            case 2:

                /*    レベルに応じたBGMの再生    */
                startBGM("Level_2");

                /*    照明点灯
                      海:白
                      カニ:RED    */
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
                startBGM("Level_3");

                /*    照明点灯
                      海:白
                      カニ:RED    */
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
        deleteBGM();

        /*    サンゴリセット    */
        resetCoralLED();
    }

    /*    サンゴLED点灯メソッド    */
    private static void LEDON() throws InterruptedException {

        System.out.println("Coral LED ON");

        switch (Sample.level) {
            case 1:
                Sample.servo_write(8, pwm1);
                break;

            case 2:
                Sample.servo_write(8, pwm1);
                Thread.sleep(50);
                Sample.servo_write(9, pwm2);
                break;

            case 3:
                Sample.servo_write(8, pwm1);
                Sample.servo_write(9, pwm2);
                Sample.servo_write(10, pwm3);
                break;
        }

        if (pwm1 <= 4096 && b1 == false) {
            pwm1 += 128;
        } else if (b1 == true) {
            pwm1 -= 128;
        }
        if (pwm1 >= 4096 - 128) {
            b1 = true;
        }
        if (pwm1 <= 0) {
            b1 = false;
        }

        if (pwm2 <= 4096 && b2 == false) {
            pwm2 += 128;
        } else if (b2 == true) {
            pwm2 -= 128;
        }
        if (pwm2 >= 4096 - 128) {
            b2 = true;
        }
        if (pwm2 <= 0) {
            b2 = false;
        }

        if (pwm3 <= 4096 && b3 == false) {
            pwm3 += 256;
        } else if (b3 == true) {
            pwm3 -= 256;
        }
        if (pwm3 >= 4096 - 256) {
            b3 = true;
        }
        if (pwm3 <= 0) {
            b3 = false;
        }

        System.out.println("pwm1:" + pwm1 + "\npwm2:" + pwm2 + "\npwm3:" + pwm3);
        System.out.println("b1:" + b1 + "\nb2:" + b2 + "\nb3:" + b3);
    }

    /*    モータドライバ書き込みメソッド    */
    public static void servo_write(int ch, int ang) {
        pca9685.setPWM(ch, 0, ang);
    }

    /*    BGM再生。Threadは毎回インスタンスを生成する    */
    private static void startBGM(String fileName) {
        bgmPlayer = new BGMPlayer("BGM/" + fileName);
        bgmPlayer.musicPlay();
    }

    /*    古いBGMPlayerインスタンスを削除    */
    private static void deleteBGM() {
        Sample.bgmPlayer = null;
    }

    /*    サンゴLEDリセットメソッド    */
    private static void resetCoralLED() {
        /*    サンゴLEDをできるだけ弱く光らせる    */
        servo_write(8, 10);
        servo_write(9, 10);
        servo_write(10, 10);

        /*    値初期化    */
        pwm1 = 0;
        pwm2 = 1024;
        pwm3 = 2048;

        b1 = false;
        b2 = false;
        b3 = false;
    }

}
