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

package flash.stm32.core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;

import flash.stm32.core.internal.CommandExecutor;

/**
 * <p>
 * Base class representing a stm32 device.
 * </p>
 * 
 * <p>
 * RAM address 0x00 means two or more devices have same pid but different
 * starting RAM addresses. Therefore more concrete information has to be
 * provided by caller.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class Device {

    /*
     * Some of the memory region is used by bootloader or inaccessible during
     * bootloader execution, we have taken address ranges from AN2606 to work with
     * valid memory ranges.
     */

    protected CommandExecutor cmdExtr;

    public int pid;
    public String mcuSeries;

    public int IBSysMemStartAddr;
    public int IBSysMemEndAddr;

    public int RAMMemStartAddr;
    public int RAMMemEndAddr;

    public int resetCodeAddress;

    /**
     * <p>
     * Gets the product series to which given stm32 belongs.
     * </p>
     * 
     * @return product series to which given stm32 belongs
     */
    public String getuCSeries() {
        return mcuSeries;
    }

    /**
     * <p>
     * Provides memory related information of the given stm32 device in the
     * Following order: stm32 pid, start address of system memory area, end address
     * of system memory area, start address of RAM area, end address of RAM area.
     * </p>
     * 
     * @return information about the stm32 device
     */
    public int[] getMCUInformation() {

        int[] info = new int[5];

        info[0] = pid;
        info[1] = IBSysMemStartAddr;
        info[2] = IBSysMemEndAddr;
        info[3] = RAMMemStartAddr;
        info[4] = RAMMemEndAddr;

        return info;
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
        return cmdExtr.getAllowedCommands();
    }

    /**
     * <p>
     * Sends command 'Get' (0x00) to know commands supported by this bootloader and
     * then extracts bootloader version for the received response. This represents
     * version of the serial peripheral (USART, CAN, USB, etc.) communication
     * protocol used in the bootloader.
     * </p>
     * 
     * @return bootloader version in human readable format
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public String getBootloaderProtocolVersion() throws SerialComException, TimeoutException {
        return cmdExtr.getBootloaderProtocolVersion();
    }

    /**
     * <p>
     * Read product specific system memory and extracts bootloader ID programmed
     * into stm32.
     * </p>
     * 
     * @return bootloader ID of the stm32
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int getChipID() throws SerialComException, TimeoutException {
        return cmdExtr.getChipID();
    }

    /**
     * <p>
     * Reads bootloader ID programmed into the last two byte of the device's system
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
        return cmdExtr.getBootloaderID();
    }

    /**
     * <p>
     * Sends command 'Get Version and Read Protection Status' (0x01) to stm32 and
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
        return cmdExtr.getReadProtectionStatus();
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
     * @return number of bytes read from stm32
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int readMemory(byte[] data, int startAddr, int numBytesToRead, ICmdProgressListener progressListener)
            throws SerialComException, TimeoutException {
        return cmdExtr.readMemory(data, startAddr, numBytesToRead, progressListener);
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
        return cmdExtr.readMemory(file, startAddr, numBytesToRead, progressListener);
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
        cmdExtr.goJump(addrToJumpTo);
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
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public int writeMemory(final int fwType, final File fwFile, final int startAddr,
            ICmdProgressListener progressListener) throws IOException, TimeoutException {
        return cmdExtr.writeMemory(fwType, fwFile, startAddr, progressListener);
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
    public int writeMemory(final int fwType, final byte[] data, int startAddr, ICmdProgressListener progressListener)
            throws SerialComException, TimeoutException {
        return cmdExtr.writeMemory(fwType, data, startAddr, progressListener);
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
     *            total number of pages which should be erased starting from
     *            starting page number
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void eraseMemoryRegion(final int memReg, final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {
        cmdExtr.eraseMemoryRegion(memReg, startPageNum, totalNumOfPages);
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
     *            total number of pages which should be erased starting from
     *            starting page number
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void extendedEraseMemoryRegion(final int memReg, final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {
        cmdExtr.extendedEraseMemoryRegion(memReg, startPageNum, totalNumOfPages);
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
     *            total number of pages to be protected
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void writeProtectMemoryRegion(final int startPageNum, final int totalNumOfPages)
            throws SerialComException, TimeoutException {
        cmdExtr.writeProtectMemoryRegion(startPageNum, totalNumOfPages);
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
        cmdExtr.writeUnprotectMemoryRegion();
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
        cmdExtr.readoutprotectMemoryRegion();
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
        cmdExtr.readoutUnprotectMemoryRegion();
    }

    /**
     * <p>
     * Loads reset program in RAM and executes it. This method is used mainly to
     * reset stm32 programmatically.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader declines this command, fails to execute this
     *             command or sends no response at all
     */
    public void triggerSystemReset() throws SerialComException, TimeoutException {
        cmdExtr.triggerSystemReset(resetCodeAddress);
    }
}
