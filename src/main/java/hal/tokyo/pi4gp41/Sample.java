/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import java.util.Scanner;

/**
 *
 * @author gn5r
 */
public class Sample {

    private static BGMPlayer bgmPlayer;
    private static int num = 0;
    private static boolean flag = true;

    private static PCA9685 pca9685;

    private static int n = 0;
    private static int m = 180;
    private static boolean b = false;
    private static boolean d = false;

    public static void main(String[] args) throws Exception {
//        sample();

        pca9685 = new PCA9685();
        pca9685.setPWMFreq(60);
        for (int i = 0; i < 16; i++) {
            servo_write(i, 0);
        }

        Thread.sleep(1000);

        while (true) {
            servo_write(1, n);
            servo_write(4, m);

            if (n <= 180 && b == false) {
                n = n + 10;
            } else if (b == true) {
                n = n - 10;
            }

            if (m >= 0 && d == false) {
                m = m - 10;
            } else if (d == true) {
                m = m + 10;
            }

            if (n >= 180) {
                b = true;
            }
            if (n <= 0) {
                b = false;
            }

            if (m <= 0) {
                d = true;
            }
            if (m >= 180) {
                d = false;
            }

            Thread.sleep(50);
            System.out.println("Angle1:" + n + "\nAngle2:" + m);
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

    private static void sample() {
        System.out.println("曲再生プログラム");

        Scanner scanner = new Scanner(System.in);

        System.out.println("再生する曲を選んでね");

        String keyCode = scanner.next();
        num = Integer.parseInt(keyCode);

        /*    入力した番号で、曲を選択    */
        switch (num) {
            case 0:
            case 1:
            case 2:
                BGMStart(keyCode);
                break;

            default:
                break;
        }

        /*    "e"キーが押されるまで無限ループ    */
        while (flag) {
            System.out.println("\"n\"を入力すると、次の曲を再生するよ\n"
                    + "\"e\"を入力すると終了するよ");
            String key = scanner.next();

            switch (key) {
                /*    次の曲を再生    */
                case "n":
                    nextPlay();
                    break;

                /*    再生を停止し、プログラム終了    */
                case "e":
                    bgmPlayer.stopBGM();
                    flag = false;
                    break;

                default:
                    break;
            }
        }
    }

    /*    BGM再生。Threadは毎回インスタンスを生成する    */
    private static void BGMStart(String fileName) {
        bgmPlayer = new BGMPlayer(fileName);
        bgmPlayer.musicPlay();
    }

    /*    次の曲へ変更。古いインスタンスを使いBGMをストップさせないと例外が発生    */
    private static void nextPlay() {

        System.out.println("前回のトラック:" + String.valueOf(num));

        num++;

        /*    3.wav は無いのでそれのチェック    */
        if (num > 2) {
            num = 0;
        }

        bgmPlayer.stopBGM();
        BGMStart(String.valueOf(num));

        System.out.println("再生トラック:" + String.valueOf(num));
    }

}
