/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF1x420 extends Device {

    public DevF1x420(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x420;
        mcuSeries = "STM32F10xxx";
        RAMMemStartAddr = 0x20000200;
        RAMMemEndAddr = 0x20001FFF;
        IBSysMemStartAddr = 0x1FFFF000;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
