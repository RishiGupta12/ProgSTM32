/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF1x430 extends Device {

    public DevF1x430(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x430;
        mcuSeries = "STM32F10xxx XL-density";
        RAMMemStartAddr = 0x20000800;
        RAMMemEndAddr = 0x20017FFF;
        IBSysMemStartAddr = 0x1FFFE000;
        IBSysMemEndAddr = 0x1FFFF7FF;
    }
}
