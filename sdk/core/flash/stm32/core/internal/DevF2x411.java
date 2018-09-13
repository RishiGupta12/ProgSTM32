/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF2x411 extends Device {

    public DevF2x411() {
        pid = 0x411;
        mcuSeries = "STM32F2xxxx";
        RAMMemStartAddr = 0x20002000;
        RAMMemEndAddr = 0x2001FFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF77FF;
    }
}
