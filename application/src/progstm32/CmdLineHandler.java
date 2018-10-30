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

package progstm32;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import flash.stm32.core.BLCMDS;
import flash.stm32.core.Device;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.DeviceManager.IFace;
import flash.stm32.core.FileType;
import flash.stm32.core.FlashUtils;
import flash.stm32.core.HexFirmware;
import flash.stm32.core.ICmdProgressListener;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

import com.serialpundit.core.util.SerialComUtil;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

/* If the application is executing in command line mode, it extracts arguments
 * and execute the user given command. */
public final class CmdLineHandler implements ICmdProgressListener {

    final int ACT_BL_ENTRY = 0x01;
    final int ACT_READ_UNPROTECT = 0x02;
    final int ACT_WRITE_UNPROTECT = 0x04;
    final int ACT_GET_PID = 0x08;
    final int ACT_GET_BLID = 0x10;
    final int ACT_MASS_ERASE = 0x20;
    final int ACT_ERASE = 0x40;
    final int ACT_WRITE = 0x80;
    final int ACT_READ = 0x100;
    final int ACT_WRITE_PROTECT = 0x200;
    final int ACT_READ_PROTECT = 0x400;
    final int ACT_SOFT_RESET = 0x800;
    final int ACT_GO = 0x1000;
    final int ACT_BL_EXIT = 0x2000;
    final int ACT_SHOW_HELP = 0x4000;

    private DeviceManager devMgr;
    private UARTInterface uci;
    private Device dev;
    private boolean opened = false;
    private int allowedCmds = 0;

    /* 1 for DTR, 2 for RTS */
    private int firstSignalToSet = -1;
    private boolean entryDTRstate1 = false;
    private boolean entryRTSstate1 = false;
    private boolean entryDTRstate2 = false;
    private boolean entryRTSstate2 = false;
    private boolean exitDTRstate1 = false;
    private boolean exitRTSstate1 = false;
    private boolean exitDTRstate2 = false;
    private boolean exitRTSstate2 = false;
    private int i;

