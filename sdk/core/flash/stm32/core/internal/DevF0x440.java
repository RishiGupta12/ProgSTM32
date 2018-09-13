/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF0x440 extends Device {

    public DevF0x440() {
        pid = 0x440;
        mcuSeries = "STM32F05xxx - STM32F030x8";
        RAMMemStartAddr = 0x20000800;
        RAMMemEndAddr = 0x20001FFF;
        IBSysMemStartAddr = 0x1FFFEC00;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
