/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevH7x450 extends Device {

    public DevH7x450() {
        pid = 0x450;
        mcuSeries = "STM32H74xxx/75xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FF00000;
        IBSysMemEndAddr = 0x1FF1E7FF;
    }
}
