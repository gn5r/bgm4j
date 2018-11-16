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
public class ArduinoMega2 {

    /*    ArduinoMegaのスレーブアドレス    */
    private static final int MEGA_ADDRESS = 0x80;

    /*    I2C通信用のグローバル変数    */
    private I2CBus bus;
    private I2CDevice device;

    /*    コンストラクタ    */
    public ArduinoMega2() throws I2CFactory.UnsupportedBusNumberException, IOException {
        this(MEGA_ADDRESS);
    }

    /*    privateコンストラクタ    */
    private ArduinoMega2(int addr) throws I2CFactory.UnsupportedBusNumberException, IOException {
        /*     ラズパイのI2C_BUS1に接続したと仮定    */
        this.bus = I2CFactory.getInstance(I2CBus.BUS_1);

        /*    ArduinoMegaのアドレスはターミナルから検索可能    */
        this.device = bus.getDevice(addr);
    }

    /*    データ読み取りメソッド    */
    public int read() throws IOException {
        return this.device.read();
    }
    
    /*    スレーブへ書き込み    */
    public void write(int data) throws IOException{
        this.device.write((byte)data);
    }
}
