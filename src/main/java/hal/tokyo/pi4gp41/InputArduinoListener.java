/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.pi4gp41;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;

/**
 *
 * @author gn5r
 */
public class InputArduinoListener implements GpioPinListenerDigital{
    
    
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
        
        if(gpdsce.getState() == PinState.HIGH){
            int data = Gpio.digitalRead(2);
            
            System.out.println("データ:" + String.valueOf(data));
        }
    }
    
}
