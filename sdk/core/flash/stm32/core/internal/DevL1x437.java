/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL1x437 extends Device {

    public DevL1x437() {
        pid = 0x437;
        mcuSeries = "STM32L1xxxE";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x20013FFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
