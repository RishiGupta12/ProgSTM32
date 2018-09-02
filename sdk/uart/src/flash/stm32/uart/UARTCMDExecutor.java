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
    public enum BR { 
        B1200(1200), B1800(1800), B2400(2400), B4800(4800), B9600(9600), B14400(14400), B19200(19200), 
        B28800(28800), B38400(38400), B56000(56000), B57600(57600), B115200(115200);
        private int value;
        private BR(int value) {
            this.value = value;	
        }
        public int getValue() {
            return this.value;
        }
    }
    
    /** <p>Pre-defined enum constants for number of data bits in a given serial frame. </p>*/
    public enum DB {
        /** <p>Serial frame will contain 5 data bits. </p>*/
        DB5(5),
        /** <p>Serial frame will contain 6 data bits. </p>*/
        DB6(6),
        /** <p>Serial frame will contain 7 data bits. </p>*/
        DB7(7),
        /** <p>Serial frame will contain 8 data bits. </p>*/
        DB8(8);
        private int value;
        private DB(int value) {
            this.value = value;	
        }
        public int getValue() {
            return this.value;
        }
    }
    
    /** <p>Pre-defined enum constants for number of stop bits in a given serial frame. </p>*/
    public enum SB {
        /** <p>Number of stop bits in one frame is 1. </p>*/
        SB1(1),
        /** <p>Number of stop bits in one frame is 1.5. </p>*/
        SB15(4),
        /** <p>Number of stop bits in one frame is 2. </p>*/
        SB2(2);
        private int value;
        private SB(int value) {
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
