/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF3x422 extends Device {

    public DevF3x422(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x422;
        mcuSeries = "STM32F302xB(C)/303xB(C) - STM32F358xx";
        RAMMemStartAddr = 0x20001400;
        RAMMemEndAddr = 0x20009FFF;
        IBSysMemStartAddr = 0x1FFFD800;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
