/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 *
 * @author gn5r
 */
public class PCA9685 {

    public final static int PCA9685_ADDRESS = 0x40;

    public final static int MODE1 = 0x00;

    public final static int SUBADR1 = 0x02;
    public final static int SUBADR2 = 0x03;
    public final static int SUBADR3 = 0x04;

    public final static int PRESCALE = 0xFE;
    public final static int LED0_ON_L = 0x06;
    public final static int LED0_ON_H = 0x07;
    public final static int LED0_OFF_L = 0x08;
    public final static int LED0_OFF_H = 0x09;

    public final static int ALL_LED_ON_L = 0xFA;
    public final static int ALL_LED_ON_H = 0xFB;
    public final static int ALL_LED_OFF_L = 0xFC;
    public final static int ALL_LED_OFF_H = 0xFD;

    private final I2CBus bus;
    private final I2CDevice motorDriver;

    /*    コンストラクタ    */
    public PCA9685() throws I2CFactory.UnsupportedBusNumberException, IOException {
        this(PCA9685_ADDRESS);
    }

    /*    privateコンストラクタ    */
    private PCA9685(int addr) throws I2CFactory.UnsupportedBusNumberException, IOException {
        /*    RaspberryPiのI2C_BUS1に接続したと想定    */
        this.bus = I2CFactory.getInstance(I2CBus.BUS_1);
        /*    Addressは0x40がメイン    */
        this.motorDriver = bus.getDevice(addr);

        /*    Reseting    */
        this.motorDriver.write(MODE1, (byte) 0x00);
    }

    public void setPWMFreq(float freq) {
        float prescaleval = 25000000;
        prescaleval /= 4096;
        prescaleval /= freq;
        prescaleval -= 1;

        double preScale = Math.floor(prescaleval + 0.5);

        try {
            byte oldReg = (byte) motorDriver.read(MODE1);
            /*    sleep    */
            byte newReg = (byte) motorDriver.read((oldReg & 0x7F) | 0x10);

            motorDriver.write(MODE1, newReg);
            motorDriver.write(PRESCALE, (byte) Math.floor(preScale));
            motorDriver.write(MODE1, oldReg);
            Thread.sleep(5);
            motorDriver.write(MODE1, (byte) (oldReg | 0x80));
        } catch (Exception e) {
        }
    }

    public void setPWM(int channel, int on, int off) {
        try {
            motorDriver.write(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
            motorDriver.write(LED0_ON_H + 4 * channel, (byte) (on >> 8));
            motorDriver.write(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
            motorDriver.write(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
        } catch (Exception e) {
        }
    }

}
