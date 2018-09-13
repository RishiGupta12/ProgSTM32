/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

/**
 * <p>
 * Handles concrete stm32 device specific information management.
 * </p>
 * 
 * @author Rishi Gupta
 */
public class DeviceCreator {

    public Device createDevFromPID(int pid) {

        if ((pid >= 0x422) && (pid <= 0x446)) {
            // F3 series
            switch (pid) {
            case 0x422:
                return new DevF3x422();
            case 0x432:
                return new DevF3x432();
            case 0x438:
                break;
            case 0x439:
                break;
            case 0x446:
                break;
            }
        }

        return null;
    }
}
