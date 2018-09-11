/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

import flash.stm32.core.internal.SystemProperties;
import flash.stm32.core.CommunicationInterface;

import java.io.IOException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

/**
 * <p>
 * Represents a serial port (UART interface) through which host computer will
 * communicate with bootloader in a stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTInterface extends CommunicationInterface {

    private final SerialComManager scm;
    private final SystemProperties sprop;

    private long comPortHandle;

    public UARTInterface(String libName) throws IOException {

        sprop = new SystemProperties();
        String tmpDir = sprop.getJavaIOTmpDir();

        scm = new SerialComManager(libName, tmpDir, true, false);

    }
    
    public void open(String port, SerialComManager.BAUDRATE baudRate, 
            SerialComManager.DATABITS dataBits, SerialComManager.STOPBITS stopBits, 
            SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException {
        
    }

}
