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
    private final byte[] CMD_GET_ID = new byte[] { (byte)0x02, (byte)0xFD };
    private final byte[] CMD_READ_MEMORY = new byte[] { (byte)0x11, (byte)0xEE };
    private final byte[] CMD_GO = new byte[] { (byte)0x21, (byte)0xDE };
    private final byte[] CMD_WRITE_MEMORY = new byte[] { (byte)0x31, (byte)0xCE };
    private final byte[] CMD_ERASE = new byte[] { (byte)0x43, (byte)0xBC };
    private final byte[] CMD_EXTD_ERASE = new byte[] { (byte)0x44, (byte)0xBB };
    private final byte[] CMD_WRITE_PROTECT = new byte[] { (byte)0x63, (byte)0x9C };
    private final byte[] CMD_WRITE_UNPROTECT = new byte[] { (byte)0x73, (byte)0x8C };
    private final byte[] CMD_READOUT_PROTECT = new byte[] { (byte)0x82, (byte)0x7D };
    private final byte[] CMD_READOUT_UNPROTECT = new byte[] { (byte)0x92, (byte)0x6D };
    
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
    
    private int sendCmdOrCmdData(byte[] sndbuf, int timeOutDuration) 
            throws SerialComException, TimeoutException {
        
        int x;
        byte[] buf = new byte[2];
        long curTime;
        long responseWaitTime;
        
        scm.writeBytes(comPortHandle, sndbuf);
        
        responseWaitTime = System.currentTimeMillis() + (1000 * timeOutDuration);
        
        while(true) {
            
            x = scm.readBytes(comPortHandle, buf, 0, 1, -1, null); //TODO parity error handling
            
            if (x > 0) {
                if (buf[0] == ACK) {
                    return 0;
                }
                else if (buf[0] == NACK) {
                    throw new SerialComException("nack");
                }
                else {
                }
            }
            
            curTime = System.currentTimeMillis();
            if (curTime >= responseWaitTime) {
                throw new TimeoutException("init sequence timedout");
            }
            
            if ((responseWaitTime - curTime) > 1000) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
