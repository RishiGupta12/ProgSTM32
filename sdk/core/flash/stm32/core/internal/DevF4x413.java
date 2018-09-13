/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x413 extends Device {

    public DevF4x413() {
        pid = 0x413;
        mcuSeries = "STM32F40xxx/41xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
