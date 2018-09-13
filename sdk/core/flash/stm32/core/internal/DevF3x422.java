/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

final class DevF3x422 extends Device {

    protected int pid;
    protected String mcuSeries;

    protected int flashMemSize;
    protected int mainMemStartAddr;
    protected int mainMemEndAddr;

    protected int numBanks;
    protected int numPagesInABank;
    protected int pageSize;

    protected int IBSysMemStartAddr;
    protected int IBSysMemEndAddr;

    protected int RAMMemStartAddr;
    protected int RAMMemEndAddr;

    public DevF3x422() {
        pid = 0x422;
        RAMMemStartAddr = 0x20001400;
        RAMMemEndAddr = 0x20009FFF;
    }
}
