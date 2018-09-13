/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL4x415 extends Device {

    public DevL4x415(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x415;
        mcuSeries = "STM32L47xxx/48xxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF6FFF;
    }
}
