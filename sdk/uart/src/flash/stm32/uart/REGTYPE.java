/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart;

/**
 * <p>
 * Contains bit masks to categories flash memory area.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class REGTYPE {

    /**
     * <p>
     * Represents main memory region in flash address space of the given microcontroller.
     * </p>
     */
    public static final int MAIN = 0x01;

    /**
     * <p>
     * Represents system memory region in information block in the flash address space of the 
     * given microcontroller.
     * </p>
     */
    public static final int SYSTEM = 0x02;

    /**
     * <p>
     * Represents option byte memory region in information block in the flash address space of 
     * the given microcontroller.
     * </p>
     */
    public static final int OPTIONBYTE = 0x04;
    
    /**
     * <p>
     * Represents page 1 memory region in the flash address space of the given microcontroller.
     * </p>
     */
    public static final int PAGE1 = 0x08;
}
