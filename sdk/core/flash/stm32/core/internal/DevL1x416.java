/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL1x416 extends Device {

    public DevL1x416() {
        pid = 0x416;
        mcuSeries = "STM32L1xxx6(8/B)";
        RAMMemStartAddr = 0x20000800;
        RAMMemEndAddr = 0x20003FFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF01FFF;
    }
}
