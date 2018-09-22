/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF1x418 extends Device {

    public DevF1x418(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x418;
        mcuSeries = "STM32F105xx - 107xx";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x2000FFFF;
        IBSysMemStartAddr = 0x1FFFB000;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
