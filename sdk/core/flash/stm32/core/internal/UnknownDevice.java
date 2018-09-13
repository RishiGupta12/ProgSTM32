/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class UnknownDevice extends Device {

    public UnknownDevice(int pid) {
        this.pid = pid;
        mcuSeries = "xxxxxxxx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x00;
        IBSysMemEndAddr = 0x00;
    }
}
