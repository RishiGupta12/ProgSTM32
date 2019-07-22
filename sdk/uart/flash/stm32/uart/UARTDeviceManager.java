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

package flash.stm32.uart;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import flash.stm32.uart.UARTInterface;
import flash.stm32.core.CommunicationInterface;
import flash.stm32.core.FlashUtils;

/**
 * <p>
 * Entry point to the progstm32 sdk. Application must obtain an instance of this
 * class and call methods in it.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTDeviceManager {

    /**
     * <p>
     * Production release version of prog32 SDK.
     * </p>
     */
    public static final String SDK_VERSION = "1.0";

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
    private final FlashUtils flashUtils;

    /**
     * <p>
     * Allocates instance of DeviceManager with English as language.
     * </p>
     */
    public UARTDeviceManager() {
        rb = ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", Locale.ENGLISH);
        flashUtils = new FlashUtils(rb);
    }

    /**
     * <p>
     * Allocates instance of DeviceManager with the given language.
     * </p>
     * 
     * @param locale
     *            locale specifying language to use
     */
    public UARTDeviceManager(Locale locale) {

        String bundle = null;
        if (locale == null) {
            throw new IllegalArgumentException("Invalid locale");
        }

        String lang = locale.getLanguage();
        if (lang.equals("en")) {
            bundle = "flash.stm32.resources.MessagesBundle";
        } else if (lang.equals("fr")) {
            bundle = "flash.stm32.resources.MessagesBundle_fr_FR";
        } else if (lang.equals("ko")) {
            bundle = "flash.stm32.resources.MessagesBundle_ko_KR";
        } else if (lang.equals("it")) {
            bundle = "flash.stm32.resources.MessagesBundle_it_IT";
        } else if (lang.equals("de")) {
            bundle = "flash.stm32.resources.MessagesBundle_de_DE";
        } else if (lang.equals("zh")) {
            bundle = "flash.stm32.resources.MessagesBundle_zh_CN";
        } else if (lang.equals("ja")) {
            bundle = "flash.stm32.resources.MessagesBundle_ja_JP";
        } else {
            bundle = "flash.stm32.resources.MessagesBundle";
        }

        rb = ResourceBundle.getBundle(bundle, locale);
        flashUtils = new FlashUtils(rb);
    }

    /**
     * <p>
     * Gives an instance of UARTInterface which represents serial port of host.
     * </p>
     * 
     * @param iface
     *            set it to IFace.UART
     * @param libName
     *            unique name for shared library
     * @return an instance of UARTInterface
     * @throws IOException
     *             if SerialPundit library can not be initialized
     */
    public CommunicationInterface getCommunicationIface(IFace iface, String libName) throws IOException {

        if (iface == null) {
            throw new IllegalArgumentException(rb.getString("inval.iface"));
        }
        if (libName == null) {
            throw new IllegalArgumentException(rb.getString("inval.libname"));
        }

        int x = iface.getValue();
        if (x == 1) {
            return new UARTInterface(libName, rb, flashUtils);
        }

        return null;
    }

    /**
     * <p>
     * Try to parse and check if the given file is in intel hex format or not.
     * </p>
     * 
     * @param file
     *            file which needs to be verified
     * @return true if file is in intel hex format otherwise false
     * @throws IOException
     *             if given file not found or unable to read it
     */
    public boolean isFileInHexFormat(File file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException(rb.getString("inval.file"));
        }
        return flashUtils.isHexFormatFwFile(file);
    }

    /**
     * <p>
     * Gives version of the prog32 SDK.
     * </p>
     * 
     * @return version of the prog32 SDK
     */
    public String getSDKVersion() {
        return SDK_VERSION;
    }
}
