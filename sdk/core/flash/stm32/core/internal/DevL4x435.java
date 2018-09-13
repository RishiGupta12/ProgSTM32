/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevL4x435 extends Device {

    public DevL4x435(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x435;
        mcuSeries = "STM32L43xxx/44xxx";
        RAMMemStartAddr = 0x20003100;
        RAMMemEndAddr = 0x2000BFFF;
        IBSysMemStartAddr = 0x1FFF0000;
        IBSysMemEndAddr = 0x1FFF6FFF;
    }
}
