/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

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
     * This command corresponds to Get Version & Read Protection Status command
     * (0x01) of default STM32 Bootloader. This command gets the bootloader version
     * and the read protection status of the flash memory
     * </p>
     */
    public static final int GET_VRPS = 0x02;
    
    /**
     * <p>
     * This command corresponds to Get ID (0x02) of default STM32 Bootloader. This 
     * command gets the chip ID.
     * </p>
     */
    public static final int GET_ID = 0x04;
}
