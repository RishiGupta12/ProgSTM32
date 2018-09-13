/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF3x438 extends Device {

    public DevF3x438() {
        pid = 0x438;
        mcuSeries = "STM32F303x4(6/8) - 334xx/328xx";
        RAMMemStartAddr = 0x20001800;
        RAMMemEndAddr = 0x20002FFF;
        IBSysMemStartAddr = 0x1FFFD800;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