    public void process(String[] args) {

        int numArgs = args.length;

        int action = 0;
        int startPageNum = 0;
        int totalPageNum = 0;
        int baudrate = 0;
        BAUDRATE brate = null;
        int fileType = -1;
        int startAddress = -1;
        int length = -1;
        String device = null;
        boolean verifyWrite = false;
        File fwFile = null;
        boolean stdout = false;

        int x = 0;
        int offset = 0;
        int numBytesToRead = 0;
        int numBytesActuallyRead = 0;
        int totalBytesReadTillNow = 0;
        BufferedInputStream inStream = null;
        byte[] wrtBuf = null;
        int numBytesRead = 0;
        byte[] readBuf = null;
        int lengthOfFileContents = 0;
        String readFile = null;
        long time = 0;
        int holdTime = 5;

        if (numArgs == 0) {
            System.out.println(
                    "Usage: progstm32 -d port [-{r|w} filename] [-{bn|ih}] [-e {m | start total}] [-s address] [-l length] [-kopjnivhR] [-br baudrate] [-g address] [-{er|ex} -{dtr|rts} a b -{dtr|rts} c d]");
            System.out.println("Try 'progstm32 --help' for more information.");
            return;
        }

        for (i = 0; i < numArgs; i++) {

            switch (args[i]) {

            case "-w":
                action |= ACT_WRITE;
                i++;
                try {
                    fwFile = new File(args[i]);
                    if (fwFile.exists() == false) {
                        System.out.println("Firmware file doesn't exist");
                        return;
                    }
                    if (fwFile.isFile() == false) {
                        System.out.println("Invalid firmware file");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid start address, " + e.getMessage());
                    return;
                }
                break;

            case "-v":
                verifyWrite = true;
                break;

            case "-ih":
                fileType = FileType.HEX;
                break;

            case "-bn":
                fileType = FileType.BIN;
                break;

            case "-s":
                i++;
                try {
                    startAddress = Integer.parseInt(args[i], 16);
                } catch (Exception e) {
                    System.out.println("Invalid start address, " + e.getMessage());
                    return;
                }
                break;

            case "-l":
                i++;
                try {
                    length = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    System.out.println("Invalid length, " + e.getMessage());
                    return;
                }
                break;

            case "-e":
                i++;
                if (args[i].equals("m")) {
                    action |= ACT_MASS_ERASE;
                } else {
                    action |= ACT_ERASE;
                    try {
                        startPageNum = Integer.parseInt(args[i]);
                        i++;
                        totalPageNum = Integer.parseInt(args[i]);
                    } catch (Exception e) {
                        System.out.println("Invalid erase option, " + e.getMessage());
                        return;
                    }
                }
                break;

            case "-r":
                action |= ACT_READ;
                i++;
                if (args[i].equals("stdout")) {
                    stdout = true;
                } else {
                    readFile = args[i];
                }
                break;

            case "-br":
                i++;
                try {
                    baudrate = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    System.out.println("Invalid baudrate, " + e.getMessage());
                    return;
                }
                break;

            case "-d":
                i++;
                device = args[i];
                break;

            case "-g":
                action |= ACT_GO;
                i++;
                try {
                    startAddress = Integer.parseInt(args[i], 16);
                } catch (Exception e) {
                    System.out.println("Invalid go address, " + e.getMessage());
                    return;
                }
                break;

            case "-j":
                action |= ACT_READ_PROTECT;
                break;

            case "-k":
                action |= ACT_READ_UNPROTECT;
                break;

            case "-n":
                action |= ACT_WRITE_PROTECT;
                i++;
                try {
                    startPageNum = Integer.parseInt(args[i]);
                    i++;
                    totalPageNum = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    System.out.println("Invalid write protect option, " + e.getMessage());
                    return;
                }
                break;

            case "-o":
                action |= ACT_WRITE_UNPROTECT;
                break;

            case "-p":
                action |= ACT_GET_PID;
                break;

            case "-i":
                action |= ACT_GET_BLID;
                break;

            case "-er":
                action |= ACT_BL_ENTRY;
                i++;
                if (extractEntrySequence(args, 1) < 0) {
                    return;
                }
                i++;
                if (extractEntrySequence(args, 0) < 0) {
                    return;
                }
                break;

            case "-t":
                i++;
                try {
                    holdTime = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    System.out.println("Invalid hold time, " + e.getMessage());
                    return;
                }
                break;

            case "-ex":
                action |= ACT_BL_EXIT;
                i++;
                if (extractExitSequence(args) < 0) {
                    return;
                }
                i++;
                if (extractExitSequence(args) < 0) {
                    return;
                }
                break;

            case "-R":
                action |= ACT_SOFT_RESET;
                break;

            case "-h":
            case "--help":
                showHelp();
                return;

            default:
                System.out.println("Invalid option " + args[i]);
                return;
            }
        }

        /* Check mandatory options has been supplied by user */
        if ((device == null) || (device.length() == 0)) {
            System.out.println("Communication port not given");
            return;
        }

        /*
         * All option has been parsed, let us execute user given command. The action
         * must contain only one primary action and other info given is supplement to
         * the primary command.
         */
        try {
            devMgr = new DeviceManager(new Locale("English", "EN"));
            uci = (UARTInterface) devMgr.getCommunicationIface(IFace.UART, "progstm32jqix7");
        } catch (Exception e) {
            System.out.println("Can't get device mgr & communication interface: " + e.getMessage());
            return;
        }

        /* If user has given baudrate use it, if not than use default 115200 */
        brate = translateBaudrate(baudrate);
        try {
            uci.open(device, brate, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_EVEN, FLOWCONTROL.NONE);
            opened = true;
        } catch (Exception e) {
            System.out.println("Can't open device " + device + " : " + e.getMessage());
            return;
        }

        /* Make stm32 enter bootloader mode by applying sequence as specified by user */
        if ((action & ACT_BL_ENTRY) == ACT_BL_ENTRY) {
            System.out.println("Executing bootmode entry seq...");
            try {
                if (firstSignalToSet == 1) {
                    uci.setDTR(entryDTRstate1);
                    uci.setRTS(entryRTSstate1);
                } else {
                    uci.setRTS(entryRTSstate1);
                    uci.setDTR(entryDTRstate1);
                }
                try {
                    time = System.currentTimeMillis() + holdTime;
                    Thread.sleep(holdTime);
                } catch (InterruptedException e) {
                    long y = time - System.currentTimeMillis();
                    System.out.println("Wokeup " + y + " milliseconds early during entry sequence");
                }
                if (firstSignalToSet == 1) {
                    if (entryDTRstate2 != entryDTRstate1) {
                        uci.setDTR(entryDTRstate2);
                    }
                    if (entryRTSstate2 != entryRTSstate1) {
                        uci.setRTS(entryRTSstate2);
                    }
                } else {
                    if (entryRTSstate2 != entryRTSstate1) {
                        uci.setRTS(entryRTSstate2);
                    }
                    if (entryDTRstate2 != entryDTRstate1) {
                        uci.setDTR(entryDTRstate2);
                    }
                }
                System.out.println("Sequence done.");
            } catch (Exception e) {
                System.out.println("Can't execute bootloader mode sequence: " + e.getMessage());
                closeDevice();
            }
        }

        try {
            dev = uci.initAndIdentifyDevice();
        } catch (Exception e) {
            System.out.println("Can't init device: " + e.getMessage());
            closeDevice();
            return;
        }

        /* Disable read protection */
        if ((action & ACT_READ_UNPROTECT) == ACT_READ_UNPROTECT) {
            try {
                dev.readoutUnprotectMemoryRegion();
                System.out.println("Disabled read protection");
                if ((action > ACT_READ_UNPROTECT) && (reinit() == -1)) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Can't disable read protection: " + e.getMessage());
                closeDevice();
            }
            if (action <= ACT_READ_UNPROTECT) {
                return;
            }
        }

        /* Get commands supported by bootloader in connected stm32 device */
        try {
            allowedCmds = dev.getAllowedCommands();
        } catch (Exception e) {
            System.out.println("Can't get commands supported by bootloader: " + e.getMessage());
            closeDevice();
        }

        /* Disable write protection */
        if ((action & ACT_WRITE_UNPROTECT) == ACT_WRITE_UNPROTECT) {
            if ((allowedCmds & BLCMDS.WRITE_UNPROTECT) == BLCMDS.WRITE_UNPROTECT) {
                try {
                    dev.writeUnprotectMemoryRegion();
                    System.out.println("Disabled write protection");
                    if ((action > ACT_WRITE_UNPROTECT) && (reinit() == -1)) {
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Can't disable write protection: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't support disabling write protection");
            }
            if (action <= ACT_WRITE_UNPROTECT) {
                return;
            }
        }

        /* Get product id of the stm32 device */
        if ((action & ACT_GET_PID) == ACT_GET_PID) {
            if ((allowedCmds & BLCMDS.GET_ID) == BLCMDS.GET_ID) {
                try {
                    System.out.println("Pid : " + dev.getChipID());
                } catch (Exception e) {
                    System.out.println("Can't get product ID: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't reading product id");
            }
            if (action <= ACT_GET_PID) {
                return;
            }
        }

        /* Get bootloader id of the stm32 device */
        if ((action & ACT_GET_BLID) == ACT_GET_BLID) {
            try {
                System.out.println("Blid : " + dev.getBootloaderID());
            } catch (Exception e) {
                System.out.println("Can't get bootloader ID: " + e.getMessage());
                closeDevice();
            }
            if (action <= ACT_GET_BLID) {
                return;
            }
        }

        /* Do mass erase */
        if ((action & ACT_MASS_ERASE) == ACT_MASS_ERASE) {
            System.out.println("Doing mass erase...");
            try {
                if ((allowedCmds & BLCMDS.ERASE) == BLCMDS.ERASE) {
                    dev.eraseMemoryRegion(REGTYPE.MAIN, -1, -1);
                } else if ((allowedCmds & BLCMDS.EXTENDED_ERASE) == BLCMDS.EXTENDED_ERASE) {
                    dev.extendedEraseMemoryRegion(REGTYPE.MAIN, -1, -1);
                } else {
                    System.out.println("Bootloader doesn't support mass erase");
                }
                System.out.println("Mass erase done");
            } catch (Exception e) {
                System.out.println("Can't do mass erase: " + e.getMessage());
                closeDevice();
            }
            if (action <= ACT_MASS_ERASE) {
                return;
            }
        }

        /* Do page by page erase */
        if ((action & ACT_ERASE) == ACT_ERASE) {
            System.out.println("Doing erase...");
            try {
                if ((allowedCmds & BLCMDS.ERASE) == BLCMDS.ERASE) {
                    dev.eraseMemoryRegion(REGTYPE.MAIN, startPageNum, totalPageNum);
                } else if ((allowedCmds & BLCMDS.EXTENDED_ERASE) == BLCMDS.EXTENDED_ERASE) {
                    dev.extendedEraseMemoryRegion(REGTYPE.MAIN, startPageNum, totalPageNum);
                } else {
                    System.out.println("Bootloader doesn't support erase");
                }
                System.out.println("Erase done");
            } catch (Exception e) {
                System.out.println("Can't erase: " + e.getMessage());
                closeDevice();
            }
            if (action <= ACT_ERASE) {
                return;
            }
        }

        /* Write given firmware in stm32 memory */
        if ((action & ACT_WRITE) == ACT_WRITE) {
            try {
                if ((fileType != -1) && (fileType != FileType.HEX) && (fileType != FileType.BIN)) {
                    System.out.println("Invalid file type");
                    return;
                }
                System.out.println("Writing...");
                dev.writeMemory(fileType, fwFile, startAddress, this);
                System.out.println("Write done");
            } catch (Exception e) {
                System.out.println("Can't write: " + e.getMessage());
                closeDevice();
            }

            /* Verify data written if requested by user */
            if (verifyWrite == true) {
                System.out.println("Verifying data written...");
                try {
                    lengthOfFileContents = (int) fwFile.length();
                    wrtBuf = new byte[lengthOfFileContents];
                    inStream = new BufferedInputStream(new FileInputStream(fwFile));
                    numBytesToRead = lengthOfFileContents;
                    for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
                        numBytesActuallyRead = inStream.read(wrtBuf, offset, numBytesToRead);
                        totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
                        offset = totalBytesReadTillNow;
                        numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
                    }
                    inStream.close();
                    x = 1;
                } catch (Exception e) {
                    System.out.println("Can't read fw file in host PC: " + e.getMessage());
                    closeDevice();
                    try {
                        inStream.close();
                    } catch (Exception e1) {
                    }
                    x = 0;
                }

                if (x == 1) {
                    /* Convert from Hex format to bin format if needed */
                    try {
                        if (fileType == FileType.HEX) {
                            FlashUtils fu = new FlashUtils();
                            HexFirmware hf = fu.hexToBinFwFormat(wrtBuf);
                            wrtBuf = hf.fwInBinFormat;
                        } else {
                        }
                        x = 1;
                    } catch (Exception e) {
                        System.out.println("Can't convert from hex to bin format: " + e.getMessage());
                        closeDevice();
                        x = 0;
                    }
                }

                if (x == 1) {
                    /* Read data written to flash */
                    try {
                        readBuf = new byte[lengthOfFileContents];
                        numBytesRead = dev.readMemory(readBuf, startAddress, lengthOfFileContents, null);
                        System.out.println("Read from flash : " + numBytesRead);
                        x = 1;
                    } catch (Exception e) {
                        System.out.println("Can't read flash: " + e.getMessage());
                        closeDevice();
                        x = 0;
                    }
                }

                if (x == 1) {
                    /* Data byte written must be equal to the data byte read */
                    lengthOfFileContents = wrtBuf.length;
                    for (x = 0; x < lengthOfFileContents; x++) {
                        if (wrtBuf[x] != readBuf[x]) {
                            System.out.println(
                                    "Mismatch at byte number " + x + " expected " + wrtBuf[x] + " found " + readBuf[x]);
                            return;
                        }
                    }

                    System.out.println("Verification done");
                }
            }
            if (action <= ACT_WRITE) {
                return;
            }
        }

        /* Read from stm32 memory */
        if ((action & ACT_READ) == ACT_READ) {
            System.out.println("Reading...");
            if ((allowedCmds & BLCMDS.READ_MEMORY) == BLCMDS.READ_MEMORY) {
                try {
                    if (stdout == true) {
                        readBuf = new byte[length];
                        numBytesRead = dev.readMemory(readBuf, startAddress, length, this);
                        String str = SerialComUtil.byteArrayToHexString(readBuf, " ");
                        System.out.println(str);
                    } else {
                        dev.readMemory(readFile, startAddress, length, this);
                    }
                    System.out.println("Read done");
                } catch (Exception e) {
                    System.out.println("Can't read flash: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't support reading memory");
            }
            if (action <= ACT_READ) {
                return;
            }
        }

        /* Enable write protection */
        if ((action & ACT_WRITE_PROTECT) == ACT_WRITE_PROTECT) {
            System.out.println("Enabling write protection...");
            if ((allowedCmds & BLCMDS.WRITE_PROTECT) == BLCMDS.WRITE_PROTECT) {
                try {
                    dev.writeProtectMemoryRegion(startPageNum, totalPageNum);
                    System.out.println("Enabled write protection");
                    if ((action > ACT_WRITE_PROTECT) && (reinit() == -1)) {
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Can't enable write protection: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't support enabling write protection");
            }
            if (action <= ACT_WRITE_PROTECT) {
                return;
            }
        }

        /* Enable read protection */
        if ((action & ACT_READ_PROTECT) == ACT_READ_PROTECT) {
            System.out.println("Enabling read protection...");
            if ((allowedCmds & BLCMDS.READOUT_PROTECT) == BLCMDS.READOUT_PROTECT) {
                try {
                    dev.readoutprotectMemoryRegion();
                    System.out.println("Enabled read protection");
                    if ((action > ACT_READ_PROTECT) && (reinit() == -1)) {
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Can't enable read protection: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't support enabling read protection");
            }
            if (action <= ACT_READ_PROTECT) {
                return;
            }
        }

        /* Do soft system reset */
        if ((action & ACT_SOFT_RESET) == ACT_SOFT_RESET) {
            System.out.println("Soft resetting...");
            try {
                dev.triggerSystemReset();
                System.out.println("Soft reset done");
            } catch (Exception e) {
                System.out.println("Can't soft reset: " + e.getMessage());
                closeDevice();
            }
        }

        /* Make program counter jump to the user given address */
        if ((action & ACT_GO) == ACT_GO) {
            System.out.println("Starting execution at address 0x" + Integer.toHexString(startAddress));
            if ((allowedCmds & BLCMDS.GO) == BLCMDS.GO) {
                try {
                    dev.goJump(startAddress);
                    System.out.println("Started execution at address 0x" + Integer.toHexString(startAddress));
                } catch (Exception e) {
                    System.out.println("Can't jump/execute: " + e.getMessage());
                    closeDevice();
                }
            } else {
                System.out.println("Bootloader doesn't support go command");
            }
            if (action <= ACT_GO) {
                return;
            }
        }

        /* Make stm32 exit bootloader mode by applying sequence as specified by user */
        if ((action & ACT_BL_EXIT) == ACT_BL_EXIT) {
            System.out.println("Executing bootmode exit seq...");
            try {
                if (firstSignalToSet == 1) {
                    if (entryDTRstate2 != exitDTRstate1) {
                        uci.setDTR(exitDTRstate1);
                    }
                    if (entryRTSstate2 != exitRTSstate1) {
                        uci.setRTS(exitRTSstate1);
                    }
                } else {
                    if (entryRTSstate2 != exitRTSstate1) {
                        uci.setRTS(exitRTSstate1);
                    }
                    if (entryDTRstate2 != exitDTRstate1) {
                        uci.setDTR(exitDTRstate1);
                    }
                }
                try {
                    time = System.currentTimeMillis() + holdTime;
                    Thread.sleep(holdTime);
                } catch (InterruptedException e) {
                    long y = time - System.currentTimeMillis();
                    System.out.println("Wokeup " + y + " milliseconds early during exit sequence");
                }
                if (firstSignalToSet == 1) {
                    if (exitDTRstate2 != exitDTRstate1) {
                        uci.setDTR(exitDTRstate2);
                    }
                    if (exitRTSstate2 != exitRTSstate1) {
                        uci.setRTS(exitRTSstate2);
                    }
                } else {
                    if (exitRTSstate2 != exitRTSstate1) {
                        uci.setRTS(exitRTSstate2);
                    }
                    if (exitDTRstate2 != exitDTRstate1) {
                        uci.setDTR(exitDTRstate2);
                    }
                }
                System.out.println("Sequence done.");
            } catch (Exception e) {
                System.out.println("Can't exit bootloader mode: " + e.getMessage());
                closeDevice();
            }
        }

        /* Processing completed, let's go back home */
        closeDevice();
        return;
    }

    /*
     * Translate baudrate to a form as expected by serialpundit sdk. If the stm32
     * can't determine baudrate and initialize its serial port, we can't get into
     * bootloader mode. In this case user must give correct baudrate explicitly for
     * his particular device.
     */
    BAUDRATE translateBaudrate(int baudrate) {
        switch (baudrate) {
        case 9600:
            return BAUDRATE.B9600;
        case 115200:
            return BAUDRATE.B115200;
        case 2400:
            return BAUDRATE.B2400;
        case 4800:
            return BAUDRATE.B4800;
        case 14400:
            return BAUDRATE.B14400;
        case 19200:
            return BAUDRATE.B19200;
        case 28800:
            return BAUDRATE.B28800;
        case 38400:
            return BAUDRATE.B38400;
        case 56000:
            return BAUDRATE.B56000;
        case 57600:
            return BAUDRATE.B57600;
        default:
            return BAUDRATE.B115200;
        }
    }

    /*
     * Some command trigger system reset after they have been executed, so we need
     * to re-init (handshake with bootloader) again so that next commands if given
     * by user can be sent to it.
     */
    int reinit() {
        try {
            Thread.sleep(300);
            dev = uci.initAndIdentifyDevice();
        } catch (Exception e) {
            System.out.println("Can't reinit device: " + e.getMessage());
            closeDevice();
            return -1;
        }
        return 0;
    }

    /*
     * Close serial port
     */
    void closeDevice() {
        try {
            if (opened == true) {
                uci.close();
            }
        } catch (Exception e) {
            System.out.println("Closing serial port failed: " + e.getMessage());
        }
    }

    /*
     * Extract DTR and RTS signal values for exit bootloader mode sequence.
     */
    int extractExitSequence(final String[] args) {

        if (args[i].equals("-dtr")) {
            i++;
            if (args[i].equals("1")) {
                exitDTRstate1 = true;
            } else if (args[i].equals("0")) {
                exitDTRstate1 = false;
            } else {
                System.out.println("Invalid exit dtr value: " + args[i]);
                return -1;
            }
            i++;
            if (args[i].equals("1")) {
                exitDTRstate2 = true;
            } else if (args[i].equals("0")) {
                exitDTRstate2 = false;
            } else {
                System.out.println("Invalid exit dtr value: " + args[i]);
                return -1;
            }
        } else if (args[i].equals("-rts")) {
            i++;
            if (args[i].equals("1")) {
                exitRTSstate1 = true;
            } else if (args[i].equals("0")) {
                exitRTSstate1 = false;
            } else {
                System.out.println("Invalid exit rts value: " + args[i]);
                return -1;
            }
            i++;
            if (args[i].equals("1")) {
                exitRTSstate2 = true;
            } else if (args[i].equals("0")) {
                exitRTSstate2 = false;
            } else {
                System.out.println("Invalid exit rts value: " + args[i]);
                return -1;
            }
        } else {
            System.out.println("-ex given but dtr/rts not specified: " + args[i]);
            return -1;
        }

        return 0;
    }

    /*
     * Extract DTR and RTS signal values for entry bootloader mode sequence.
     */
    int extractEntrySequence(final String[] args, int firstCall) {

        if (args[i].equals("-dtr")) {
            if (firstCall == 1) {
                firstSignalToSet = 1;
            }
            i++;
            if (args[i].equals("1")) {
                entryDTRstate1 = true;
            } else if (args[i].equals("0")) {
                entryDTRstate1 = false;
            } else {
                System.out.println("Invalid entry dtr value: " + args[i]);
                return -1;
            }
            i++;
            if (args[i].equals("1")) {
                entryDTRstate2 = true;
            } else if (args[i].equals("0")) {
                entryDTRstate2 = false;
            } else {
                System.out.println("Invalid entry dtr value: " + args[i]);
                return -1;
            }
        } else if (args[i].equals("-rts")) {
            if (firstCall == 1) {
                firstSignalToSet = 2;
            }
            i++;
            if (args[i].equals("1")) {
                entryRTSstate1 = true;
            } else if (args[i].equals("0")) {
                entryRTSstate1 = false;
            } else {
                System.out.println("Invalid entry rts value: " + args[i]);
                return -1;
            }
            i++;
            if (args[i].equals("1")) {
                entryRTSstate2 = true;
            } else if (args[i].equals("0")) {
                entryRTSstate2 = false;
            } else {
                System.out.println("Invalid entry rts value: " + args[i]);
                return -1;
            }
        } else {
            System.out.println("-er given but dtr/rts not specified: " + args[i]);
            return -1;
        }

        return 0;
    }

    /*
     * Prints usage of command line options on stdout
     */
    private void showHelp() {
        try {
            String LINE_SEPARATOR = System.getProperty("line.separator");
            String FILE_SEPARATOR = System.getProperty("file.separator");
            InputStream in = CmdLineHandler.class.getResourceAsStream(FILE_SEPARATOR + "help.txt");
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while ((line = r.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println("Can't read help text");
        }
    }

    @Override
    public void onDataReadProgressUpdate(int totalBytesReadTillNow, int numBytesToRead) {
        System.out.println("Total bytes read : " + totalBytesReadTillNow);
    }

    @Override
    public void onDataWriteProgressUpdate(int totalBytesWrittenTillNow, int numBytesToWrite) {
        System.out.println("Total bytes written : " + totalBytesWrittenTillNow);
    }
}
