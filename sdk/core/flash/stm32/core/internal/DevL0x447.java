/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL0x447 extends Device {

    public DevL0x447(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x447;
        mcuSeries = "STM32L07xxx/08xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
