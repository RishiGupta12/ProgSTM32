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
                return new DevF3x438();
            case 0x439:
                return new DevF3x439();
            case 0x446:
                return new DevF3x446();
            }
        } else if ((pid >= 0x413) && (pid <= 0x458)) {
            // F4 series
            switch (pid) {
            case 0x413:
                return new DevF4x413();
            case 0x419:
                return new DevF4x419();
            case 0x421:
                return new DevF4x421();
            case 0x423:
                return new DevF4x423();
            case 0x431:
                return new DevF4x431();
            case 0x433:
                return new DevF4x433();
            case 0x441:
                return new DevF4x441();
            case 0x458:
                return new DevF4x458();
            }
        } else {

        }

        return new UnknownDevice(pid);
    }
}
