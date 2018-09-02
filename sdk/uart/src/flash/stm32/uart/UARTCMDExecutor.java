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
    private int supportedCmds;

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
    
    private int sendCommand(byte[] cmd) throws SerialComException {
        
        int x;
        byte[] buf = new byte[2];
        
        scm.writeBytes(comPortHandle, cmd);
        
        //TODO parity error handling
        x = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
        
        if ((x == 1) && (buf[0] == ACK)) {
            return 0;
        }
        
        return -1;
    }
    
    /*
     * @return number of bytes read including length of data, ACK at end.
     */
    private int receiveResponse(byte[] res) throws SerialComException {
        
        int x;
        int index = 0;
        int numBytes = res.length;
        
        //TODO add total op timeout, consider lenth of response
        while(true) {
            x = scm.readBytes(comPortHandle, res, index, numBytes, -1, null);
            if (x > 0) {
                if (res[x - 1] == ACK) {
                    break;
                }
                index = index + x;
                numBytes = numBytes - x;
            }
        }
        
        return index;
    }
    
    /**
     * 
     * @return 0 if operation fails or bit mask of commands supported by given bootloader.
     * @throws SerialComException
     */
    public int getAllowedCommands() throws SerialComException {

        int x;
        int res;
        byte[] buf = new byte[64];
        
        res = sendCommand(CMD_GET_ALLOWED_CMDS);
        if (res == -1) {
            return 0;
        }
        
        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }
        
        x = 1;
        while((buf[x] != ACK) && (x < res)) {
            switch (buf[x]) {
            
            case 0x00:
                supportedCmds = supportedCmds | BLCMDS.GET;
                break;
                
            case 0x01:
                supportedCmds = supportedCmds | BLCMDS.GET_VRPS;
                break;
                
            case 0x02:
                supportedCmds = supportedCmds | BLCMDS.GET_ID;
                break;
                
            case 0x11:
                supportedCmds = supportedCmds | BLCMDS.READ_MEMORY;
                break;
                
            case 0x21:
                supportedCmds = supportedCmds | BLCMDS.GO;
                break;
                
            case 0x31:
                supportedCmds = supportedCmds | BLCMDS.WRITE_MEMORY;
                break;
                
            case 0x43:
                supportedCmds = supportedCmds | BLCMDS.ERASE;
                break;
                
            case 0x44:
                supportedCmds = supportedCmds | BLCMDS.EXTENDED_ERASE;
                break;
                
            case 0x63:
                supportedCmds = supportedCmds | BLCMDS.WRITE_PROTECT;
                break;
                
            case 0x73:
                supportedCmds = supportedCmds | BLCMDS.WRITE_UNPROTECT;
                break;
                
            case (byte)0x82:
                supportedCmds = supportedCmds | BLCMDS.READOUT_PROTECT;
                break;
                
            case (byte)0x92:
                supportedCmds = supportedCmds | BLCMDS.READOUT_UNPROTECT;
                break;                
            }
            
            x++;
        }
        
        return supportedCmds;
    }
}


























