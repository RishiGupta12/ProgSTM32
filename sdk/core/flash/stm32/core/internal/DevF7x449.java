/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF7x449 extends Device {

    public DevF7x449(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x449;
        mcuSeries = "STM32F74xxx/75xxx";
        RAMMemStartAddr = 0x20004000;
        RAMMemEndAddr = 0x2004FFFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF0EDBF;
    }
}
