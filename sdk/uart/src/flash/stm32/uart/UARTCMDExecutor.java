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
    
    /** <p>Pre-defined enum constants for baud rates supported by STM32 bootloader. </p>*/
    public enum BAUDRATE { 
        B1200(1200), B1800(1800), B2400(2400), B4800(4800), B9600(9600), B14400(14400), B19200(19200), 
        B28800(28800), B38400(38400), B56000(56000), B57600(57600), B115200(115200);
        private int value;
        private BAUDRATE(int value) {
            this.value = value;	
        }
        public int getValue() {
            return this.value;
        }
    }
	
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
