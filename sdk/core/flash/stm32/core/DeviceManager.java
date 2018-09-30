/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import flash.stm32.uart.UARTInterface;

/**
 * <p>
 * Entry point to the progstm32 sdk. Application must obtain an instance of
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

    private final ResourceBundle rb;

    /**
     * 
     * @param locale
     */
    public DeviceManager(Locale locale) {
        rb = ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", locale);
    }

    /**
     * 
     * 
     * @param iface
     * @param libName
     * @return
     * @throws IOException
     */
    public CommunicationInterface getCommunicationIface(IFace iface, String libName) throws IOException {

        int x;

        if (iface == null) {
            throw new IllegalArgumentException(rb.getString("iface.null.not.allowed"));
        }
        if (libName == null) {
            throw new IllegalArgumentException(rb.getString("invalid.libname"));
        }

        x = iface.getValue();

        if (x == 1) {
            return new UARTInterface(libName, rb);
        }

        return null;
    }
}
