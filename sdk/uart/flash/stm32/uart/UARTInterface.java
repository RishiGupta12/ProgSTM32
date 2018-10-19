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

import flash.stm32.uart.internal.UARTCommandExecutor;
import flash.stm32.core.CommunicationInterface;
import flash.stm32.core.Device;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import flash.stm32.core.internal.FlashUtils;

/**
 * <p>
 * Represents a serial port (UART interface) through which host computer will
 * communicate with bootloader in a stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTInterface extends CommunicationInterface {

    private final ResourceBundle rb;
    private final SerialComManager scm;
    private final UARTCommandExecutor uartce;

    private long comPortHandle;

    /**
     * <p>
     * Allocates and prepares classes responsible for communication using serial
     * port and stm32 bootloader protocol implementation.
     * </p>
     * 
     * @param libName
     *            unique name for the shared library (consider application name with
     *            some unique characters)
     * @param rb
     *            resource bundle currently active
     * @param flashUtils
     *            an instance of utility class to carry out common operations
     * @throws IOException
     *             if instance of SerialComManager can't be created
     */
    public UARTInterface(String libName, ResourceBundle rb, FlashUtils flashUtils) throws IOException {

        super();

        this.rb = rb;

        String tmpDir = sysprop.getJavaIOTmpDir();

        scm = new SerialComManager(libName, tmpDir, true, false);

        uartce = new UARTCommandExecutor(scm, rb, flashUtils);

        comPortHandle = -1;
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
    public void open(String port, SerialComManager.BAUDRATE baudRate, SerialComManager.DATABITS dataBits,
            SerialComManager.STOPBITS stopBits, SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException {

        comPortHandle = scm.openComPort(port, true, true, true);

        scm.configureComPortData(comPortHandle, dataBits, stopBits, parity, baudRate, 0);

        scm.configureComPortControl(comPortHandle, flowCtrl, 'x', 'x', true, false);

        /*
         * 500 milliseconds timeout on serial port read. Some methods have timeouts
         * which are multiple of this 500. So if this is changed update those methods
         * also.
         */
        scm.fineTuneReadBehaviour(comPortHandle, 0, 5, 100, 5, 200);

        scm.clearPortIOBuffers(comPortHandle, true, true);
    }

    /**
     * <p>
     * Closes opened serial port and release resources if any.
     * </p>
     * 
     * @throws SerialComException
     *             if port can not be closed for some reason
     */
    public void close() throws SerialComException {

        scm.closeComPort(comPortHandle);
    }

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
     */
    public Device initAndIdentifyDevice() throws SerialComException, TimeoutException {

        if (comPortHandle != -1) {
            return uartce.initAndIdentifyDevice(comPortHandle);
        }

        throw new IllegalStateException(rb.getString("uart.notopen"));
    }

    public void disconnectFromDevice() {
        // TODO reset needed or not
    }
}
