/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

/**
 * <p>
 * Base class representing a stm32 device.
 * </p>
 * <p>
 * RAM address 0x00 means two or more devices have same pid but different
 * addresses. Therefore more concrete information has to be provided by caller.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class Device {

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

    /**
     * 
     * @param flashMemSize
     */
    public void setFlashMemSize(int flashMemSize) {
        // TODO update applicable parameters from datasheet
        this.flashMemSize = flashMemSize;
    }

    /**
     * 
     * @return
     */
    public String getuCSeries() {
        return mcuSeries;
    }
}
