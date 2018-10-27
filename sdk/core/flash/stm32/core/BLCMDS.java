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

/**
 * <p>
 * Contains bit masks for all commands supported by STM32 default bootloader.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class BLCMDS {

    /**
     * <p>
     * This command corresponds to GET command (0x00) of default STM32 Bootloader.
     * This command gets the version and the allowed commands supported by the
     * current version of the bootloader.
     * </p>
     */
    public static final int GET = 0x01;

    /**
     * <p>
     * This command corresponds to 'get version and read protection status' command
     * (0x01) of default STM32 bootloader. This command gets the bootloader version
     * and the read protection status of the flash memory.
     * </p>
     */
    public static final int GET_VRPS = 0x02;

    /**
     * <p>
     * This command corresponds to 'get ID' (0x02) command of default STM32
     * bootloader. This command gets the chip ID.
     * </p>
     */
    public static final int GET_ID = 0x04;

    /**
     * <p>
     * This command corresponds to 'read memory' (0x11) command of default STM32
     * bootloader. This command reads up to 256 bytes of memory starting from an
     * address specified by the application.
     * </p>
     */
    public static final int READ_MEMORY = 0x08;

    /**
     * <p>
     * This command corresponds to 'go' (0x21) command of default STM32 bootloader.
     * This command make jump to user application code located in the internal flash
     * memory or in SRAM.
     * </p>
     */
    public static final int GO = 0x10;

    /**
     * <p>
     * This command corresponds to 'write memory' (0x31) command of default STM32
     * bootloader. This command make writes up to 256 bytes to the RAM or flash
     * memory starting from an address specified by the application.
     * </p>
     */
    public static final int WRITE_MEMORY = 0x20;

    /**
     * <p>
     * This command corresponds to 'erase' (0x43) command of default STM32
     * bootloader. This command erases from one to all the flash memory pages.
     * </p>
     */
    public static final int ERASE = 0x40;

    /**
     * <p>
     * This command corresponds to 'extended erase' (0x44) command of default STM32
     * bootloader. This command erases from one to all the flash memory pages using
     * two byte addressing mode (available only for v3.0 usart bootloader versions
     * and above).
     * </p>
     */
    public static final int EXTENDED_ERASE = 0x80;

    /**
     * <p>
     * This command corresponds to 'write protect' (0x63) command of default STM32
     * bootloader. This command enables the write protection for some sectors.
     * </p>
     */
    public static final int WRITE_PROTECT = 0x100;

    /**
     * <p>
     * This command corresponds to 'write unprotect' (0x73) command of default STM32
     * bootloader. This command disables the write protection for all Flash memory
     * sectors.
     * </p>
     */
    public static final int WRITE_UNPROTECT = 0x200;

    /**
     * <p>
     * This command corresponds to 'readout protect' (0x82) command of default STM32
     * bootloader. This command enables read protection.
     * </p>
     */
    public static final int READOUT_PROTECT = 0x400;

    /**
     * <p>
     * This command corresponds to 'readout protect' (0x92) command of default STM32
     * bootloader. This command disables read protection.
     * </p>
     */
    public static final int READOUT_UNPROTECT = 0x800;
}
