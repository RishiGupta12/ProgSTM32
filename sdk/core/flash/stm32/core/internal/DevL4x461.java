/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL4x461 extends Device {

    public DevL4x461(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x461;
        mcuSeries = "STM32L496xx/4A6xx";
        RAMMemStartAddr = 0x20003100;
        RAMMemEndAddr = 0x2003FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF6FFF;
    }
}
