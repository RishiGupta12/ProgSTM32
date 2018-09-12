/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

/**
 * <p>
 * Contains bit masks to categories flash memory area into main flash memory,
 * system memory, option bytes and banks as applicable to given microcontroller.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class REGTYPE {

    /**
     * <p>
     * Represents main memory region in flash address space of the given
     * microcontroller.
     * </p>
     */
    public static final int MAIN = 0x01;

    /**
     * <p>
     * Represents system memory region in information block in the flash address
     * space of the given microcontroller.
     * </p>
     */
    public static final int SYSTEM = 0x02;

    /**
     * <p>
     * Represents option byte memory region in information block in the flash
     * address space of the given microcontroller.
     * </p>
     */
    public static final int OPTIONBYTE = 0x04;

    /**
     * <p>
     * Represents bank 1 memory region in the flash address space of the given
     * microcontroller.
     * </p>
     */
    public static final int BANK1 = 0x08;

    /**
     * <p>
     * Represents bank 2 memory region in the flash address space of the given
     * microcontroller.
     * </p>
     */
    public static final int BANK2 = 0x10;

    /**
     * <p>
     * Represents an EEPROM memory region.
     * </p>
     */
    public static final int EEPROM = 0x20;

    /**
     * <p>
     * Represents an RAM (random access memory) region.
     * </p>
     */
    public static final int RAM = 0x40;
}
