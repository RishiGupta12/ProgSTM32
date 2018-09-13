/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF3x432 extends Device {

    public DevF3x432(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x432;
        mcuSeries = "STM32F373xx - STM32F378xx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFFD800;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
