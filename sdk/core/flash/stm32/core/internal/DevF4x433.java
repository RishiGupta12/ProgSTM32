/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x433 extends Device {

    public DevF4x433(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x433;
        mcuSeries = "STM32F401xD(E)";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x20017FFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
