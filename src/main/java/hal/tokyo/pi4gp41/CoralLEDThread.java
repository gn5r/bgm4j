/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

/**
 *
 * @author pi
 */
public class CoralLEDThread extends Thread {

    private boolean flag;
    private int pwmValue;
    private final int maxValue;
    private final int ch;

    public CoralLEDThread(int pwmValue, int maxValue, int ch) {
        this.pwmValue = pwmValue;
        this.maxValue = maxValue;
        this.ch = ch;
    }

    @Override
    public void run() {

        try {
            boolean b = false;

            while (true) {
                Sample.servo_write(this.ch, this.pwmValue);

                if (this.pwmValue <= this.maxValue && b == false) {
                    this.pwmValue = this.pwmValue + 16;
                } else if (b == true) {
                    this.pwmValue = this.pwmValue - 16;
                }

                if (this.pwmValue >= this.maxValue) {
                    b = true;
                }
                if (this.pwmValue <= 0) {
                    b = false;
                }
                Thread.sleep(20);

                if (!flag) {
                    break;
                }
            }

        } catch (InterruptedException e) {
        }
    }

    public void LEDON() {
        this.flag = true;

        start();
    }

    public void LEDOFF() {
        this.flag = false;
    }
}
