/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x441 extends Device {

    public DevF4x441() {
        pid = 0x441;
        mcuSeries = "STM32F412xx";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x2003FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
