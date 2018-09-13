/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x419 extends Device {

    public DevF4x419(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x419;
        mcuSeries = "STM32F42xxx/43xxx";
        RAMMemStartAddr = 0x20003000;
        RAMMemEndAddr = 0x2002FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
