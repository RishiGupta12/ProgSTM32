/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager;

import flash.stm32.core.internal.SystemProperties;

/**
 * <p>
 * Base class representing a communication interface.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class CommunicationInterface {

    public final SystemProperties sysprop;

    public CommunicationInterface() {

        sysprop = new SystemProperties();
    }

    public abstract void open(String port, SerialComManager.BAUDRATE baudRate, SerialComManager.DATABITS dataBits,
            SerialComManager.STOPBITS stopBits, SerialComManager.PARITY parity, SerialComManager.FLOWCONTROL flowCtrl)
            throws SerialComException;

    public abstract void close() throws SerialComException;

    public abstract Device connectAndIdentifyDevice() throws SerialComException, TimeoutException;

    public abstract void disconnectFromDevice();
}
