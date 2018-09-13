/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL0x417 extends Device {

    public DevL0x417(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x417;
        mcuSeries = "STM32L05xxx/06xxx";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x20001FFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF00FFF;
    }
}
