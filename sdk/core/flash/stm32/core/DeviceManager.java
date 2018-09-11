/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

/**
 * <p>
 * Entry point to the flashstm32 sdk. Application must obtain an instance of
 * this class and call methods in it.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class DeviceManager {

    /**
     * <p>
     * Pre-defined communication interface for communication between bootloader and
     * host computer.
     * </p>
     */
    public enum UART {
        /**
         * <p>
         * Represents USART interface (serial port).
         * </p>
         */
        SB_1(1);
        private int value;

        private UART(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

}
