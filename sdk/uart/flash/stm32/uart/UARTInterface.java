/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
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

/**
 * <p>
 * Represents a serial port (UART interface) through which host computer will
 * communicate with bootloader in a stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class UARTInterface extends CommunicationInterface {

    /**
     * <p>
     * Production release version of this UART STM32 flasher sdk.
     * </p>
     */
    public static final String STM32UARTSDK_VERSION = "1.0";

    private final ResourceBundle rb;
    private final SerialComManager scm;
    private final UARTCommandExecutor uartce;

    private long comPortHandle;

    /**
     * 
     * @param libName
     * @throws IOException
     */
    public UARTInterface(String libName, ResourceBundle rb) throws IOException {

        super();

        this.rb = rb;

        String tmpDir = sysprop.getJavaIOTmpDir();

        scm = new SerialComManager(libName, tmpDir, true, false);

        uartce = new UARTCommandExecutor(scm, rb);

        comPortHandle = -1;
    }

    /**
     * 
     * Although most of the devices uses even parity, few devices may use no parity,
     * therefore proper documents should be referred for such devices.
     * 
     * @param port
     * @param baudRate
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param flowCtrl
     * @throws SerialComException
     */
    public void open(String port, SerialComManager.BAUDRATE baudRate, SerialComManager.DATABITS dataBits,
            SerialComManager.STOPBITS stopBits, SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException {

        comPortHandle = scm.openComPort(port, true, true, true);

        scm.configureComPortData(comPortHandle, dataBits, stopBits, parity, baudRate, 0);

        scm.configureComPortControl(comPortHandle, flowCtrl, 'x', 'x', false, false);

        /*
         * 500 milliseconds timeout on serial port read. Some methods have timeouts
         * which are multiple of this 500. So if this is changed update those methods
         * also.
         */
        scm.fineTuneReadBehaviour(comPortHandle, 0, 5, 100, 5, 200);

        scm.clearPortIOBuffers(comPortHandle, true, true);
    }

    /**
     * 
     * @throws SerialComException
     */
    public void close() throws SerialComException {

        scm.closeComPort(comPortHandle);
    }

    public Device connectAndIdentifyDevice() throws SerialComException, TimeoutException {

        if (comPortHandle != -1) {
            return uartce.connectAndIdentifyDevice(comPortHandle);
        }

        throw new IllegalStateException(rb.getString("port.not.opened"));
    }

    public void disconnectFromDevice() {
        // todo reset needed or not
    }
}
