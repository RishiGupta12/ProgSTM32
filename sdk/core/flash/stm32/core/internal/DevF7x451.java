/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF7x451 extends Device {

    public DevF7x451() {
        pid = 0x451;
        mcuSeries = "STM32F76xxx/77xxx";
        RAMMemStartAddr = 0x20004000;
        RAMMemEndAddr = 0x2007FFFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF0EDBF;
    }
}
