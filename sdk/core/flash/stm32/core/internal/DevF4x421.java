/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF4x421 extends Device {

    public DevF4x421() {
        pid = 0x421;
        mcuSeries = "STM32F446xx";
        RAMMemStartAddr = 0x20003000;
        RAMMemEndAddr = 0x2001FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
