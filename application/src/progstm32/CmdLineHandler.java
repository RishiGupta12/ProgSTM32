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

import java.util.Locale;
import java.util.concurrent.TimeoutException;

import flash.stm32.core.Device;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.DeviceManager.IFace;
import flash.stm32.core.FileType;

import flash.stm32.uart.UARTInterface;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

/**
 * <p>
 * If the application is executing in command line mode, it extracts arguments
 * and execute the user given command.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class CmdLineHandler {

    final int ACT_WRITE = 0x01;
    final int ACT_ERASE = 0x02;
    final int ACT_MASS_ERASE = 0x04;
    final int ACT_READ = 0x08;
    final int ACT_SHOW_HELP = 0x10;
    final int ACT_GO = 0x20;
    final int ACT_READ_PROTECT = 0x40;
    final int ACT_READ_UNPROTECT = 0x80;
    final int ACT_WRITE_PROTECT = 0x100;
    final int ACT_WRITE_UNPROTECT = 0x200;
    final int ACT_GET_PID = 0x400;

    private DeviceManager devMgr;
    private UARTInterface uci;
    private Device dev;
    private boolean opened = false;

    public void process(String[] args) {

        int numArgs = args.length;
        int action = 0;
        int startPageNum = 0;
        int totalPageNum = 0;
        int baudrate = 0;
        int fileType = -1;
        int startAddress = -1;
        int length = -1;
        String device = null;
        boolean verify_write = false;

        for (int i = 0; i < numArgs; i++) {

            switch (args[i]) {

            case "-w":
                action = ACT_WRITE;
                break;

            case "-ih":
                fileType = FileType.HEX;
                break;

            case "-b":
                fileType = FileType.BIN;
                break;

            case "-s":
                i++;
                try {
                    startAddress = Integer.parseInt(args[i]);
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
                    action = ACT_MASS_ERASE;
                } else {
                    action = ACT_ERASE;
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
                break;

            case "-br":
                i++;
                try {
                    baudrate = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    System.out.println("Invalid erase option, " + e.getMessage());
                    return;
                }
                break;

            case "-d":
                i++;
                device = args[i];
                break;

            case "-g":
                action = ACT_GO;
                break;

            case "-v":
                verify_write = true;
                break;

            case "-j":
                action = ACT_READ_PROTECT;
                break;

            case "-k":
                action = ACT_READ_UNPROTECT;
                break;

            case "-n":
                action = ACT_WRITE_PROTECT;
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
                action = ACT_WRITE_UNPROTECT;
                break;

            case "-p":
                action = ACT_GET_PID;
                break;

            case "-h":
                break;

            default:
                System.out.println("Invalid option");
                return;
            }
        }

        /* Mandatory option check */
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
            uci.open(device, BAUDRATE.B115200, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_EVEN, FLOWCONTROL.NONE);
            opened = true;
            dev = uci.initAndIdentifyDevice();
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
            return;
        }

        switch (action) {

        case ACT_WRITE:
            break;

        case ACT_GET_PID:
            try {
                dev.getChipID();
            } catch (Exception e) {
                System.out.println("Get PID failed: " + e.getMessage());
            }
            return;

        default:
            System.out.println("Invalid action");
            return;
        }

        System.out.println("Option1 : " + action + " " + startPageNum + " " + totalPageNum);
        System.out.println("Option2 : " + fileType + " " + device + " " + baudrate);
    }
}
