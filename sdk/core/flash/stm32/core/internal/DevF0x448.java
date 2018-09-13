/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF0x448 extends Device {

    public DevF0x448() {
        pid = 0x448;
        mcuSeries = "STM32F070xB - STM32F071xx/072xx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFFC800;
        IBSysMemEndAddr = 0x1FFFC800;
    }
}
