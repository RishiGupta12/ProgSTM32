/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart.internal;

import com.serialpundit.serial.SerialComManager;

import flash.stm32.core.internal.CommandExecutor;

/**
 * <p>
 * Entity which implements USART protocol required for communication between
 * host computer and bootloader in a stm32 microcontroller.
 * </p>
 * 
 * @author Rishi Gupta
 */
public class UARTCommandExecutor extends CommandExecutor {

    private final SerialComManager scm;
    private long comPortHandle;

    public UARTCommandExecutor(SerialComManager scm) {
        this.scm = scm;
    }

    public void connectAndIdentifyDevice(long comPortHandle) {

        this.comPortHandle = comPortHandle;
    }

}
