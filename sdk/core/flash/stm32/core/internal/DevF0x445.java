/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF0x445 extends Device {

    public DevF0x445(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x445;
        mcuSeries = "STM32F04xxx - STM32F070x6";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFFC400;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
