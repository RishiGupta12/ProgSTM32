/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import java.util.concurrent.TimeoutException;

import com.serialpundit.core.SerialComException;

import flash.stm32.core.internal.CommandExecutor;

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

    protected CommandExecutor cmdExtr;

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

    /**
     * 
     * @return
     */
    public int[] getMCUInformation() {

        int[] info = new int[11];

        info[0] = pid;
        info[1] = flashMemSize;
        info[2] = mainMemStartAddr;
        info[3] = mainMemEndAddr;
        info[4] = numBanks;
        info[5] = numPagesInABank;
        info[6] = pageSize;
        info[7] = IBSysMemStartAddr;
        info[8] = IBSysMemEndAddr;
        info[9] = RAMMemStartAddr;
        info[10] = RAMMemEndAddr;

        return info;
    }

    public int getAllowedCommands() throws SerialComException, TimeoutException {
        return cmdExtr.getAllowedCommands();
    }

    public String getBootloaderVersion() throws SerialComException, TimeoutException {
        return cmdExtr.getBootloaderVersion();
    }

    public int getChipID() throws SerialComException, TimeoutException {
        return cmdExtr.getChipID();
    }

    public int getReadProtectionStatus() throws SerialComException, TimeoutException {
        cmdExtr.getReadProtectionStatus();
    }
    
    public int readMemory(byte[] data, int startAddr, int numBytesToRead) throws SerialComException, TimeoutException {
        cmdExtr.readMemory();
    }
}
