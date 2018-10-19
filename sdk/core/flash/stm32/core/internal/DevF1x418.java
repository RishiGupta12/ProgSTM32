/* 
 * This file is part of progstm32.
 * 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 * 
 * The progstm32 is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 * 
 * The progstm32 is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation,Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

/**
 * <p>
 * Represents a DevF1x418 stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class DevF1x418 extends Device {

    /**
     * <p>
     * Allocates an instance of DevF1x418 class.
     * </p>
     * 
     * @param cmdExtr
     *            an instance of concrete class that implements stm32 bootloader
     *            protocol
     */
    public DevF1x418(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x418;
        mcuSeries = "STM32F105xx - 107xx";
        RAMMemStartAddr = 0x20001000;
        RAMMemEndAddr = 0x2000FFFF;
        IBSysMemStartAddr = 0x1FFFB000;
        IBSysMemEndAddr = 0x1FFFF7FF;
        resetCodeAddress = RAMMemStartAddr;
    }
}
