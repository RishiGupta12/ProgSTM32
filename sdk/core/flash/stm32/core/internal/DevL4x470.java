/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL4x470 extends Device {

    public DevL4x470() {
        pid = 0x470;
        mcuSeries = "STM32L4Rxx/4Sxx";
        RAMMemStartAddr = 0x20003200;
        RAMMemEndAddr = 0x2009FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF6FFF;
    }
}
