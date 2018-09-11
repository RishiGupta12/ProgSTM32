/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.uart.internal;

import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;
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

    private final byte INITSEQ = 0x7F;
    private final byte ACK = 0x79;
    private final byte NACK = 0x1F;
    private final byte[] CMD_GET_ALLOWED_CMDS = new byte[] { (byte)0x00, (byte)0xFF };
    private final byte[] CMD_GET_VRPS = new byte[] { (byte)0x01, (byte)0xFE };
    
    private final SerialComManager scm;
    private long comPortHandle;

    public UARTCommandExecutor(SerialComManager scm) {
        this.scm = scm;
    }

    public void connectAndIdentifyDevice(long comPortHandle) throws SerialComException, TimeoutException {

        int x;
        int y;
        int z;
        this.comPortHandle = comPortHandle;

        for (x = 0; x < 3; x++) {
            scm.writeSingleByte(comPortHandle, INITSEQ);
            byte[] rcvData = scm.readBytes(comPortHandle);
            if (rcvData != null) {
                y = rcvData.length;
                for (z = 0; z < y; z++) {
                    if (rcvData[z] == ACK) {
                        break;
                    }
                }
            }
        }

        if (x >= 3) {
            throw new TimeoutException("init sequence timedout");
        }

    }

}
