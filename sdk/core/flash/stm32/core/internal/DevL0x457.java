/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL0x457 extends Device {

    public DevL0x457(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x457;
        mcuSeries = "STM32L01xxx/02xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF00FFF;
    }
}
