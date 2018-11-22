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

    public static void main(String[] args) throws Exception {
//        Init();

        /*    サンゴLED用インスタンス生成    */
        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);

        while (true) {
            startBGM("Level_0");

            System.out.println("ゲーム結果受信待機中...");
            Thread.sleep(3000);
            Sample.level = 3;
            System.out.println("キレイドは1です");

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

    }

    /*    メイン演出メソッド    */
    private static void mainPerform() throws Exception {

        System.out.println("main Performance");
        boolean flag = true;

        switch (Sample.level) {
            case 0:

                /*    レベルに応じたBGMの再生    */
                startBGM("Level_0");

                /*    照明点灯    海:赤*/
//                seaRED.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
                        break;
                    }
                }

                /*    照明消灯    */
//                seaRED.low();
                break;

            case 1:

                /*    レベルに応じたBGMの再生    */
                startBGM("Level_1");

                /*    照明点灯
                      海:白
                      カニ:白    */
//                seaWHITE.high();
//                crabWHITE.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    if (bgmPlayer.getSize() == -1) {
//                        coralLEDOFF(1);
//                        deleteCoral(1);
                        flag = false;
                        break;
                    }
                    LEDON(flag);

//                    coralLEDON(1, 0, 1024, 6);                    
                }
//                seaWHITE.low();
//                crabWHITE.low();
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
                        bgmPlayer.stopBGM();
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
//                seaWHITE.high();
//                crabRED.high();

                /*    BGMが終了するまで演出    */
                while (true) {
                    Thread.sleep(1000);
                    LEDON(flag);
                    if (bgmPlayer.getSize() == -1) {
                        flag = false;
                        break;
                    }
                }
//                seaWHITE.low();
//                crabRED.low();

                break;

            default:
                break;
        }

        /*    BGM停止    */
        bgmPlayer.stopBGM();
        deleteBGM();
    }

    private static void LEDON(boolean flag) throws InterruptedException {
        int pwm1 = 0;
        int pwm2 = 512;
        int pwm3 = 2048;

        boolean b = false;

        while (true) {
            switch (Sample.level) {
                case 1:
                    Sample.servo_write(1, pwm1, 2048);
                    break;

                case 2:
                    Sample.servo_write(1, pwm1, 2048);
                    Thread.sleep(50);
                    Sample.servo_write(2, pwm2, 2048);
                    break;

                case 3:
                    Sample.servo_write(8, pwm1, 2048);
                    Sample.servo_write(9, pwm2, 2048);
                    Sample.servo_write(10, pwm3, 2048);
                    break;
            }

            pwmCalc(pwm1, 2048);
            pwmCalc(pwm2, 2048);
            pwmCalc(pwm3, 2048);

            if (!flag) {
                break;
            }
        }
    }

    /*    サンゴLED点灯パターンメソッド    */
    public static void servo_write(int ch, int ang, int maxValue) {
        ang = (int) map(ang, 0, maxValue, 150, 600);
        pca9685.setPWM(ch, 0, ang);
    }

    /*    Arduino IDEでのmap関数    */
    private static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
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

    private static void pwmCalc(int pwm, int max) {

        if (pwm <= max) {
            pwm += 16;
        } else if (pwm >= max) {
            pwm -= 16;
        }
    }

    private void calc() {

        int n = 0;
        boolean b = false;

        if (n <= 2048 && b == false) {
            n = n + 32;
        } else if (b == true) {
            n = n - 32;
        }

        if (n >= 2048) {
            b = true;
        }
        if (n <= 0) {
            b = false;
        }
    }

}
