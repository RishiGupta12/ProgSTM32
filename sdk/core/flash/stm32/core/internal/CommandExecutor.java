/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;

import flash.stm32.core.internal.DeviceCreator;

/**
 * <p>
 * Base class representing a entity which carry out execution of user given
 * commands on the given concrete instance of a device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class CommandExecutor {

    public final DeviceCreator dCreator;

    public CommandExecutor() {
        dCreator = new DeviceCreator();
    }

    public abstract int getAllowedCommands() throws SerialComException, TimeoutException;

    public abstract String getBootloaderVersion() throws SerialComException, TimeoutException;

    public abstract int getChipID() throws SerialComException, TimeoutException;

    public abstract int getReadProtectionStatus() throws SerialComException, TimeoutException;

    public abstract int readMemory(byte[] data, int startAddr, int numBytesToRead)
            throws SerialComException, TimeoutException;

    public abstract int goJump(int addrToJumpTo) throws SerialComException, TimeoutException;

    public abstract int writeMemory(final byte[] data, int startAddr) throws SerialComException, TimeoutException;

    public abstract int eraseMemoryRegion(final int memReg, final int startPageNum, final int numOfPages)
            throws SerialComException, TimeoutException;
}
