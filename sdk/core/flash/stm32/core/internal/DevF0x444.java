/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF0x444 extends Device {

    public DevF0x444() {
        pid = 0x444;
        mcuSeries = "STM32F03xx4/6";
        RAMMemStartAddr = 0x20000800;
        RAMMemEndAddr = 0x20000FFF;
        IBSysMemStartAddr = 0x1FFFEC00;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}