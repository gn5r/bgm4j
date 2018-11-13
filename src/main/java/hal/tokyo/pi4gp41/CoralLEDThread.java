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

        boolean b = false;
        while (true) {
            Sample.servo_write(this.ch, this.pwmValue,this.maxValue);
            
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
            
            System.out.println("value:" + String.valueOf(this.pwmValue));
            if (!this.flag)break;
            
        }
    }

    public void LEDON() {
        this.flag = true;

        start();
    }

    public void LEDOFF() {
        this.flag = false;
    }
    
    private synchronized void delay(long ms){
        try{
            wait(ms);
        }catch(InterruptedException e){
            
        }
    }
}
