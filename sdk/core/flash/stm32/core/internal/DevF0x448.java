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
 * Represents a DevF0x448 stm32 device.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class DevF0x448 extends Device {

    /**
     * <p>
     * Allocates an instance of DevF0x448 class.
     * </p>
     * 
     * @param cmdExtr
     *            an instance of concrete class that implements stm32 bootloader
     *            protocol
     */
    public DevF0x448(CommandExecutor cmdExtr) {
        this.cmdExtr = cmdExtr;
        pid = 0x448;
        mcuSeries = "STM32F070xB - STM32F071xx/072xx";
        RAMMemStartAddr = 0x00;
        RAMMemEndAddr = 0x00;
        IBSysMemStartAddr = 0x1FFFC800;
        IBSysMemEndAddr = 0x1FFFC800;
        resetCodeAddress = 0x20001800;
    }
}
