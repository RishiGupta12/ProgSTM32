/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL1x436 extends Device {

    public DevL1x436() {
        pid = 0x436;
        mcuSeries = "STM32L1xxxD";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x2000BFFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
