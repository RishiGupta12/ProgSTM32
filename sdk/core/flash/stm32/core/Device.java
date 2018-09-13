/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

/**
 * <p>
 * Base class representing a stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public abstract class Device {

    protected int pid;
    protected int numBanks;
    protected int numPagesInABank;
    protected int pageSize;
    protected int flashMemSize;
    protected int mainMemStartAddr;
    protected int mainMemEndAddr;
    protected int IBSysMemStartAddr;
    protected int IBSysMemeEndAddr;

}
