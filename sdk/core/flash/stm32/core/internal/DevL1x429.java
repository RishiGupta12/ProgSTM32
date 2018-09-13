/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL1x429 extends Device {

    public DevL1x429() {
        pid = 0x429;
        mcuSeries = "STM32L1xxx6(8/B)A";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x20007FFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
