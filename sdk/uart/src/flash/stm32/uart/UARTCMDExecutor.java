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
    private final byte[] CMD_GET_VRPS = new byte[] { (byte)0x01, (byte)0xFE };
    private final byte[] CMD_GET_ID = new byte[] { (byte)0x02, (byte)0xFD };
    private final byte[] CMD_READ_MEMORY = new byte[] { (byte)0x11, (byte)0xEE };
    private final byte[] CMD_GO = new byte[] { (byte)0x21, (byte)0xDE };
    private final byte[] CMD_WRITE_MEMORY = new byte[] { (byte)0x31, (byte)0xCE };

    private long comPortHandle;
    private final SerialComManager scm;
    private final SystemProperties sprop;

    public UARTCMDExecutor(String libName) throws IOException {

        sprop = new SystemProperties();
        String tmpDir = sprop.getJavaIOTmpDir();

        scm = new SerialComManager(libName, tmpDir, true, false);

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
        int supportedCmds;
        byte[] buf = new byte[32];
        
        res = sendCommand(CMD_GET_ALLOWED_CMDS);
        if (res == -1) {
            return 0;
        }
        
        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }
        
        supportedCmds = 0;
        x = 2;
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
    
    /**
     * 
     * @return 
     * @throws SerialComException
     */
    public String getBootloaderVersion() throws SerialComException {
        
        int res;
        String bootloaderVersion = null;
        byte[] buf = new byte[32];
        
        res = sendCommand(CMD_GET_ALLOWED_CMDS);
        if (res == -1) {
            return null;
        }
        
        res = receiveResponse(buf);
        if (res == -1) {
            return null;
        }
        
        if (buf[1] == 0x31) {
            bootloaderVersion = new String("3.1");
        } else if (buf[1] == 0x30) {
            bootloaderVersion = new String("3.0");
        } else if (buf[1] == 0x22) {
            bootloaderVersion = new String("2.2");
        } else {
        }
        
        return bootloaderVersion;
    }
    
    /**
     * STM32 product codes are defined in AN2606 application note.
     * 
     * @return 
     * @throws SerialComException
     */
    public int getChipID() throws SerialComException {
        
        int res;
        byte[] buf = new byte[16];
        
        res = sendCommand(CMD_GET_ID);
        if (res == -1) {
            return 0;
        }
        
        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }
        
        res = 0;
        res = res | (buf[1] << 8);
        res = res | buf[2];
        
        return res;
    }
    
    /**
     * 
     * @return 0 on failure, 1 if enabled, 2 if disabled.
     * @throws SerialComException
     */
    public int getReadProtectionStatus() throws SerialComException {
        
        int res;
        byte[] buf = new byte[16];
        
        res = sendCommand(CMD_GET_VRPS);
        if (res == -1) {
            return 0;
        }
        
        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }
        
        //TODO exact purpose of byte1,2
        //buf[1]
        //buf[2]

        
        return res;
    }
    
    /**
     * This API read data from any valid memory address in RAM, Flash memory 
     * and the information block (system memory or option byte areas). It may 
     * be used by GUI programs where input address is taken from user.
     * 
     * To address 32 bit address range, only 4 LSB bytes are used by this API, 
     * upper 4 bytes are discarded.
     * 
     * @return 
     * @throws SerialComException
     */
    public int readMemory(byte[] data, long startAddr, int numBytesToRead) throws SerialComException {
        
        int x;
        int res;
        int index = 0;
        byte[] addrbuf = new byte[5];
        byte[] numbuf = new byte[2];
        
        if(data == null) {
            throw new IllegalArgumentException("data buffer can't be null");
        }
        if ((numBytesToRead > 256) || (numBytesToRead <= 0)) {
            throw new IllegalArgumentException("numBytesToRead must be between 1 to 256");
        }
        if (numBytesToRead > data.length) {
            throw new IllegalArgumentException("data buffer is small");
        }
        
        res = sendCommand(CMD_READ_MEMORY);
        if (res == -1) {
            return 0;
        }
        
        startAddr = startAddr & 0xFFFFFFFF;
        
        addrbuf[0] = (byte) ((startAddr >> 24) & 0xFF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0xFF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0xFF);
        addrbuf[3] = (byte) ( startAddr & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCommand(addrbuf);
        if (res == -1) {
            return 0;
        }
        
        numbuf[0] = (byte) numBytesToRead;
        numbuf[1] = (byte) (numBytesToRead ^ 0xFF);
        
        res = sendCommand(numbuf);
        if (res == -1) {
            return 0;
        }
        
        //TODO timeout
        while(true) {
            x = scm.readBytes(comPortHandle, data, index, numBytesToRead, -1, null);
            if (x > 0) {
                index = index + x;
                numBytesToRead = numBytesToRead - x;
            }
            if (numBytesToRead == 0) {
                break;
            }
        }
        
        return 0;
    }
    
    /**
     * The host should send the base address where the application to jump to is programmed.
     * 
     * To address 32 bit address range, only 4 LSB bytes are used by this API, 
     * upper 4 bytes are discarded.
     * 
     * @return 
     * @throws SerialComException
     */
    public int goJump(long addrToJumpTo) throws SerialComException {
        
        int res;
        byte[] addrbuf = new byte[5];
        
        res = sendCommand(CMD_GO);
        if (res == -1) {
            return 0;
        }
        
        addrToJumpTo = addrToJumpTo & 0xFFFFFFFF;
        
        addrbuf[0] = (byte) ((addrToJumpTo >> 24) & 0xFF);
        addrbuf[1] = (byte) ((addrToJumpTo >> 16) & 0xFF);
        addrbuf[2] = (byte) ((addrToJumpTo>> 8) & 0xFF);
        addrbuf[3] = (byte) ( addrToJumpTo & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCommand(addrbuf);
        if (res == -1) {
            return 0;
        }
        
        //TODO timeout
        while(true) {
            res = scm.readBytes(comPortHandle, addrbuf, 0, 1, -1, null);
            if (res > 0) {
                if (addrbuf[0] == ACK) {
                    return 0;
                }
                if (addrbuf[0] == NACK) {
                    return -1;
                }
            }
        }
    }
    
    /**
     * 
     * To address 32 bit address range, only 4 LSB bytes are used by this API, 
     * upper 4 bytes are discarded.
     * 
     * TODO handle two NACK
     * @return 
     * @throws SerialComException
     */
    public int writeMemory(final byte[] data, long startAddr) throws SerialComException {
        
        int res;
        int checksum;
        int numBytesToWrite;
        byte[] addrbuf = new byte[5];

        if (data == null) {
            throw new IllegalArgumentException("data buffer can't be null");
        }
        
        numBytesToWrite = data.length;
        if ((numBytesToWrite > 256) || (numBytesToWrite == 0)) {
            throw new IllegalArgumentException("inappropriate data buffer size");
        }
        
        res = sendCommand(CMD_WRITE_MEMORY);
        if (res == -1) {
            return 0;
        }
        
        startAddr = startAddr & 0xFFFFFFFF;
        
        addrbuf[0] = (byte) ((startAddr >> 24) & 0xFF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0xFF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0xFF);
        addrbuf[3] = (byte) ( startAddr & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCommand(addrbuf);
        if (res == -1) {
            return 0;
        }
        
        scm.writeSingleByte(comPortHandle, (byte) numBytesToWrite);
        
        scm.writeBytes(comPortHandle, data);
        
        checksum = 0;
        for (res=0; res < numBytesToWrite; res++) {
            checksum = (byte) ((byte)checksum ^ data[res]);
        }
        
        scm.writeSingleByte(comPortHandle, (byte) checksum);
        
        return 0;
    }

}


























