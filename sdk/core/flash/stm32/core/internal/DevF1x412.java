/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF1x412 extends Device {

    public DevF1x412(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x412;
        mcuSeries = "STM32F10xxx";
        RAMMemStartAddr = 0x20000200;
        RAMMemEndAddr = 0x200027FF;
        IBSysMemStartAddr = 0x1FFFF000;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
