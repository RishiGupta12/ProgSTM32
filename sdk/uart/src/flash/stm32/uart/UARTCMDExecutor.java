/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

import java.io.IOException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.uart.internal.SystemProperties;

public final class UARTCMDExecutor {

    /**
     * <p>Production release version of this UART STM32 flasher sdk.</p>
     */
    public static final String STM32UARTSDK_VERSION = "1.0";
    
    private final byte ACK = 0x79;
    private final byte NACK = 0x1F;

    private final byte[] CMD_GET_ALLOWED_CMDS = new byte[] { (byte)0x00, (byte)0xFF };


    private long comPortHandle;
    private final SerialComManager scm;
    private final SystemProperties sprop;

    public UARTCMDExecutor() throws IOException {

        sprop = new SystemProperties();
        String tmpDir = sprop.getJavaIOTmpDir();

        scm = new SerialComManager("stmuartfwqkj", tmpDir, true, false);

    }

    public void openComPort(String port, SerialComManager.BAUDRATE baudRate, SerialComManager.DATABITS dataBits,
            SerialComManager.STOPBITS stopBits, SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException {

        comPortHandle = scm.openComPort(port, true, true, true);

        scm.configureComPortData(comPortHandle, dataBits, stopBits, parity, baudRate, 0);

        scm.configureComPortControl(comPortHandle, flowCtrl, 'x', 'x', false, false);

        // 500 milliseconds timeout or serial port read
        scm.fineTuneReadBehaviour(comPortHandle, 0, 5, 100, 5, 200);
        
        scm.clearPortIOBuffers(comPortHandle, true, true);
    }

    public void closeComPort() throws SerialComException {
        
        scm.closeComPort(comPortHandle);
    }
    
    /**
     * 
     * @return 0 if operation fails or bit mask of commands supported by given bootloader
     * @throws SerialComException
     */
    public int getAllowedCommands() throws SerialComException {
        
        byte[] buf = new byte[64];        
        
        scm.writeBytes(comPortHandle, CMD_GET_ALLOWED_CMDS);
        
        //TODO parity error handling
        scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
        if(buf[0] == ACK) {
            return 0;
        }
        
        return 0;
    }

}


























