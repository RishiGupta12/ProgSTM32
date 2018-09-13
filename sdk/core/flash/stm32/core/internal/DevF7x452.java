/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF7x452 extends Device {

    public DevF7x452() {
        pid = 0x452;
        mcuSeries = "STM32F72xxx/73xxx";
        RAMMemStartAddr = 0x20004000;
        RAMMemEndAddr = 0x2003FFFF;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF0EDBF;
    }
}
