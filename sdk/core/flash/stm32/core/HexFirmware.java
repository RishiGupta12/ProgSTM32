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

package flash.stm32.core;

/**
 * <p>
 * Internal use only.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class HexFirmware {

    public final byte[] fwInBinFormat;
    public final int address;

    /**
     * <p>
     * Contains firmware in binary format and address where this firmware should be
     * flashed.
     * </p>
     * 
     * @param fwInBinFormat
     *            firmware converted from hex format to bin format.
     * @param address
     *            address where this firmware should be flashed
     */
    public HexFirmware(byte[] fwInBinFormat, int address) {
        this.fwInBinFormat = fwInBinFormat;
        this.address = address;
    }
}
