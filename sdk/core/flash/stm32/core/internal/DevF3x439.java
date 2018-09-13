/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF3x439 extends Device {

    public DevF3x439(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x439;
        mcuSeries = "STM32F301xx/302x4(6/8) - STM32F318xx";
        RAMMemStartAddr = 0x20001800;
        RAMMemEndAddr = 0x20003FFF;
        IBSysMemStartAddr = 0x1FFFD800;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
