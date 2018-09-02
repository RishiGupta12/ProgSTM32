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

import flash.stm32.uart.internal.SystemProperties;

public final class UARTCMDExecutor {
	
    /**<p>Production release version of this UART STM32 flasher sdk. </p>*/
    public static final String UART_LIB_VERSION = "1.0";
	
    private long comPortHandle;
    private final SerialComManager scm;
    private final SystemProperties sprop;
    
    public UARTCMDExecutor() throws IOException {
    	
    	sprop = new SystemProperties();
    	String tmpDir = sprop.getJavaIOTmpDir();
    	
    	scm = new SerialComManager("stmuartfwqkj", tmpDir, true, false);
    	
    }
    
    public void openComPort( ) {
    	
    }
    


}
