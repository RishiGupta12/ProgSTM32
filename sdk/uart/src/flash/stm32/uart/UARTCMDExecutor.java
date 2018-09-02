/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

import java.io.IOException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.uart.internal.SystemProperties;

public final class UARTCMDExecutor {
	
    /**<p>Production release version of this UART STM32 flasher sdk. </p>*/
    public static final String STM32UARTSDK_VERSION = "1.0";
    
    private long comPortHandle;
    private final SerialComManager scm;
    private final SystemProperties sprop;
    
    public UARTCMDExecutor() throws IOException {
    	
    	sprop = new SystemProperties();
    	String tmpDir = sprop.getJavaIOTmpDir();
    	
    	scm = new SerialComManager("stmuartfwqkj", tmpDir, true, false);
    	
    }
    
    public void openComPort(String port, SerialComManager.BAUDRATE baudRate, 
    		SerialComManager.DATABITS dataBits, SerialComManager.STOPBITS stopBits, 
    		SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl) throws SerialComException {
    	
        comPortHandle = scm.openComPort(port, true, true, true);
        
        scm.configureComPortData(comPortHandle, dataBits, stopBits, parity, baudRate, 0);
        
        scm.configureComPortControl(comPortHandle, flowCtrl, 'x', 'x', false, false);
    }

}
