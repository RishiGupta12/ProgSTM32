/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF0x442 extends Device {

    public DevF0x442() {
        pid = 0x442;
        mcuSeries = "STM32F030xC - STM32F09xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFFD800;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
