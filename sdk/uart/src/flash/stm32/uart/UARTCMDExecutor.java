/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.uart.internal.SystemProperties;

public final class UARTCMDExecutor {

    /**
     * <p>
     * Production release version of this UART STM32 flasher sdk.
     * </p>
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
    private final byte[] CMD_ERASE = new byte[] { (byte)0x43, (byte)0xBC };
    private final byte[] CMD_EXTD_ERASE = new byte[] { (byte)0x44, (byte)0xBB };
    private final byte[] CMD_WRITE_PROTECT = new byte[] { (byte)0x63, (byte)0x9C };
    private final byte[] CMD_WRITE_UNPROTECT = new byte[] { (byte)0x73, (byte)0x8C };
    private final byte[] CMD_READOUT_PROTECT = new byte[] { (byte)0x82, (byte)0x7D };
    private final byte[] CMD_READOUT_UNPROTECT = new byte[] { (byte)0x92, (byte)0x6D };
    
    private final SerialComManager scm;
    private final SystemProperties sprop;

    private long comPortHandle;
    int supportedCmds;

    public UARTCMDExecutor(String libName) throws IOException {

        sprop = new SystemProperties();
        String tmpDir = sprop.getJavaIOTmpDir();

        scm = new SerialComManager(libName, tmpDir, true, false);

    }

    public void connectToBootloader(String port, SerialComManager.BAUDRATE baudRate, 
            SerialComManager.DATABITS dataBits, SerialComManager.STOPBITS stopBits, 
            SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException, TimeoutException {

        int x;
        
        comPortHandle = scm.openComPort(port, true, true, true);

        scm.configureComPortData(comPortHandle, dataBits, stopBits, parity, baudRate, 0);

        scm.configureComPortControl(comPortHandle, flowCtrl, 'x', 'x', false, false);

        // 500 milliseconds timeout on serial port read
        scm.fineTuneReadBehaviour(comPortHandle, 0, 5, 100, 5, 200);
        
        scm.clearPortIOBuffers(comPortHandle, true, true);
        
        for (x=0; x < 3; x++) {
            scm.writeSingleByte(comPortHandle, (byte) 0x7F);
            byte[] rcvData = scm.readBytes(comPortHandle);
            if ((rcvData != null) && (rcvData[0] == ACK)) {
                return;
            }
        }
        
        throw new TimeoutException("init sequence timedout");
    }

    public void disconnectFromBootloader() throws SerialComException {
        
        scm.closeComPort(comPortHandle);
    }
    
    private int sendCmdOrCmdData(byte[] sndbuf) throws SerialComException {
        
        int x;
        byte[] buf = new byte[2];
        
        scm.writeBytes(comPortHandle, sndbuf);
        
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
        byte[] buf = new byte[32];
        
        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS);
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
        
        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS);
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
        
        res = sendCmdOrCmdData(CMD_GET_ID);
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
        
        res = sendCmdOrCmdData(CMD_GET_VRPS);
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
     * @return 
     * @throws SerialComException
     */
    public int readMemory(byte[] data, int startAddr, int numBytesToRead) throws SerialComException {
        
        int x;
        int res;
        int index = 0;
        byte[] addrbuf = new byte[5];
        byte[] numbuf = new byte[2];
        
        if(data == null) {
            throw new IllegalArgumentException("Data buffer can't be null");
        }
        if ((numBytesToRead > 256) || (numBytesToRead <= 0)) {
            throw new IllegalArgumentException("The numBytesToRead must be between 1 to 256");
        }
        if (numBytesToRead > data.length) {
            throw new IllegalArgumentException("Data buffer is small");
        }
        
        res = sendCmdOrCmdData(CMD_READ_MEMORY);
        if (res == -1) {
            return 0;
        }
        
        addrbuf[0] = (byte) ((startAddr >> 24) & 0xFF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0xFF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0xFF);
        addrbuf[3] = (byte) ( startAddr & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCmdOrCmdData(addrbuf);
        if (res == -1) {
            return 0;
        }
        
        numbuf[0] = (byte) numBytesToRead;
        numbuf[1] = (byte) (numBytesToRead ^ 0xFF);
        
        res = sendCmdOrCmdData(numbuf);
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
     * @return 
     * @throws SerialComException
     */
    public int goJump(int addrToJumpTo) throws SerialComException {
        
        int res;
        byte[] addrbuf = new byte[5];
        
        res = sendCmdOrCmdData(CMD_GO);
        if (res == -1) {
            return 0;
        }
        
        addrbuf[0] = (byte) ((addrToJumpTo >> 24) & 0xFF);
        addrbuf[1] = (byte) ((addrToJumpTo >> 16) & 0xFF);
        addrbuf[2] = (byte) ((addrToJumpTo>> 8) & 0xFF);
        addrbuf[3] = (byte) ( addrToJumpTo & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCmdOrCmdData(addrbuf);
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
     * 
     * TODO handle two NACK
     * @return 
     * @throws SerialComException
     */
    public int writeMemory(final byte[] data, int startAddr) throws SerialComException {
        
        int res;
        int x = 0;
        int checksum;
        int requiredPad ;
        int numBytesToWrite;
        byte[] paddingData = null;
        byte[] addrbuf = new byte[5];

        if (data == null) {
            throw new IllegalArgumentException("Data buffer can't be null");
        }
        
        numBytesToWrite = data.length;
        if ((numBytesToWrite > 256) || (numBytesToWrite == 0)) {
            throw new IllegalArgumentException("Inappropriate data buffer size");
        }
        
        //TODO is this required for all uC
        if ((startAddr & 0x3) != 0) {
            throw new IllegalArgumentException("The startAddr must be 32 bit aligned");
        }
        
        res = sendCmdOrCmdData(CMD_WRITE_MEMORY);
        if (res == -1) {
            return 0;
        }
        
        addrbuf[0] = (byte) ((startAddr >> 24) & 0xFF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0xFF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0xFF);
        addrbuf[3] = (byte) ( startAddr & 0xFF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);
        
        res = sendCmdOrCmdData(addrbuf);
        if (res == -1) {
            return 0;
        }
        
        requiredPad = (numBytesToWrite + 1) % 4;
        
        if (requiredPad  > 0) {
            requiredPad  = 4 - requiredPad ;
            paddingData = new byte[requiredPad];
            x = paddingData.length;
            for (res=0; res < x; res++) {
                paddingData[res] = (byte) 0xFF;
            }
        }
        
        checksum = 0;
        for (res=0; res < numBytesToWrite; res++) {
            checksum = (byte) ((byte)checksum ^ data[res]);
        }
        
        if (requiredPad  > 0) {
            for (res=0; res < x; res++) {
                checksum = (byte) ((byte)checksum ^ paddingData[res]);
            }
        }
        
        scm.writeSingleByte(comPortHandle, (byte) (numBytesToWrite + requiredPad));
        
        scm.writeBytes(comPortHandle, data);
        
        if (requiredPad  > 0) {
            scm.writeBytes(comPortHandle, paddingData);
        }
        
        scm.writeSingleByte(comPortHandle, (byte) checksum);
        
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
     * <p>
     * If memReg has both REGTYPE.MAIN and REGTYPE.SYSTEM bits set, mass erase is performed. In this 
     * case remaining arguments are ignored.
     * </p>
     * 
     * @param memReg
     * @param startPageNum
     * @param numOfPages
     * @return
     * @throws SerialComException
     */
    public int eraseMemoryRegion(final int memReg, final int startPageNum, final int numOfPages) 
            throws SerialComException {
        
        int x;
        int i;
        int res;
        byte[] erasePagesInfo;
        
        if (startPageNum < 0) {
            throw new IllegalArgumentException("Invalid startPageNum");
        }
        
        if ((numOfPages > 254) || (numOfPages < 0)) {
            throw new IllegalArgumentException("Invalid numOfPages");
        }
        
        res = sendCmdOrCmdData(CMD_ERASE);
        if (res == -1) {
            return 0;
        }
        
        // mass erase case
        if ((memReg & (REGTYPE.MAIN | REGTYPE.SYSTEM)) == (REGTYPE.MAIN | REGTYPE.SYSTEM)) {
            erasePagesInfo = new byte[2];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0x00;
            //TODO should mass erase ack will take more time than normal commands, if yes then add timeout parameters to sendCommand API
            res = sendCmdOrCmdData(erasePagesInfo);
            if (res == -1) {
                return 0;
            }
            return 0;
        }
        
        // non-mass erase case
        erasePagesInfo = new byte[numOfPages + 2];
        erasePagesInfo[0] = (byte) numOfPages;
        
        x = startPageNum;
        for (res=1; res < numOfPages; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }
        
        res = 0;
        i = numOfPages + 1;
        for (x=0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;
        
        //TODO total timeout
        res = sendCmdOrCmdData(erasePagesInfo);
        if (res == -1) {
            return 0;
        }
        
        return 0;
    }
    
    /**
     * <p>
     * If memReg has REGTYPE.MAIN and REGTYPE.SYSTEM bits set, mass erase is performed. In this 
     * case remaining arguments are ignored.
     * </p>
     * 
     * <p>
     * If memReg is set to REGTYPE.BANK1, mass erase of bank 1 is performed. Similarly if memReg is set to 
     * REGTYPE.BANK2, mass erase of bank 2 is performed. Erasing both bank 1 and 2 in one go is not allowed 
     * i.e. both REGTYPE.BANK1 and REGTYPE.BANK2 bits should be set together. Consider using global mass erase 
     * for this requirement.
     * </p>
     * 
     * @param memReg
     * @param startPageNum
     * @param numOfPages
     * @return
     * @throws SerialComException
     */
    public int extendedEraseMemoryRegion(final int memReg, final int startPageNum, final int numOfPages) 
            throws SerialComException {
        
        int x;
        int i;
        int res;
        int totalPages;
        byte[] erasePagesInfo;
        
        if (startPageNum < 0) {
            throw new IllegalArgumentException("Invalid startPageNum");
        }
        
        //TODO what is max num pages in extended cmd, product specific it is
        if ((numOfPages > 254) || (numOfPages < 0)) {
            throw new IllegalArgumentException("Invalid numOfPages");
        }
        
        if ((memReg & (REGTYPE.BANK1 | REGTYPE.BANK2)) == (REGTYPE.BANK1 | REGTYPE.BANK2)) {
            throw new IllegalArgumentException("Both bank1 and bank2 bits can't be set");
        }
        
        res = sendCommand(CMD_EXTD_ERASE);
        if (res == -1) {
            return 0;
        }
        
        // global mass erase case
        if ((memReg & (REGTYPE.MAIN | REGTYPE.SYSTEM)) == (REGTYPE.MAIN | REGTYPE.SYSTEM)) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFF;
            erasePagesInfo[2] = (byte) 0x00;
            //TODO should mass erase ack will take more time than normal commands, if yes then add timeout parameters to sendCommand API
            res = sendCommand(erasePagesInfo);
            if (res == -1) {
                return 0;
            }
            return 0;
        }
        
        // bank 1 mass erase case
        if ((memReg & (REGTYPE.BANK1)) == REGTYPE.BANK1) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFE;
            erasePagesInfo[2] = (byte) 0x01;
            //TODO should mass erase ack will take more time than normal commands, if yes then add timeout parameters to sendCommand API
            res = sendCommand(erasePagesInfo);
            if (res == -1) {
                return 0;
            }
            return 0;
        }
        
        // bank 2 mass erase case
        if ((memReg & (REGTYPE.BANK2)) == REGTYPE.BANK2) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFD;
            erasePagesInfo[2] = (byte) 0x02;
            //TODO should mass erase ack will take more time than normal commands, if yes then add timeout parameters to sendCommand API
            res = sendCommand(erasePagesInfo);
            if (res == -1) {
                return 0;
            }
            return 0;
        }
        
        // certain number of pages case
        totalPages = numOfPages & 0xFFFF;
        erasePagesInfo = new byte[2 + (2 * totalPages) + 1];
        
        erasePagesInfo[0] = (byte) ((totalPages >> 8) & 0xFF);
        erasePagesInfo[1] = (byte) (totalPages & 0xFF);
        
        i = 0;
        x = startPageNum;
        for (res=2; i < totalPages; res = res + 2) {
            erasePagesInfo[res] = (byte) ((x >> 8) & 0xFF);
            erasePagesInfo[res + 1] = (byte) (x & 0xFF);
            x++;
            i++;
        }
        
        res = 0;
        i = 2 + (2 * totalPages);
        for (x=0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;
        
        //TODO total timeout
        res = sendCommand(erasePagesInfo);
        if (res == -1) {
            return 0;
        }
        
        return 0;
    }
    
    public int writeProtectMemoryRegion(final int startPageNum, final int numOfPages) 
            throws SerialComException {
        
        int x;
        int i;
        int res;
        byte[] erasePagesInfo;
        
        if (startPageNum < 0) {
            throw new IllegalArgumentException("Invalid startPageNum");
        }
        
        //TODO what is max num pages in extended cmd, product specific it is
        if ((numOfPages > 254) || (numOfPages < 0)) {
            throw new IllegalArgumentException("Invalid numOfPages");
        }
        
        res = sendCommand(CMD_WRITE_PROTECT);
        if (res == -1) {
            return 0;
        }
        
        //TODO what if numOfPages = 0xFF
        erasePagesInfo = new byte[1 + numOfPages + 1];
        erasePagesInfo[0] = (byte) numOfPages;
        
        x = startPageNum;
        for (res=1; res < numOfPages; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }
        
        res = 0;
        i = numOfPages + 1;
        for (x=0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;
        
        //TODO total timeout
        res = sendCommand(erasePagesInfo);
        if (res == -1) {
            return 0;
        }
        
        return 0;
    }
    
    public int writeUnprotectMemoryRegion() throws SerialComException {
        
        int res;
        byte[] buf = new byte[2];
        
        res = sendCommand(CMD_WRITE_UNPROTECT);
        if (res == -1) {
            return 0;
        }
        
        //TODO timeout
        while(true) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return 0;
                }
                if (buf[0] == NACK) {
                    return -1;
                }
            }
        }
    }
    
    public int readoutprotectMemoryRegion() throws SerialComException {
        
        int res;
        byte[] buf = new byte[2];
        
        res = sendCommand(CMD_READOUT_PROTECT);
        if (res == -1) {
            return 0;
        }
        
        //TODO timeout
        while(true) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return 0;
                }
                if (buf[0] == NACK) {
                    return -1;
                }
            }
        }
    }
    
    public int readoutUnprotectMemoryRegion() throws SerialComException {
        
        int res;
        byte[] buf = new byte[2];
        
        res = sendCommand(CMD_READOUT_UNPROTECT);
        if (res == -1) {
            return 0;
        }
        
        //TODO timeout
        while(true) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return 0;
                }
                if (buf[0] == NACK) {
                    return -1;
                }
            }
        }
    }
}
