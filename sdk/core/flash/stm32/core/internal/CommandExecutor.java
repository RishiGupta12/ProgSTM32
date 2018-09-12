/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.internal.DeviceCreator;

/**
 * <p>
 * Base class representing a entity which carry out execution of user given
 * commands on the given concrete instance of a device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public class CommandExecutor {

    public final DeviceCreator dCreator;

    public CommandExecutor() {

        dCreator = new DeviceCreator();
    }
}
