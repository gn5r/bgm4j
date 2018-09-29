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

    public static void main(String[] args) throws Exception {
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
