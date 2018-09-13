/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x431 extends Device {

    public DevF4x431(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x431;
        mcuSeries = "STM32F446xx";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x2000FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
