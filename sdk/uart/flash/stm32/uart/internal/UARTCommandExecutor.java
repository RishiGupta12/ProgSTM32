/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.core.Device;
import flash.stm32.core.FileType;
import flash.stm32.core.FlashUtils;
import flash.stm32.core.ICmdProgressListener;
import flash.stm32.core.REGTYPE;
import flash.stm32.core.internal.CommandExecutor;

/**
 * <p>
 * Entity which implements USART protocol required for communication between
 * host computer and bootloader in a stm32 microcontroller.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTCommandExecutor extends CommandExecutor {

    /* Upto 35 seconds maximum wait for erase command to complete. */
    private final int ERASE_TIMEOUT = 35;

    /* General one second timeout */
    private final int TIMEOUT_ONE = 1;

    /* Constants as mentioned in an2606 document */
    private final byte INITSEQ = 0x7F;
    private final byte ACK = 0x79;
    private final byte NACK = 0x1F;
    private final byte[] CMD_GET_ALLOWED_CMDS = new byte[] { (byte) 0x00, (byte) 0xFF };
    private final byte[] CMD_GET_VRPS = new byte[] { (byte) 0x01, (byte) 0xFE };
    private final byte[] CMD_GET_ID = new byte[] { (byte) 0x02, (byte) 0xFD };
    private final byte[] CMD_READ_MEMORY = new byte[] { (byte) 0x11, (byte) 0xEE };
    private final byte[] CMD_GO = new byte[] { (byte) 0x21, (byte) 0xDE };
    private final byte[] CMD_WRITE_MEMORY = new byte[] { (byte) 0x31, (byte) 0xCE };
    private final byte[] CMD_ERASE = new byte[] { (byte) 0x43, (byte) 0xBC };
    private final byte[] CMD_EXTD_ERASE = new byte[] { (byte) 0x44, (byte) 0xBB };
    private final byte[] CMD_WRITE_PROTECT = new byte[] { (byte) 0x63, (byte) 0x9C };
    private final byte[] CMD_WRITE_UNPROTECT = new byte[] { (byte) 0x73, (byte) 0x8C };
    private final byte[] CMD_READOUT_PROTECT = new byte[] { (byte) 0x82, (byte) 0x7D };
    private final byte[] CMD_READOUT_UNPROTECT = new byte[] { (byte) 0x92, (byte) 0x6D };

    private final SerialComManager scm;
    private final ResourceBundle rb;

    private FlashUtils flashUtils;
    private long comPortHandle;
    private int supportedCmds;
    private Device curDev;

    public UARTCommandExecutor(SerialComManager scm, ResourceBundle rb) {

        super();
        this.scm = scm;
        this.rb = rb;
    }

    public Device connectAndIdentifyDevice(long comPortHandle) throws SerialComException, TimeoutException {

        int x;
        int y;
        int z = 0;
        byte[] rcvData = null;

        this.comPortHandle = comPortHandle;

        for (x = 0; x < 5; x++) {
            scm.writeSingleByte(comPortHandle, INITSEQ);

            rcvData = scm.readBytes(comPortHandle);
            if (rcvData != null) {
                y = rcvData.length;
                for (z = 0; z < y; z++) {
                    /*
                     * If stm32 was already in bootloader mode, it will send NACK if command code is
                     * wrong. Sending INITSEQ 5 times is done to send enough data bytes that stm32
                     * can detect as wrong in comparison to what it was expecting and than it has no
                     * choice other than sending NACK.
                     */
                    if ((rcvData[z] == ACK) || (rcvData[z] == NACK)) {
                        break;
                    }
                }
            }
        }

        /*
         * Extra safety; Suppose bootloader was waiting for the next data byte for the
         * command previously executed, then sending invalid data will result in NACK
         * and bootloader getting out of the command execution.
         */
        if ((rcvData != null) && (rcvData[z] == NACK)) {
            scm.writeSingleByte(comPortHandle, (byte) 0xFF);
            rcvData = scm.readBytes(comPortHandle);
            if ((rcvData != null) && (rcvData[z] != NACK)) {
                throw new TimeoutException(rb.getString("init.seq.timedout"));
            }
        }

        if (x >= 3) {
            throw new TimeoutException(rb.getString("init sequence timedout"));
        }

        x = getChipID();
        curDev = dCreator.createDevFromPID(x, this);

        return curDev;
    }

    private int sendCmdOrCmdData(byte[] sndbuf, int timeOutDuration) throws SerialComException, TimeoutException {

        int x;
        byte[] buf = new byte[2];
        long curTime;
        long responseWaitTime;

        scm.writeBytes(comPortHandle, sndbuf);

        responseWaitTime = System.currentTimeMillis() + (1000 * timeOutDuration);

        while (true) {

            x = scm.readBytes(comPortHandle, buf, 0, 1, -1, null); // TODO parity error handling

            if (x > 0) {
                if (buf[0] == ACK) {
                    return 0;
                } else if (buf[0] == NACK) {
                    throw new SerialComException(rb.getString("nack.received"));
                } else {
                }
            }

            curTime = System.currentTimeMillis();
            if (curTime >= responseWaitTime) {
                throw new TimeoutException(rb.getString("operation.timedout"));
            }

            if ((responseWaitTime - curTime) > 1000) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * @return number of bytes read including length of data, ACK/N at end.
     */
    private int receiveResponse(byte[] res) throws SerialComException {

        int x = 0;
        int y = 0;
        int index = 0;
        int numBytesToRead = res.length;

        for (y = 0; y < 3; y++) {
            x = scm.readBytes(comPortHandle, res, index, numBytesToRead, -1, null);
            if (x > 0) {
                index = index + x;
                numBytesToRead = numBytesToRead - x;
                if ((res[index - 1] == ACK) || (res[index - 1] == NACK)) {
                    break;
                }
                if (numBytesToRead <= 0) {
                    break;
                }
            }
        }

        return index;
    }

    /**
     * 
     * @return 0 if operation fails or bit mask of commands supported by given
     *         bootloader.
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int getAllowedCommands() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[32];

        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS, 0);
        if (res == 0) {
            throw new TimeoutException(rb.getString("no.response.from.stm.cmd"));
        }

        res = receiveResponse(buf);
        if (res == 0) {
            throw new TimeoutException(rb.getString("no.response.from.stm"));
        }

        supportedCmds = 0;
        x = 2;
        while ((buf[x] != ACK) && (x < res)) {
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

            case (byte) 0x82:
                supportedCmds = supportedCmds | BLCMDS.READOUT_PROTECT;
                break;

            case (byte) 0x92:
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
     * @throws TimeoutException
     */
    public String getBootloaderVersion() throws SerialComException, TimeoutException {

        int res;
        String bootloaderVersion = null;
        byte[] buf = new byte[32];

        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS, 0);
        if (res == -1) {
            return null;
        }

        res = receiveResponse(buf);
        if (res == -1) {
            return null;
        }

        if (buf[1] == 0x31) {
            bootloaderVersion = new String(rb.getString("bootloader.version.3.1"));
        } else if (buf[1] == 0x30) {
            bootloaderVersion = new String(rb.getString("bootloader.version.3.0"));
        } else if (buf[1] == 0x22) {
            bootloaderVersion = new String(rb.getString("bootloader.version.2.2"));
        } else {
            bootloaderVersion = new String(rb.getString("unknown.version") + " " + buf[1]);
        }

        return bootloaderVersion;
    }

    /**
     * STM32 product codes are defined in AN2606 application note.
     * 
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int getChipID() throws SerialComException, TimeoutException {

        int res;
        byte[] buf = new byte[8];

        res = sendCmdOrCmdData(CMD_GET_ID, 0);
        if (res == -1) {
            return 0;
        }

        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }

        /*
         * note some device may send more than two bytes in response to get chip id
         * command, we ignore extra two bytes sent,as they are of no use to us.
         */
        res = 0;
        res = res | (buf[1] << 8);
        res = res | buf[2];

        return res;
    }

    /**
     * 
     * @return 0 on failure, 1 if enabled, 2 if disabled.
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int getReadProtectionStatus() throws SerialComException, TimeoutException {

        int res;
        byte[] buf = new byte[16];

        res = sendCmdOrCmdData(CMD_GET_VRPS, 0);
        if (res == -1) {
            return 0;
        }

        res = receiveResponse(buf);
        if (res == -1) {
            return 0;
        }

        // TODO exact purpose of byte1,2
        // buf[1]
        // buf[2]

        return res;
    }

    private int readGivenMemory(byte[] data, int startAddr, int numBytesToRead, int index)
            throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] numbuf = new byte[2];
        byte[] addrbuf = new byte[5];

        res = sendCmdOrCmdData(CMD_READ_MEMORY, 0);
        if (res == -1) {
            return 0;
        }

        addrbuf[0] = (byte) ((startAddr >> 24) & 0x000000FF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0x000000FF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0x000000FF);
        addrbuf[3] = (byte) (startAddr & 0x000000FF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);

        res = sendCmdOrCmdData(addrbuf, 0);
        if (res == -1) {
            return 0;
        }

        numbuf[0] = (byte) numBytesToRead;
        numbuf[1] = (byte) (numBytesToRead ^ 0xFF);

        res = sendCmdOrCmdData(numbuf, 0);
        if (res == -1) {
            return 0;
        }

        for (res = 0; res < 2; res++) {
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
     * This API read data from any valid memory address in RAM, main flash memory
     * and the information block (system memory or option byte areas). It may be
     * used by GUI programs where input address is taken from user.
     * 
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int readMemory(byte[] data, int startAddr, final int numBytesToRead, ICmdProgressListener progressListener)
            throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        int index = 0;
        int totalBytesReadTillNow = 0;
        int bytesToRead;

        if (data == null) {
            throw new IllegalArgumentException(rb.getString("null.data.buffer"));
        }
        if (numBytesToRead <= 0) {
            throw new IllegalArgumentException(rb.getString("invalid.dataread.length"));
        }
        if (numBytesToRead > data.length) {
            throw new IllegalArgumentException(rb.getString("shorter.data.buffer"));
        }

        /* read data chunks in multiples of 255 */
        x = numBytesToRead / 255;

        if (x > 0) {
            bytesToRead = 255;
            for (z = 0; z < x; z++) {
                this.readGivenMemory(data, startAddr, bytesToRead, index);
                index = index + 255;
                startAddr = startAddr + 255;
                if (progressListener != null) {
                    totalBytesReadTillNow = totalBytesReadTillNow + 255;
                    progressListener.onDataReadProgressUpdate(totalBytesReadTillNow, numBytesToRead);
                }
            }
        }

        /* read remaining (bytes less than 255 in last read chunk) */
        y = numBytesToRead % 255;

        if (y > 0) {
            this.readGivenMemory(data, startAddr, y, index);
            if (progressListener != null) {
                totalBytesReadTillNow = totalBytesReadTillNow + y;
                progressListener.onDataReadProgressUpdate(totalBytesReadTillNow, numBytesToRead);
            }
            index = index + y;
        }

        return index;
    }

    /**
     * The host should send the base address where the application to jump to is
     * programmed.
     * 
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int goJump(int addrToJumpTo) throws SerialComException, TimeoutException {

        int res;
        byte[] addrbuf = new byte[5];

        res = sendCmdOrCmdData(CMD_GO, 0);
        if (res == -1) {
            return 0;
        }

        addrbuf[0] = (byte) ((addrToJumpTo >> 24) & 0x000000FF);
        addrbuf[1] = (byte) ((addrToJumpTo >> 16) & 0x000000FF);
        addrbuf[2] = (byte) ((addrToJumpTo >> 8) & 0x000000FF);
        addrbuf[3] = (byte) (addrToJumpTo & 0x000000FF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);

        res = sendCmdOrCmdData(addrbuf, 0);
        if (res == -1) {
            return 0;
        }

        // TODO timeout
        while (true) {
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

    private int writeMemoryInBinFormat(final byte[] data, int offset, final int length, final int startAddr)
            throws SerialComException, TimeoutException {

        int x;
        int res;
        int checksum;
        int numBytesToWrite;
        int numPaddingBytes;
        byte[] addrBuf = new byte[5];
        byte[] buf;

        numBytesToWrite = data.length;
        if ((numBytesToWrite > 256) || (numBytesToWrite <= 0)) {
            throw new IllegalArgumentException(rb.getString("invalid.datawrite.length"));
        }

        // TODO is this required for all uC
        if ((startAddr & 0x3) != 0) {
            throw new IllegalArgumentException(rb.getString("addr.must.align"));
        }

        /* send write memory command */
        res = sendCmdOrCmdData(CMD_WRITE_MEMORY, 0);
        if (res == -1) {
            return 0;
        }

        addrBuf[0] = (byte) ((startAddr >> 24) & 0x000000FF);
        addrBuf[1] = (byte) ((startAddr >> 16) & 0x000000FF);
        addrBuf[2] = (byte) ((startAddr >> 8) & 0x000000FF);
        addrBuf[3] = (byte) (startAddr & 0x000000FF);
        addrBuf[4] = (byte) (addrBuf[0] ^ addrBuf[1] ^ addrBuf[2] ^ addrBuf[3]);

        /* send address and checksum of address */
        res = sendCmdOrCmdData(addrBuf, 0);
        if (res == -1) {
            return 0;
        }

        numPaddingBytes = length % 4;
        if (numPaddingBytes > 0) {
            numPaddingBytes = 4 - numPaddingBytes;
        }

        /* send length of data to be written */
        scm.writeSingleByte(comPortHandle, (byte) (length + numPaddingBytes));

        buf = new byte[length + numPaddingBytes];

        for (x = 0; x < length; x++) {
            buf[x] = data[offset];
            offset++;
        }

        if (numPaddingBytes > 0) {
            for (res = 0; res < numPaddingBytes; res++) {
                buf[x] = (byte) 0xFF;
                x++;
            }
        }

        checksum = 0;
        x = length + numPaddingBytes;
        for (res = 0; res < x; res++) {
            checksum = (byte) ((byte) checksum ^ buf[res]);
        }

        /* send actual data flashed + checksum of this data */
        scm.writeBytes(comPortHandle, buf);

        /* wait for physical flashing for 1 sec */
        for (x = 0; x < 2; x++) {
            res = scm.readBytes(comPortHandle, addrBuf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return 0;
                }
                if (buf[0] == NACK) {
                    return -1;
                }
            }
        }

        return 0;
    }

    public int writeMemory(final int fileType, final File fwFile, final int startAddr,
            ICmdProgressListener progressListener) throws TimeoutException, IOException {

        int x = 0;
        int offset = 0;
        int numBytesToRead = 0;
        int numBytesActuallyRead = 0;
        int totalBytesReadTillNow = 0;
        BufferedInputStream inStream = null;
        byte[] data;

        try {
            int lengthOfFileContents = (int) fwFile.length();

            data = new byte[lengthOfFileContents];

            inStream = new BufferedInputStream(new FileInputStream(fwFile));

            numBytesToRead = lengthOfFileContents;
            for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
                numBytesActuallyRead = inStream.read(data, offset, numBytesToRead);
                totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
                offset = totalBytesReadTillNow;
            }

            inStream.close();
        } catch (IOException e) {
            inStream.close();
            throw e;
        }

        return this.writeMemory(fileType, data, startAddr, progressListener);
    }

    /**
     * 
     * <p>
     * Selection of address from where the given firmware should be written has to
     * be chosen carefully. For example; if IAP (in-application circuit) programming
     * is used, few memory from the beginning of flash may not be available.
     * </p>
     * 
     * TODO handle two NACK progressListener can be null.
     * 
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int writeMemory(final int fileType, final byte[] data, final int startAddr,
            ICmdProgressListener progressListener) throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        int numBytesToWrite;
        int offset = 0;
        int beginAddr = startAddr;
        int totalBytesWrittenTillNow = 0;
        byte[] fwBuf;

        if (data == null) {
            throw new IllegalArgumentException(rb.getString("null.data.buffer"));
        }

        numBytesToWrite = data.length;
        if (numBytesToWrite == 0) {
            throw new IllegalArgumentException(rb.getString("invalid.datawrite.length"));
        }

        if (fileType == FileType.HEX) {
            if (flashUtils == null) {
                flashUtils = new FlashUtils(rb);
            }
            fwBuf = flashUtils.hexToBinFwFormat(data);
            numBytesToWrite = fwBuf.length;
        } else if (fileType == FileType.DETECT) {
            // TODO
            fwBuf = data;
        } else if (fileType == FileType.BIN) {
            fwBuf = data;
        } else {
            throw new IllegalArgumentException(rb.getString("invalid.filetype"));
        }

        /* send data in chunk of 256 bytes */
        x = numBytesToWrite / 256;
        if (x > 0) {
            for (z = 0; z < x; z++) {
                this.writeMemoryInBinFormat(fwBuf, offset, 256, beginAddr);
                offset = offset + 256;
                beginAddr = beginAddr + 256;
                if (progressListener != null) {
                    totalBytesWrittenTillNow = totalBytesWrittenTillNow + 256;
                    progressListener.onDataWriteProgressUpdate(totalBytesWrittenTillNow, numBytesToWrite);
                }
            }
        }

        /* send last or chunk of size less than 256 */
        y = numBytesToWrite % 256;
        if (y > 0) {
            this.writeMemoryInBinFormat(fwBuf, offset, y, beginAddr);
            if (progressListener != null) {
                totalBytesWrittenTillNow = totalBytesWrittenTillNow + y;
                progressListener.onDataWriteProgressUpdate(totalBytesWrittenTillNow, numBytesToWrite);
            }
        }

        return 0;
    }

    /**
     * <p>
     * For performing mass erase set memReg to REGTYPE.MAIN, startPageNum to -1 and
     * totalNumOfPages to -1. The total time it takes to perform mass erase varies
     * with processor series, flash characteristics and flash size. STM32 datasheets
     * mentions tME as mass erase time with minimum, maximum and typical values.
     * </p>
     * 
     * <p>
     * Some devices like STM32F4/STM32F7 series may not have flash page concept and
     * instead might organize flash memory into sectors. In such microcontrollers,
     * sector is the smallest possible unit for performing erase operation. To
     * handle these cases this method treat startPageNum starting sector number and
     * totalNumOfPages as total number of sectors.
     * </p>
     * 
     * <p>
     * Some devices like STM32L1 series may have both flash page concept and sector
     * concept where a particular sector may have a group of pages. In such
     * microcontrollers, sector is the smallest possible unit for performing erase
     * operation. To address such cases this method treat startPageNum starting
     * sector number and totalNumOfPages as total number of sectors.
     * </p>
     * 
     * <p>
     * Default bootloader in STM32 microcontrollers does not allow to erase system
     * memory, user data area, option bytes area etc. Therefore only main memory
     * flash area can be erased through this bootloader and command.
     * </p>
     * 
     * @param memReg
     * @param startPageNum
     * @param numOfPages
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int eraseMemoryRegion(final int memReg, final int startPageNum, int totalNumOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        byte[] erasePagesInfo;

        if ((memReg & REGTYPE.MAIN) != REGTYPE.MAIN) {
            throw new IllegalArgumentException(rb.getString("invalid.mem.region"));
        }

        if ((startPageNum != -1) && (totalNumOfPages != -1)) {
            if (startPageNum < 0) {
                throw new IllegalArgumentException(rb.getString("invalid.start.pagenum"));
            }
            /* totalNumOfPages is device dependent, as of now some initial value is kept */
            if ((totalNumOfPages > 1024) || (totalNumOfPages < 0)) {
                throw new IllegalArgumentException(rb.getString("invalid.start.pagenum"));
            }
        }

        res = sendCmdOrCmdData(CMD_ERASE, 0);
        if (res == -1) {
            return 0;
        }

        /* mass erase case */
        if ((startPageNum == -1) && (totalNumOfPages == -1)) {
            erasePagesInfo = new byte[2];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0x00;
            res = sendCmdOrCmdData(erasePagesInfo, ERASE_TIMEOUT);
            if (res == -1) {
                return 0;
            }
            return 0;
        }

        /* non-mass erase case */
        erasePagesInfo = new byte[totalNumOfPages + 2];

        if (totalNumOfPages >= 255) {
            totalNumOfPages = 254;
        }
        erasePagesInfo[0] = (byte) totalNumOfPages;

        x = startPageNum;
        for (res = 1; res < totalNumOfPages; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }

        res = 0;
        i = totalNumOfPages + 1;
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;

        res = sendCmdOrCmdData(erasePagesInfo, ERASE_TIMEOUT);
        if (res == -1) {
            return 0;
        }

        return 0;
    }

    /**
     * <p>
     * For performing mass erase set memReg to REGTYPE.MAIN, startPageNum to -1 and
     * totalNumOfPages to -1.
     * </p>
     * 
     * <p>
     * If memReg is set to REGTYPE.BANK1, mass erase of bank 1 is performed.
     * Similarly if memReg is set to REGTYPE.BANK2, mass erase of bank 2 is
     * performed. Erasing both bank 1 and 2 in one go is not allowed i.e. both
     * REGTYPE.BANK1 and REGTYPE.BANK2 bits should be set together. Consider using
     * global mass erase for this requirement.
     * </p>
     * 
     * @param memReg
     * @param startPageNum
     * @param numOfPages
     * @return
     * @throws SerialComException
     * @throws TimeoutException
     */
    public int extendedEraseMemoryRegion(final int memReg, final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        int totalPages;
        byte[] erasePagesInfo;

        if ((startPageNum == -1) && (totalNumOfPages == -1)) {
            if ((memReg & REGTYPE.MAIN) != REGTYPE.MAIN) {
                throw new IllegalArgumentException(rb.getString("invalid.mem.region"));
            }
        } else {
            if (startPageNum < 0) {
                throw new IllegalArgumentException(rb.getString("invalid.start.pagenum"));
            }
            /* total number of pages is product specific */
            if ((totalNumOfPages > 255) || (totalNumOfPages < 0)) {
                throw new IllegalArgumentException(rb.getString("invalid.number.pages"));
            }
            if ((memReg & (REGTYPE.BANK1 | REGTYPE.BANK2)) == (REGTYPE.BANK1 | REGTYPE.BANK2)) {
                throw new IllegalArgumentException(rb.getString("both.bank.not.allowed"));
            }
        }

        res = sendCmdOrCmdData(CMD_EXTD_ERASE, 0);
        if (res == -1) {
            return 0;
        }

        /* global mass erase case */
        if ((memReg & (REGTYPE.MAIN | REGTYPE.SYSTEM)) == (REGTYPE.MAIN | REGTYPE.SYSTEM)) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFF;
            erasePagesInfo[2] = (byte) 0x00;
            res = sendCmdOrCmdData(erasePagesInfo, 0);
            if (res == -1) {
                return 0;
            }
            return 0;
        }

        /* bank 1 mass erase case */
        if ((memReg & (REGTYPE.BANK1)) == REGTYPE.BANK1) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFE;
            erasePagesInfo[2] = (byte) 0x01;
            res = sendCmdOrCmdData(erasePagesInfo, 0);
            if (res == -1) {
                return 0;
            }
            return 0;
        }

        /* bank 2 mass erase case */
        if ((memReg & (REGTYPE.BANK2)) == REGTYPE.BANK2) {
            erasePagesInfo = new byte[3];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFD;
            erasePagesInfo[2] = (byte) 0x02;
            res = sendCmdOrCmdData(erasePagesInfo, 0);
            if (res == -1) {
                return 0;
            }
            return 0;
        }

        /* certain number of pages case */
        totalPages = totalNumOfPages & 0xFFFF;
        erasePagesInfo = new byte[2 + (2 * totalPages) + 1];

        erasePagesInfo[0] = (byte) ((totalPages >> 8) & 0xFF);
        erasePagesInfo[1] = (byte) (totalPages & 0xFF);

        i = 0;
        x = startPageNum;
        for (res = 2; i < totalPages; res = res + 2) {
            erasePagesInfo[res] = (byte) ((x >> 8) & 0xFF);
            erasePagesInfo[res + 1] = (byte) (x & 0xFF);
            x++;
            i++;
        }

        res = 0;
        i = 2 + (2 * totalPages);
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;

        res = sendCmdOrCmdData(erasePagesInfo, ERASE_TIMEOUT);
        if (res == -1) {
            return 0;
        }

        return 0;
    }

    /**
     * At the end of the write protect command, the bootloader transmits the ACK
     * byte and generates a system reset to take into account, the new configuration
     * of the option byte.
     */
    public int writeProtectMemoryRegion(final int startPageNum, final int numOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        byte[] erasePagesInfo;

        if (startPageNum < 0) {
            throw new IllegalArgumentException(rb.getString("invalid.start.pagenum"));
        }

        // TODO what is max num pages in extended cmd, product specific it is
        if ((numOfPages > 254) || (numOfPages < 0)) {
            throw new IllegalArgumentException(rb.getString("invalid.number.pages"));
        }

        res = sendCmdOrCmdData(CMD_WRITE_PROTECT, 0);
        if (res == -1) {
            return 0;
        }

        // TODO what if numOfPages = 0xFF
        erasePagesInfo = new byte[1 + numOfPages + 1];
        erasePagesInfo[0] = (byte) numOfPages;

        x = startPageNum;
        for (res = 1; res < numOfPages; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }

        res = 0;
        i = numOfPages + 1;
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;

        res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_ONE);
        if (res == -1) {
            return 0;
        }

        return 0;
    }

    public int writeUnprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_WRITE_UNPROTECT, 0);
        if (res == -1) {
            return 0;
        }

        /* one loop is 500 ms, so two loops for 1 second timeout */
        for (x = 0; x < 2; x++) {
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

        return -1;
    }

    public int readoutprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_READOUT_PROTECT, 0);
        if (res == -1) {
            return 0;
        }

        /* one loop is 500 ms, so two loops for 1 second timeout */
        for (x = 0; x < 2; x++) {
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

        return -1;
    }

    public int readoutUnprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_READOUT_UNPROTECT, 0);
        if (res == -1) {
            return 0;
        }

        /* one loop is 500 ms, so two loops for 1.5 second timeout */
        for (x = 0; x < 3; x++) {
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

        return -1;
    }
}
