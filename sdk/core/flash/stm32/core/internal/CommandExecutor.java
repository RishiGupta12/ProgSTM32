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
}
