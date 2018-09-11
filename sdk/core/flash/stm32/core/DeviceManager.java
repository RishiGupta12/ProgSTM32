/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import java.io.IOException;

import flash.stm32.uart.UARTInterface;

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
    public enum IFace {
        /**
         * <p>
         * Represents USART interface (serial port).
         * </p>
         */
        UART(1);
        private int value;

        private IFace(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public CommunicationInterface getCommunicationIface(IFace iface, String libName) throws IOException {

        int x;

        if (iface == null) {
            throw new IllegalArgumentException("iface can not be null.");
        }

        x = iface.getValue();

        if (x == 1) {
            return new UARTInterface(libName);
        }

        return null;
    }
}
