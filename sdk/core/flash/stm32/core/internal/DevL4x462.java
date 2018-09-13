/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL4x462 extends Device {

    public DevL4x462() {
        pid = 0x462;
        mcuSeries = "STM32L45xxx/46xxx";
        RAMMemStartAddr = 0x20003100;
        RAMMemEndAddr = 0x2001FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF6FFF;
    }
}
