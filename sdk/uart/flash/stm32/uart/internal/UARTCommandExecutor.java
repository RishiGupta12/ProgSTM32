/* 
 * This file is part of progstm32.
 * 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 * 
 * The progstm32 is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 * 
 * The progstm32 is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation,Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package flash.stm32.uart.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.core.util.SerialComUtil;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.core.BLCMDS;
import flash.stm32.core.Device;
import flash.stm32.core.FileType;
import flash.stm32.core.FlashUtils;
import flash.stm32.core.HexFirmware;
import flash.stm32.core.ICmdProgressListener;
import flash.stm32.core.REGTYPE;
import flash.stm32.core.Reset;
import flash.stm32.core.internal.CommandExecutor;
import flash.stm32.core.internal.Debug;

/**
 * <p>
 * Implements USART protocol used in the STM32 bootloader as per AN3155
 * application note.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTCommandExecutor extends CommandExecutor {

    /* Upto 35 seconds maximum wait for mass erase command to complete. */
    private final int TIMEOUT_MASS_ERASE = 35;

    /*
     * Upto 15 seconds maximum wait for erase command to complete when erasing a
     * bank.
     */
    private final int TIMEOUT_BANK = 15;

    /* Upto 20 seconds maximum wait for erasing 255 pages. */
    private final int TIMEOUT_255_PAGES = 20;

    /* General zero seconds timeout */
    private final int TIMEOUT_ZERO = 0;

    /* General one second timeout */
    private final int TIMEOUT_ONE = 1;

    /* General five second timeout */
    private final int TIMEOUT_FIVE = 5;

    /* Values as per AN2606 document */
    private final byte INITSEQ = 0x7F;
    private final byte ACK = 0x79;
    private final byte NACK = 0x1F;

    /* Command codes with checksum as per AN2606 */
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
    private final FlashUtils flashUtils;
    private final Reset rst;
    private final Debug dbg;

    private long comPortHandle;
    private Device curDev;
    private int blVer;

    /**
     * <p>
     * Allocates an instance of UARTCommandExecutor class.
     * </p>
     * 
     * @param scm
     *            an instance of SerialComManager to communicate through serial port
     * @param rb
     *            an instance of ResourceBundle to get appropriate messages and
     *            resources
     * @param flashUtils
     *            an instance of utility class to carry out common operations
     */
    public UARTCommandExecutor(SerialComManager scm, ResourceBundle rb, FlashUtils flashUtils) {

        super();
        this.scm = scm;
        this.rb = rb;
        this.flashUtils = flashUtils;
        rst = new Reset();
        dbg = new Debug();
    }

    /**
     * <p>
     * Sends init byte 0x7F data frame to stm32 so that stm32 can configure its UART
     * interface. It then wait to receive response from stm32 expecting ACK (0x79)
     * or NACK (0x1F). Once response is received it then sends command to get the
     * PID of stm32. Based on the PID value received it creates corresponding device
     * instance and return to the caller.
     * </p>
     * 
     * <p>
     * If the stm32 can't determine baudrate and initialize its serial port, we
     * can't get into bootloader mode. In this case user must give correct baudrate
     * for his particular device.
     * </p>
     * 
     * @param comPortHandle
     *            handle of serial port to which stm32 is connected
     * @return an instance of Device class representing stm32 device
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public Device initAndIdentifyDevice(long comPortHandle) throws SerialComException, TimeoutException {

        int x;
        int y;
        int z = 0;
        int ackReceived = 0;
        int nackReceived = 0;
        byte[] rcvData = null;

        this.comPortHandle = comPortHandle;

        for (x = 0; x < 4; x++) {
            scm.writeSingleByte(comPortHandle, INITSEQ);

            rcvData = scm.readBytes(comPortHandle);
            if (rcvData != null) {
                y = rcvData.length;
                for (z = 0; z < y; z++) {
                    /*
                     * If stm32 was booted in bootloader mode and waiting for init sequence, it will
                     * send ACK. However if init sequence has already been done, stm32 may or may
                     * not send NACK depending upon what next byte was expected by bootloader. If it
                     * was waiting for command, sending invalid command code may result it in
                     * sending NACK. If it was waiting for data like address or checksum etc after a
                     * command that it has received previously, than we send it enough invalid bytes
                     * such that it has no choice other than sending NACK and aborting current
                     * command execution.
                     */
                    if (rcvData[z] == ACK) {
                        ackReceived = 1;
                        x = 20;
                        break;
                    } else if (rcvData[z] == NACK) {
                        nackReceived = 1;
                    } else {
                    }
                }
            }
        }

        if ((ackReceived == 1) || (nackReceived == 1)) {

            /* create stm32 device based on pid */
            x = getChipID();
            curDev = dCreator.createDevFromPID(x, this);

            /* get bootloader version so that quirks can be handled */
            this.getBootloaderVersion();
        } else {
            throw new TimeoutException(rb.getString("init.to"));
        }

        return curDev;
    }

    /**
     * <p>
     * Internal use only. Sends command or data as part of the command to serial
     * port to which stm32 is connected.
     * </p>
     * 
     * @param sndbuf
     *            data bytes to be sent to serial port
     * @param timeOutDuration
     *            timeout in seconds
     * 
     * @return 0 if ACK is received, -1 if NACK is received, -2 if stm32 sends no
     *         response at all
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             if unexpected data is received from stm32
     */
    private int sendCmdOrCmdData(byte[] sndbuf, int timeOutDuration) throws SerialComException, TimeoutException {

        int x;
        byte[] buf = new byte[2];
        long curTime;
        long responseWaitTime;

        scm.writeBytes(comPortHandle, sndbuf);

        responseWaitTime = System.currentTimeMillis() + (1000 * timeOutDuration);

        while (true) {

            // TODO parity error handling
            x = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);

            if (x > 0) {
                if (buf[0] == ACK) {
                    return 0;
                } else if (buf[0] == NACK) {
                    return -1;
                } else {
                    throw new TimeoutException("Unexpected data: " + SerialComUtil.byteToHexString(buf[0]));
                }
            }

            curTime = System.currentTimeMillis();
            if (curTime >= responseWaitTime) {
                return -2;
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
     * <p>
     * Internal use only. Used by internal methods as helper method to receive data
     * from serial port.
     * </p>
     * 
     * @param res
     *            buffer that will contain response received from stm32 device
     * @return number of bytes read including length of data, ACK, NACK
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     */
    private int receiveResponse(byte[] buf) throws SerialComException {

        int x = 0;
        int y = 0;
        int index = 0;
        int numBytesToRead = buf.length;

        for (y = 0; y < 3; y++) {
            x = scm.readBytes(comPortHandle, buf, index, numBytesToRead, -1, null);
            if (x > 0) {
                index = index + x;
                numBytesToRead = numBytesToRead - x;
                if (buf[index - 1] == NACK) {
                    return -1;
                }
                if ((buf[index - 1] == ACK) || (numBytesToRead <= 0)) {
                    break;
                }
            }
        }

        return index;
    }

    /**
     * <p>
     * Sends command 'Get' (0x00) to stm32 to get commands supported by bootloader
     * running in the stm32 device currently connected to host.
     * </p>
     * 
     * @return 0 if operation fails or bit mask of commands supported by given
     *         bootloader
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int getAllowedCommands() throws SerialComException, TimeoutException {

        int x;
        int res;
        int supportedCmds = 0;
        byte[] buf = new byte[32];

        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        res = receiveResponse(buf);
        if (res <= 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.meta.data"));
            } else if (res == 0) {
                throw new TimeoutException(rb.getString("info.to"));
            } else {
            }
        }

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
     * <p>
     * Sends command 'Get' (0x00) to know commands supported by this bootloader and
     * then extracts bootloader version for the received response.
     * </p>
     * 
     * @return bootloader version in human readable format
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public String getBootloaderVersion() throws SerialComException, TimeoutException {

        int res;
        String bootloaderVersion = null;
        byte[] buf = new byte[32];

        res = sendCmdOrCmdData(CMD_GET_ALLOWED_CMDS, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        res = receiveResponse(buf);
        if (res <= 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.meta.data"));
            } else if (res == 0) {
                throw new TimeoutException(rb.getString("info.to"));
            } else {
            }
        }

        switch (buf[1]) {
        case (byte) 0x6F:
            bootloaderVersion = new String("v11.1");
            blVer = 0x6F;
            break;
        case (byte) 0x6E:
            bootloaderVersion = new String("v11.0");
            blVer = 0x6E;
            break;
        case (byte) 0x67:
            bootloaderVersion = new String("v10.3");
            blVer = 0x67;
            break;
        case (byte) 0x66:
            bootloaderVersion = new String("v10.2");
            blVer = 0x66;
            break;
        case (byte) 0x65:
            bootloaderVersion = new String("v10.1");
            blVer = 0x65;
            break;
        case (byte) 0x64:
            bootloaderVersion = new String("v10.0");
            blVer = 0x64;
            break;
        case (byte) 0x92:
            bootloaderVersion = new String("v9.2");
            blVer = 0x92;
            break;
        case (byte) 0x91:
            bootloaderVersion = new String("v9.1");
            blVer = 0x91;
            break;
        case (byte) 0x90:
            bootloaderVersion = new String("v9.0");
            blVer = 0x90;
            break;
        case 0x70:
            bootloaderVersion = new String("v7.0");
            blVer = 0x70;
            break;
        case 0x52:
            bootloaderVersion = new String("v5.2");
            blVer = 0x52;
            break;
        case 0x50:
            bootloaderVersion = new String("v5.0");
            blVer = 0x50;
            break;
        case 0x45:
            bootloaderVersion = new String("v4.5");
            blVer = 0x45;
            break;
        case 0x41:
            bootloaderVersion = new String("v4.1");
            blVer = 0x41;
            break;
        case 0x40:
            bootloaderVersion = new String("v4.0");
            blVer = 0x40;
            break;
        case 0x33:
            bootloaderVersion = new String("v3.3");
            blVer = 0x33;
            break;
        case 0x32:
            bootloaderVersion = new String("v3.2");
            blVer = 0x32;
            break;
        case 0x31:
            bootloaderVersion = new String("v3.1");
            blVer = 0x31;
            break;
        case 0x30:
            bootloaderVersion = new String("v3.0");
            blVer = 0x30;
            break;
        case 0x22:
            bootloaderVersion = new String("v2.2");
            blVer = 0x22;
            break;
        case 0x21:
            bootloaderVersion = new String("v2.1");
            blVer = 0x21;
            break;
        case 0x10:
            bootloaderVersion = new String("v1.0");
            blVer = 0x10;
            break;
        default:
            bootloaderVersion = new String("unknown.ver" + " " + SerialComUtil.byteToHexString(buf[1]));
            blVer = buf[1];
        }

        return bootloaderVersion;
    }

    /**
     * <p>
     * Sends command 'Get ID' (0x02) to stm32 to get product id.
     * </p>
     * 
     * @return product id of the stm32 as reported by bootloader
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int getChipID() throws SerialComException, TimeoutException {

        int res;
        byte[] buf;

        res = sendCmdOrCmdData(CMD_GET_ID, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /*
         * some stm32 sends 2 bytes presenting pid while some sends 4 bytes. we read all
         * data from serial port so that serial port buffers are clear for next command.
         * we discard extra bytes.
         */
        res = 0;
        buf = scm.readBytes(comPortHandle, 5);

        if (buf != null) {
            if (buf[0] == 2) {
                /* 2 byte pid */
                if (buf.length > 2) {
                    res = res | (buf[1] << 8);
                    res = res | buf[2];
                } else {
                    if (buf.length == 1) {
                        byte[] buf1 = scm.readBytes(comPortHandle, 2);
                        if (buf1 != null) {
                            res = res | (buf1[0] << 8);
                            res = res | buf1[1];
                        } else {
                            throw new TimeoutException(rb.getString("info.to"));
                        }
                    } else {
                        byte[] buf2 = scm.readBytes(comPortHandle, 1);
                        if (buf2 != null) {
                            res = res | (buf[1] << 8);
                            res = res | buf2[0];
                        } else {
                            throw new TimeoutException(rb.getString("info.to"));
                        }
                    }
                }
            } else {
                /* 4 byte pid */
                if (buf.length > 2) {
                    res = res | (buf[1] << 8);
                    res = res | buf[2];
                    if (buf.length < 5) {
                        /* read and discard extra bytes */
                        scm.readBytes(comPortHandle, 5);
                    }
                } else {
                    if (buf.length == 1) {
                        byte[] buf1 = scm.readBytes(comPortHandle, 4);
                        if (buf1 != null) {
                            res = res | (buf1[0] << 8);
                            res = res | buf1[1];
                        } else {
                            throw new TimeoutException(rb.getString("info.to"));
                        }
                    } else {
                        byte[] buf2 = scm.readBytes(comPortHandle, 3);
                        if (buf2 != null) {
                            res = res | (buf[1] << 8);
                            res = res | buf2[0];
                        } else {
                            throw new TimeoutException(rb.getString("info.to"));
                        }
                    }
                }
            }
        } else {
            throw new TimeoutException(rb.getString("info.to"));
        }

        return res;
    }

    /**
     * <p>
     * For the USART interface, two consecutive NACKs instead of 1 NACK are sent
     * when a Read Memory or Write Memory command is sent and the RDP level is
     * active. This method reads that NACK and discard it so that serial port buffer
     * between host and stm32 is clear and ready for next command.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     */
    private void handleReadProtectionQuirk() throws SerialComException {

        int read = 0;

        if ((curDev.pid == 0x444) && (blVer == 0x10)) {
            read = 1;
        } else if ((curDev.pid == 0x440) && (blVer == 0x21)) {
            read = 1;
        } else if ((curDev.pid == 0x411) && (blVer == 0x33)) {
            read = 1;
        } else if ((curDev.pid == 0x413) && (blVer == 0x90)) {
            read = 1;
        } else if ((curDev.pid == 0x427) && (blVer == 0x40)) {
            read = 1;
        } else if ((curDev.pid == 0x436) && (blVer == 0x45)) {
            read = 1;
        } else if ((curDev.pid == 0x437) && (blVer == 0x40)) {
            read = 1;
        } else {
        }

        /*
         * if stm32 does not send any data, this will timeout automatically after 500
         * milliseconds.
         */
        if (read == 1) {
            scm.readBytes(comPortHandle);
        }
    }

    /**
     * <p>
     * Sends command 'Get Version & Read Protection Status' (0x01) to stm32 and
     * extracts read protection status from it.
     * </p>
     * 
     * @return response data received from bootloader as is
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public byte[] getReadProtectionStatus() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[8];
        byte[] data;

        res = sendCmdOrCmdData(CMD_GET_VRPS, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        res = receiveResponse(buf);
        if (res <= 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.meta.data"));
            } else if (res == 0) {
                throw new TimeoutException(rb.getString("info.to"));
            } else {
            }
        }

        data = new byte[res];
        for (x = 0; x < res; x++) {
            data[x] = buf[x];
        }

        return data;
    }

    /**
     * <p>
     * Internal use.
     * </p>
     * 
     * <p>
     * Sends command 'Read Memory command' (0x11) to stm32 to read the data from
     * address till specified length.
     * </p>
     * 
     * @param data
     *            buffer where data read from stm32 will be stored
     * @param startAddr
     *            starting address from where 1 byte will be fetched
     * @param numBytesToRead
     *            total number of bytes to read from the given starting address
     * @param offset
     *            offset in data buffer where data read will be stored
     * @return 0 on success
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    private int readGivenMemory(byte[] data, int startAddr, int numBytesToRead, int offset)
            throws SerialComException, TimeoutException {

        int res;
        int x = 0;
        byte[] numbuf = new byte[2];
        byte[] addrbuf = new byte[5];

        if (dbg.state == true) {
            System.out.println("Read 0x" + SerialComUtil.intToHexString(startAddr) + " len " + numBytesToRead
                    + " offset " + offset);
        }

        res = sendCmdOrCmdData(CMD_READ_MEMORY, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                /* some devices may send 2 NACK */
                handleReadProtectionQuirk();
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /* start address and its checksum */
        addrbuf[0] = (byte) ((startAddr >> 24) & 0x000000FF);
        addrbuf[1] = (byte) ((startAddr >> 16) & 0x000000FF);
        addrbuf[2] = (byte) ((startAddr >> 8) & 0x000000FF);
        addrbuf[3] = (byte) (startAddr & 0x000000FF);
        addrbuf[4] = (byte) (addrbuf[0] ^ addrbuf[1] ^ addrbuf[2] ^ addrbuf[3]);

        res = sendCmdOrCmdData(addrbuf, TIMEOUT_ONE);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.adr.sm"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("adr.sm.to"));
            } else {
            }
        }

        /* total number of bytes to read and its checksum */
        x = numBytesToRead - 1;
        numbuf[0] = (byte) x;
        numbuf[1] = (byte) (x ^ 0xFF);

        res = sendCmdOrCmdData(numbuf, TIMEOUT_ONE);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.r.data"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("num.sm.to"));
            } else {
            }
        }

        /*
         * 1 second timeout between two consecutive bytes read is used here to ensure
         * that we timeout to handle situations like stm32 is removed physically from
         * serial port or stm32 device stops sending data for some reason etc.
         */
        while (numBytesToRead > 0) {
            for (res = 0; res < 2; res++) {
                x = scm.readBytes(comPortHandle, data, offset, numBytesToRead, -1, null);
                if (x > 0) {
                    break;
                }
            }
            if (x > 0) {
                offset = offset + x;
                numBytesToRead = numBytesToRead - x;
            } else {
                throw new TimeoutException(rb.getString("read.to"));
            }
        }

        return 0;
    }

    /**
     * <p>
     * This API read data from any valid memory address in RAM, main flash memory
     * and the information block (system memory or option byte areas). It may be
     * used by GUI programs where input address is taken from user. If the read
     * protection is active bootloader may return NACK.
     * </p>
     * 
     * <p>
     * Sends command 'Read Memory command' (0x11) to stm32 to read the data from
     * address till specified length.
     * </p>
     * 
     * @param data
     *            buffer where data read will be stored
     * @param startAddr
     *            address from where 1st byte will be read
     * @param numBytesToRead
     *            number of bytes to be read
     * @param progressListener
     *            instance of class which implements callback to know reading
     *            progress or null if not required
     * 
     * @return number of bytes read from stm32 device
     * @throws SerialComException
     *             if an error happens when communicating through serial port.
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
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
            throw new IllegalArgumentException(rb.getString("nul.buf"));
        }
        if (numBytesToRead <= 0) {
            throw new IllegalArgumentException(rb.getString("inval.r.len"));
        }
        if (numBytesToRead > data.length) {
            throw new IllegalArgumentException(rb.getString("inval.buf"));
        }

        /* read data chunks in multiples of 256 */
        x = numBytesToRead / 256;

        if (x > 0) {
            bytesToRead = 256;
            for (z = 0; z < x; z++) {
                this.readGivenMemory(data, startAddr, bytesToRead, index);
                index = index + 256;
                startAddr = startAddr + 256;
                if (progressListener != null) {
                    totalBytesReadTillNow = totalBytesReadTillNow + 256;
                    progressListener.onDataReadProgressUpdate(totalBytesReadTillNow, numBytesToRead);
                }
            }
        }

        /* read remaining (bytes less than 256 in last read chunk) */
        y = numBytesToRead % 256;

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
     * <p>
     * This API read data from any valid memory address in RAM, main flash memory
     * and the information block (system memory or option byte areas). It may be
     * used by GUI programs where input address is taken from user. If the read
     * protection is active bootloader may return NACK.
     * </p>
     * 
     * <p>
     * Sends command 'Read Memory command' (0x11) to stm32 to read the data from
     * address till specified length.
     * </p>
     * 
     * @param file
     *            absolute path to file which will be written by this method
     * @param startAddr
     *            address from where 1st byte will be read
     * @param numBytesToRead
     *            number of bytes to be read
     * @param progressListener
     *            instance of class which implements callback to know reading
     *            progress or null if not required
     * 
     * @return number of bytes read from stm32 device
     * @throws SerialComException
     *             if an error happens when communicating through serial port.
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int readMemory(String file, int startAddr, final int numBytesToRead, ICmdProgressListener progressListener)
            throws SerialComException, TimeoutException {

        int x = 0;
        FileOutputStream fout = null;

        if (file == null) {
            throw new IllegalArgumentException(rb.getString("inval.file"));
        }

        try {
            fout = new FileOutputStream(file, false);
        } catch (Exception e) {
            throw new IllegalArgumentException(rb.getString("inval.file"));
        }

        BufferedOutputStream bout = new BufferedOutputStream(fout);

        byte[] data = new byte[numBytesToRead];
        x = readMemory(data, startAddr, numBytesToRead, progressListener);

        try {
            bout.write(data);
            bout.flush();
            bout.close();
        } catch (Exception e) {
            try {
                bout.close();
            } catch (IOException e1) {
            }
        }

        return x;
    }

    /**
     * <p>
     * Sends command 'Go command' (0x21) to stm32 to make program counter jump to
     * the given address.
     * </p>
     * 
     * @param addrToJumpTo
     *            address where program counter should jump
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void goJump(int addrToJumpTo) throws SerialComException, TimeoutException {

        int res;
        byte[] addrBuf = new byte[5];

        res = sendCmdOrCmdData(CMD_GO, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                /* some devices may send 2 NACK */
                handleReadProtectionQuirk();
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /* set address */
        addrBuf[0] = (byte) ((addrToJumpTo >> 24) & 0x000000FF);
        addrBuf[1] = (byte) ((addrToJumpTo >> 16) & 0x000000FF);
        addrBuf[2] = (byte) ((addrToJumpTo >> 8) & 0x000000FF);
        addrBuf[3] = (byte) (addrToJumpTo & 0x000000FF);

        /* set checksum */
        addrBuf[4] = (byte) (addrBuf[0] ^ addrBuf[1] ^ addrBuf[2] ^ addrBuf[3]);

        /* bootloader sends response and then jumps to given address */
        res = sendCmdOrCmdData(addrBuf, 0);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.extr.data"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("data.cmd.to"));
            } else {
            }
        }
    }

    /**
     * <p>
     * Internal use. Actually writes data to stm32 main flash, option byte or RAM
     * area. Data must be in binary format.
     * </p>
     * 
     * <p>
     * Sends command 'Write Memory command' (0x31) to stm32 to write to memory.
     * </p>
     * 
     * @param data
     *            data bytes to be written to given memory area
     * @param offset
     *            offset in data buffer from which 1st byte should be fetched
     * @param length
     *            number of data bytes to be written from given data buffer (0 <
     *            length <= 256)
     * @param startAddr
     *            memory address in stm32 from which flashing should start
     * @return 0 on success
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    private int writeMemoryInBinFormat(final byte[] data, int offset, final int length, final int startAddr)
            throws SerialComException, TimeoutException {

        int x;
        int res;
        int checksum;
        int numPaddingBytes;
        byte[] addrBuf = new byte[5];
        byte[] buf;

        if (dbg.state == true) {
            System.out.println(
                    "Write 0x" + SerialComUtil.intToHexString(startAddr) + " len " + length + " offset " + offset);
        }

        /* send write memory command */
        res = sendCmdOrCmdData(CMD_WRITE_MEMORY, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                /* some devices may send 2 NACK */
                handleReadProtectionQuirk();
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        addrBuf[0] = (byte) ((startAddr >> 24) & 0xFF);
        addrBuf[1] = (byte) ((startAddr >> 16) & 0xFF);
        addrBuf[2] = (byte) ((startAddr >> 8) & 0xFF);
        addrBuf[3] = (byte) (startAddr & 0xFF);
        addrBuf[4] = (byte) (addrBuf[0] ^ addrBuf[1] ^ addrBuf[2] ^ addrBuf[3]);

        /* send address and checksum of address */
        res = sendCmdOrCmdData(addrBuf, TIMEOUT_ONE);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.adr.sm"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("adr.sm.to"));
            } else {
            }
        }

        /* N + 1 should be multiple of 4 */
        numPaddingBytes = length % 4;
        if (numPaddingBytes > 0) {
            numPaddingBytes = 4 - numPaddingBytes;
        }

        /* total number of bytes + actual data bytes + padding + checksum */
        buf = new byte[length + numPaddingBytes + 2];

        /* number of bytes to be sent */
        buf[0] = (byte) (length + numPaddingBytes - 1);

        /* actual firmware data */
        res = length + 1;
        for (x = 1; x < res; x++) {
            buf[x] = data[offset];
            offset++;
        }

        /* padding if data not multiple of 4 */
        if (numPaddingBytes > 0) {
            for (res = 0; res < numPaddingBytes; res++) {
                buf[x] = (byte) 0xFF;
                x++;
            }
        }

        /* checksum of 'total number of data byte' + actual data + padding */
        checksum = 0;
        x = length + numPaddingBytes + 1;
        for (res = 0; res < x; res++) {
            checksum = checksum ^ buf[res];
        }
        buf[x] = (byte) checksum;

        scm.writeBytes(comPortHandle, buf);

        /* wait for 2 seconds for write operation to complete */
        for (x = 0; x < 4; x++) {
            res = scm.readBytes(comPortHandle, addrBuf, 0, 1, -1, null);
            if (res > 0) {
                if (addrBuf[0] == ACK) {
                    return 0;
                }
                if (addrBuf[0] == NACK) {
                    throw new TimeoutException(rb.getString("nack.w.data"));
                }
            }
        }

        throw new TimeoutException(rb.getString("write.to"));
    }

    /**
     * <p>
     * Writes given data to the memory region specified starting from the given
     * starting address.
     * 
     * Selection of address from where the given firmware should be written has to
     * be chosen carefully. For example; if IAP (in-application circuit) programming
     * is used, few memory from the beginning of flash may not be available.
     * </p>
     * 
     * <p>
     * Sends command 'Write Memory command' (0x31) to stm32 to write to memory.
     * Caller should give correct addresses, for example if a particular product
     * requires aligned addresses than that address must be given.
     * </p>
     * 
     * @param fwType
     *            bitmask FileType.HEX or FileType.BIN
     * @param data
     *            data bytes to be written to given memory area
     * @param startAddr
     *            memory address in stm32 from where writing should start
     * @param progressListener
     *            instance of class which implements callback methods to know how
     *            many bytes have been sent till now or null if not required
     * @return 0 on success
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int writeMemory(final int fwType, final byte[] data, final int startAddr,
            ICmdProgressListener progressListener) throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        int numBytesToWrite = 0;
        int offset = 0;
        int beginAddr = startAddr;
        int totalBytesWrittenTillNow = 0;
        byte[] fwBuf;

        if (data == null) {
            throw new IllegalArgumentException(rb.getString("nul.buf"));
        }
        if (data.length == 0) {
            throw new IllegalArgumentException(rb.getString("inval.buf"));
        }

        switch (fwType) {

        case FileType.HEX:
            HexFirmware hf = flashUtils.hexToBinFwFormat(data);
            beginAddr = hf.address;
            fwBuf = hf.fwInBinFormat;
            numBytesToWrite = fwBuf.length;

            if (dbg.state == true) {
                System.out.println("flashaddr: " + beginAddr);
            }
            break;

        case FileType.BIN:
            fwBuf = data;
            numBytesToWrite = data.length;
            break;

        default:
            throw new IllegalArgumentException(rb.getString("inval.fl.tp"));
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
     * Writes given data to the memory region specified starting from the given
     * starting address.
     * 
     * Selection of address from where the given firmware should be written has to
     * be chosen carefully. For example; if IAP (in-application circuit) programming
     * is used, few memory from the beginning of flash may not be available.
     * </p>
     * 
     * <p>
     * Sends command 'Write Memory command' (0x31) to stm32 to write to memory.
     * </p>
     * 
     * @param fwType
     *            bitmask FileType.HEX or FileType.BIN
     * @param fwFile
     *            firmware file to be flashed
     * @param startAddr
     *            memory address in stm32 from where writing should start, this will
     *            be extracted from the hex firmware file itself
     * @param progressListener
     *            instance of class which implements callback methods to know how
     *            many bytes have been sent till now or null if not required
     * @return 0 on success
     * @throws SerialComException
     *             if an error happens when communicating through serial port.
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int writeMemory(final int fwType, final File fwFile, final int startAddr,
            ICmdProgressListener progressListener) throws IOException, TimeoutException {

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
                numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
            }

            inStream.close();
        } catch (IOException e) {
            inStream.close();
            throw e;
        }

        return this.writeMemory(fwType, data, startAddr, progressListener);
    }

    /**
     * <p>
     * Sends command 'Erase Memory command' (0x43) to stm32 to erase given memory
     * region.
     * </p>
     * 
     * @param memReg
     *            bitmask REGTYPE.MAIN
     * @param startPageNum
     *            starting page number from where erasing should start
     * @param totalNumOfPages
     *            total number of pages which should be erased
     * @param timeout
     *            maximum time to wait for erase operation to complete
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    private void eraseGivenMemReg(final int memReg, final int startPageNum, final int totalNumOfPages,
            final int timeout) throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        byte[] erasePagesInfo;

        res = sendCmdOrCmdData(CMD_ERASE, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /* non-mass erase case */
        erasePagesInfo = new byte[totalNumOfPages + 2];

        /* set total number of pages */
        erasePagesInfo[0] = (byte) ((totalNumOfPages - 1) & 0xFF);

        /* set page codes */
        x = startPageNum;
        for (res = 1; res < totalNumOfPages; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }

        /* set checksum */
        res = 0;
        i = erasePagesInfo.length;
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i - 1] = (byte) (res & 0xFF);

        res = sendCmdOrCmdData(erasePagesInfo, timeout);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.extr.data"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("data.cmd.to"));
            } else {
            }
        }
    }

    /**
     * <p>
     * Sends command 'Erase Memory command' (0x43) to stm32 to erase given memory
     * region.
     * </p>
     * 
     * <p>
     * For performing mass erase set memReg to REGTYPE.MAIN, startPageNum to -1 and
     * totalNumOfPages to -1. The total time it takes to perform mass erase varies
     * with processor series, flash characteristics and flash size. STM32 datasheets
     * mentions tME as mass erase time with minimum, maximum and typical values.
     * Some devices does not support mass erase. For these devices consider using
     * page by page erase.
     * </p>
     * 
     * <p>
     * Default bootloader in STM32 microcontrollers does not allow to erase system
     * memory, user data area, option bytes area etc. Therefore only main memory
     * flash area can be erased through default bootloader using this erase command.
     * However, when we wish to modify contents of option bytes, we can write
     * required data to it.
     * </p>
     * 
     * @param memReg
     *            bitmask REGTYPE.MAIN
     * @param startPageNum
     *            starting page number from where erasing should start
     * @param totalNumOfPages
     *            total number of pages which should be erased
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void eraseMemoryRegion(final int memReg, final int startPageNum, int totalNumOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        int res;
        byte[] erasePagesInfo;

        if ((startPageNum == -1) && (totalNumOfPages == -1)) {
            if ((memReg & REGTYPE.MAIN) != REGTYPE.MAIN) {
                throw new IllegalArgumentException(rb.getString("inval.mem.rg"));
            }
        } else {
            if (startPageNum < 0) {
                throw new IllegalArgumentException(rb.getString("inval.pg.start"));
            }
            /* total number of pages is product specific */
            if (totalNumOfPages < 0) {
                throw new IllegalArgumentException(rb.getString("inval.num.pg"));
            }
        }

        /* mass erase case */
        if ((startPageNum == -1) && (totalNumOfPages == -1)) {

            res = sendCmdOrCmdData(CMD_ERASE, TIMEOUT_ZERO);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.prot"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("prot.cmd.to"));
                } else {
                }
            }

            erasePagesInfo = new byte[2];
            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0x00;

            res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_MASS_ERASE);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.extr.data"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("data.cmd.to"));
                } else {
                }
            }
            return;
        }

        int beginPageNum = startPageNum;

        /* erase in chunk of 255 pages in one erase command execution */
        x = totalNumOfPages / 255;
        if (x > 0) {
            for (z = 0; z < x; z++) {
                this.eraseGivenMemReg(memReg, beginPageNum, 255, TIMEOUT_255_PAGES);
                beginPageNum = beginPageNum + 255;
            }
        }

        /* erase last or chunk of total pages less than 255 */
        y = totalNumOfPages % 255;
        if (y > 0) {
            this.eraseGivenMemReg(memReg, beginPageNum, y, TIMEOUT_255_PAGES);
        }
    }

    /**
     * <p>
     * Sends command 'Extended Erase Memory command' (0x44) to stm32 to erase given
     * memory region.
     * </p>
     * 
     * <p>
     * For performing mass erase set memReg to REGTYPE.MAIN, startPageNum to -1 and
     * totalNumOfPages to -1. The total time it takes to perform mass erase varies
     * with processor series, flash characteristics and flash size. STM32 datasheets
     * mentions tME as mass erase time with minimum, maximum and typical values.
     * Some devices does not support mass erase. For these devices consider using
     * page by page erase.
     * </p>
     * 
     * <p>
     * Default bootloader in STM32 microcontrollers does not allow to erase system
     * memory, user data area, option bytes area etc. Therefore only main memory
     * flash area can be erased through default bootloader using this erase command.
     * However, when we wish to modify contents of option bytes, we can write
     * required data to it.
     * </p>
     * 
     * @param memReg
     *            bitmask REGTYPE.MAIN
     * @param startPageNum
     *            starting page number from where erasing should start
     * @param totalNumOfPages
     *            total number of pages which should be erased
     * @param timeout
     *            maximum time to wait for erase operation to complete
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    private void extendedEraseGivenMemReg(final int memReg, final int startPageNum, final int totalNumOfPages,
            final int timeout) throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        int totalPages;
        byte[] erasePagesInfo;

        res = sendCmdOrCmdData(CMD_EXTD_ERASE, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /* 2 byte number of pages - 1, page numbers in 2 bytes, 1 byte checksum */
        totalPages = totalNumOfPages & 0xFFFF;
        erasePagesInfo = new byte[3 + (2 * totalPages)];

        /* set total number of pages */
        totalPages = totalPages - 1;
        erasePagesInfo[0] = (byte) ((totalPages >> 8) & 0xFF);
        erasePagesInfo[1] = (byte) (totalPages & 0xFF);

        /* set page codes */
        totalPages = totalPages + 1;
        i = 0;
        x = startPageNum;
        for (res = 2; i < totalPages; res = res + 2) {
            erasePagesInfo[res] = (byte) ((x >> 8) & 0xFF);
            erasePagesInfo[res + 1] = (byte) (x & 0xFF);
            x++;
            i++;
        }

        /* set checksum */
        res = 0;
        i = erasePagesInfo.length;
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i - 1] = (byte) (res & 0xFF);

        /*
         * caller can specify large number of pages and therefore no
         * assumption/calculation about timeout can be made.
         */
        res = sendCmdOrCmdData(erasePagesInfo, timeout);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.extr.data"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("data.cmd.to"));
            } else {
            }
        }
    }

    /**
     * 
     * <p>
     * Sends command 'Extended Erase Memory command' (0x44) to stm32 to erase given
     * memory region.
     * </p>
     * 
     * <p>
     * Erase given memory region using extended erase command. For performing mass
     * erase set memReg to REGTYPE.MAIN, startPageNum to -1 and totalNumOfPages to
     * -1.
     * </p>
     * 
     * <p>
     * If memReg is set to REGTYPE.BANK1, mass erase of bank 1 will be performed.
     * Similarly, if memReg is set to REGTYPE.BANK2, mass erase of bank 2 will be
     * performed. Erasing both bank 1 and 2 in one go is not allowed i.e. both
     * REGTYPE.BANK1 and REGTYPE.BANK2 bits should be set together. Consider using
     * global mass erase for this requirement.
     * </p>
     * 
     * @param memReg
     *            bitmask REGTYPE.MAIN
     * @param startPageNum
     *            starting page number from where erasing should start
     * @param totalNumOfPages
     *            total number of pages which should be erased
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void extendedEraseMemoryRegion(final int memReg, final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        int res;
        byte[] erasePagesInfo;

        if ((startPageNum == -1) && (totalNumOfPages == -1)) {
            if ((memReg & REGTYPE.MAIN) != REGTYPE.MAIN) {
                throw new IllegalArgumentException(rb.getString("inval.mem.rg"));
            }
        } else {
            if (startPageNum < 0) {
                throw new IllegalArgumentException(rb.getString("inval.pg.start"));
            }
            /* total number of pages is product specific */
            if (totalNumOfPages < 0) {
                throw new IllegalArgumentException(rb.getString("inval.num.pg"));
            }
            if ((memReg & (REGTYPE.BANK1 | REGTYPE.BANK2)) == (REGTYPE.BANK1 | REGTYPE.BANK2)) {
                throw new IllegalArgumentException(rb.getString("bank.not.alwd"));
            }
        }

        /* global mass erase case */
        if ((startPageNum == -1) && (totalNumOfPages == -1)) {

            res = sendCmdOrCmdData(CMD_EXTD_ERASE, TIMEOUT_ZERO);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.prot"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("prot.cmd.to"));
                } else {
                }
            }

            erasePagesInfo = new byte[3];

            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFF;
            erasePagesInfo[2] = (byte) 0x00;

            res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_MASS_ERASE);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.extr.data"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("data.cmd.to"));
                } else {
                }
            }
            return;
        }

        /* bank 1 mass erase case */
        if ((memReg & (REGTYPE.BANK1)) == REGTYPE.BANK1) {

            res = sendCmdOrCmdData(CMD_EXTD_ERASE, TIMEOUT_ZERO);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.prot"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("prot.cmd.to"));
                } else {
                }
            }

            erasePagesInfo = new byte[3];

            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFE;
            erasePagesInfo[2] = (byte) 0x01;

            res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_BANK);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.extr.data"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("data.cmd.to"));
                } else {
                }
            }
            return;
        }

        /* bank 2 mass erase case */
        if ((memReg & (REGTYPE.BANK2)) == REGTYPE.BANK2) {

            res = sendCmdOrCmdData(CMD_EXTD_ERASE, TIMEOUT_ZERO);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.prot"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("prot.cmd.to"));
                } else {
                }
            }

            erasePagesInfo = new byte[3];

            erasePagesInfo[0] = (byte) 0xFF;
            erasePagesInfo[1] = (byte) 0xFD;
            erasePagesInfo[2] = (byte) 0x02;

            res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_BANK);
            if (res < 0) {
                if (res == -1) {
                    throw new TimeoutException(rb.getString("nack.extr.data"));
                } else if (res == -2) {
                    throw new TimeoutException(rb.getString("data.cmd.to"));
                } else {
                }
            }
            return;
        }

        /* certain number of pages case */
        int beginPageNum = startPageNum;

        /* erase in chunk of 255 pages in one erase command execution */
        x = totalNumOfPages / 255;
        if (x > 0) {
            for (z = 0; z < x; z++) {
                this.extendedEraseGivenMemReg(memReg, beginPageNum, 255, TIMEOUT_255_PAGES);
                beginPageNum = beginPageNum + 255;
            }
        }

        /* erase last or chunk of total pages less than 255 */
        y = totalNumOfPages % 255;
        if (y > 0) {
            this.extendedEraseGivenMemReg(memReg, beginPageNum, y, TIMEOUT_255_PAGES);
        }
    }

    /**
     * <p>
     * Sends command 'Write Protect command' (0x63) to stm32. It enables write
     * protection on the flash memory sectors specified as argument to this method.
     * After successful operation, bootloader generates a system reset to take into
     * account, new configuration of the option byte.
     * </p>
     * 
     * <p>
     * Readout protection must not be active for write protection to work.
     * </p>
     * 
     * <p>
     * The bootloader may not validate number of sectors and their addresses.
     * Further, if a sector was protected previously and write protection is enabled
     * with new sector numbers, only latest protection is effective, i.e. any sector
     * protected previously and not covered in latest command will become
     * unprotected.
     * </p>
     * 
     * @param startPageNum
     *            page number from which protection is to be activated
     * @param totalNumOfPages
     *            total number of pages to be protected (0 < totalNumOfPages < 255)
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void writeProtectMemoryRegion(final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {

        int x;
        int i;
        int res;
        byte[] erasePagesInfo;

        /*
         * different stm32 have different level of granularity for write protection, for
         * ex; four pages for low- and medium-density devices and two pages for
         * high-density and connectivity line devices.
         */

        if (startPageNum < 0) {
            throw new IllegalArgumentException(rb.getString("inval.pg.start"));
        }

        if ((totalNumOfPages > 255) || (totalNumOfPages < 0)) {
            throw new IllegalArgumentException(rb.getString("inval.num.pg"));
        }

        res = sendCmdOrCmdData(CMD_WRITE_PROTECT, TIMEOUT_ONE);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        erasePagesInfo = new byte[totalNumOfPages + 2];

        /* set total number of pages to be protected */
        erasePagesInfo[0] = (byte) (totalNumOfPages - 1);

        /* set page numbers */
        x = startPageNum;
        i = totalNumOfPages + 1;
        for (res = 1; res < i; res++) {
            erasePagesInfo[res] = (byte) x;
            x++;
        }

        /* set checksum */
        res = 0;
        for (x = 0; x < i; x++) {
            res ^= erasePagesInfo[x];
        }
        erasePagesInfo[i] = (byte) res;

        res = sendCmdOrCmdData(erasePagesInfo, TIMEOUT_FIVE);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.extr.data"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("data.cmd.to"));
            } else {
            }
        }
    }

    /**
     * <p>
     * Sends command 'Write Unprotect command' (0x73) to stm32. It disables write
     * protection of all the flash memory sectors. After successful operation,
     * bootloader generates a system reset to take into account, new configuration
     * of the option byte.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void writeUnprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_WRITE_UNPROTECT, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        /* one loop is 500 milliseconds, so 1.5 second timeout */
        for (x = 0; x < 3; x++) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return;
                }
                if (buf[0] == NACK) {
                    throw new TimeoutException(rb.getString("nack.resp"));
                }
            }
        }
        throw new TimeoutException(rb.getString("operatin.to"));
    }

    /**
     * <p>
     * Sends command 'Readout Protect command' (0x82) to stm32. If read protection
     * is already enabled, bootloader sends NACK. After successful operation,
     * bootloader generates a system reset to take into account, new configuration
     * of the option byte. If a read protection is active, some of the bootloader
     * commands may not be available to the host computer.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void readoutprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_READOUT_PROTECT, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        for (x = 0; x < 3; x++) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return;
                }
                if (buf[0] == NACK) {
                    throw new TimeoutException(rb.getString("nack.resp"));
                }
            }
        }
        throw new TimeoutException(rb.getString("operatin.to"));
    }

    /**
     * <p>
     * Sends command 'Readout Unprotect command' (0x92) to stm32. If read
     * un-protection fails bootloader sends NACK. After successful operation,
     * bootloader generates a system reset to take into account, new configuration
     * of the option byte. If a read protection is active, some of the bootloader
     * commands may not be available, removing read protection ensures full command
     * set from bootloader is available to the host computer.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void readoutUnprotectMemoryRegion() throws SerialComException, TimeoutException {

        int x;
        int res;
        byte[] buf = new byte[2];

        res = sendCmdOrCmdData(CMD_READOUT_UNPROTECT, TIMEOUT_ZERO);
        if (res < 0) {
            if (res == -1) {
                throw new TimeoutException(rb.getString("nack.prot"));
            } else if (res == -2) {
                throw new TimeoutException(rb.getString("prot.cmd.to"));
            } else {
            }
        }

        for (x = 0; x < 3; x++) {
            res = scm.readBytes(comPortHandle, buf, 0, 1, -1, null);
            if (res > 0) {
                if (buf[0] == ACK) {
                    return;
                }
                if (buf[0] == NACK) {
                    throw new TimeoutException(rb.getString("nack.resp"));
                }
            }
        }
        throw new TimeoutException(rb.getString("operatin.to"));
    }

    /**
     * <p>
     * Reads bootloader ID programmed into last two byte of the device's system
     * memory.
     * </p>
     * 
     * @return bootloader ID
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int getBootloaderID() throws SerialComException, TimeoutException {

        byte[] bufWordData = new byte[4];
        byte[] bufBLID = new byte[2];

        readMemory(bufWordData, curDev.IBSysMemEndAddr - 0x03, 4, null);

        bufBLID[0] = bufWordData[3];
        bufBLID[1] = bufWordData[2];
        String hexStr = SerialComUtil.byteArrayToHexString(bufBLID, null);

        return Integer.parseInt(hexStr, 16);
    }

    /**
     * <p>
     * Loads reset program in RAM and executes it. This method is used mainly to
     * reset stm32 programmatically.
     * </p>
     * 
     * @param resetCodeAddress
     *            address in RAM where reset code will be put
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void triggerSystemReset(int resetCodeAddress) throws SerialComException, TimeoutException {

        /*
         * If RAM is not applicable for device throw exception. If two or more devices
         * have same pid but different bootloader ID, than based on this id deicde
         * whether to throw exception or we have valid RAM address. If two or more
         * devices have same pid but different starting RAM address we take higher RAM
         * address if that address also falls in RAM address range of devices with lower
         * RAM starting address. This information is carried by resetCodeAddress.
         */
        switch (curDev.pid) {
        case 0x442:
            if (getBootloaderID() != 0x52) {
                throw new TimeoutException(rb.getString("inval.op.stm"));
            }
            break;
        case 0x448:
            if (getBootloaderID() != 0xA1) {
                throw new TimeoutException(rb.getString("inval.op.stm"));
            }
            break;
        case 0x445:
        case 0x457:
            throw new TimeoutException(rb.getString("inval.op.stm"));
        default:
        }

        if (resetCodeAddress != 0x00) {
            byte[] resetCode = rst.getResetCode(resetCodeAddress);
            this.writeMemory(FileType.BIN, resetCode, resetCodeAddress, null);
            this.goJump(resetCodeAddress);
            return;
        }

        throw new TimeoutException(rb.getString("inval.op.stm"));
    }
}
