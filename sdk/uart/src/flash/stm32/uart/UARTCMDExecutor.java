/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

import java.io.IOException;

import com.serialpundit.serial.SerialComManager;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

public final class UARTCMDExecutor {
	
    private long handle;
    private SerialComManager scm;
    
    public UARTCMDExecutor() throws IOException {
    	
        // get serial communication manager instance
    	scm = new SerialComManager();
    	
    }
    


}
