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

import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.core.internal.SystemProperties;

/**
 * <p>
 * Base class representing a communication interface for example; serial port.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class CommunicationInterface {

    protected final SystemProperties sysprop;

    /**
     * <p>
     * Allocate an instance of CommunicationInterface.
     * </p>
     */
    public CommunicationInterface() {

        sysprop = new SystemProperties();
    }

    /**
     * <p>
     * Opens and configures serial port as per the given parameters. Although most
     * of the devices uses even parity, few devices may use no parity, therefore
     * proper documents should be referred for such devices. This also sets the
     * minimum timeout to 500 milli seconds for reading data from serial port
     * </p>
     * 
     * @param port
     *            serial port (COMxx/ttyXX) through which stm32 is connected to host
     *            computer
     * @param baudRate
     *            rate of signal change used for communication, select as per the
     *            crystal connected to stm32 board for minimum baud rate error
     * @param dataBits
     *            number of data bits a serial frame will contain, set to
     *            DATABITS.DB_8
     * @param stopBits
     *            number of stop bits a serial frame will contain, set to
     *            STOPBITS.SB_1
     * @param parity
     *            parity type for error checking, set to PARITY.P_EVEN
     * @param flowCtrl
     *            set to FLOWCONTROL.NONE as default bootloader does not use flow
     *            control
     * @throws SerialComException
     *             if the port is not found, unable to open and configure it
     */
    public abstract void open(String port, SerialComManager.BAUDRATE baudRate, SerialComManager.DATABITS dataBits,
            SerialComManager.STOPBITS stopBits, SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException;

    /**
     * <p>
     * Closes opened serial port and release resources if any.
     * </p>
     * 
     * @throws SerialComException
     *             if port can not be closed for some reason
     */
    public abstract void close() throws SerialComException;

    /**
     * <p>
     * Establish communication with bootloader by sending init sequence (0x7F) and
     * identifies the product id of it. Based on the pid obtained creates and
     * instance of Device class which represents connected stm32.
     * </p>
     * 
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     * @throws TimeoutException
     *             when bootloader sends NACK or timeout happens
     * @return an instance of Device class representing stm32 device
     */
    public abstract Device initAndIdentifyDevice() throws SerialComException, TimeoutException;

    /**
     * <p>
     * Sets the DTR signal of the host side serial port to the given value.
     * </p>
     * 
     * @param value
     *            true or false corresponding to the voltage level desired at
     *            hardware level
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     */
    public abstract void setDTR(boolean value) throws SerialComException;

    /**
     * <p>
     * Sets the RTS signal of the host side serial port to the given value.
     * </p>
     * 
     * @param value
     *            true or false corresponding to the voltage level desired at
     *            hardware level
     * @throws SerialComException
     *             if an error happens when communicating through serial port
     */
    public abstract void setRTS(boolean value) throws SerialComException;
}
