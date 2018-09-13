/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL1x427 extends Device {

    public DevL1x427(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x427;
        mcuSeries = "STM32L1xxxC";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x20007FFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
