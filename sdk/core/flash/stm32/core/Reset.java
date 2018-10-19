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
 * Contains behaviour and data required for triggering system reset using
 * software.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class Reset {

    private final byte[] resetCode = { 0x01, 0x49, 0x02, 0x4a, 0x0a, 0x60, (byte) 0xfe, (byte) 0xe7, 0x0c, (byte) 0xed,
            0x00, (byte) 0xe0, 0x04, 0x00, (byte) 0xfa, 0x05 };

    /**
     * <p>
     * Assemble code in plain binary format that can be sent to stm32 and executed
     * to trigger system reset.
     * </p>
     * 
     * @param RAMMemStartAddr
     *            location which will be sent to bootloader for go command
     * @return assembled code
     */
    public byte[] getResetCode(int RAMMemStartAddr) {

        int y = resetCode.length;
        byte[] resetCodeWithStack = new byte[y + 8];

        int addr = 0x20002000;
        resetCodeWithStack[3] = (byte) ((addr >> 24) & 0x000000FF);
        resetCodeWithStack[2] = (byte) ((addr >> 16) & 0x000000FF);
        resetCodeWithStack[1] = (byte) ((addr >> 8) & 0x000000FF);
        resetCodeWithStack[0] = (byte) (addr & 0x000000FF);

        int ta = RAMMemStartAddr + 8 + 1;
        resetCodeWithStack[7] = (byte) ((ta >> 24) & 0x000000FF);
        resetCodeWithStack[6] = (byte) ((ta >> 16) & 0x000000FF);
        resetCodeWithStack[5] = (byte) ((ta >> 8) & 0x000000FF);
        resetCodeWithStack[4] = (byte) (ta & 0x000000FF);

        int x;
        int z = 0;
        y = y + 8;
        for (x = 8; x < y; x++) {
            resetCodeWithStack[x] = resetCode[z];
            z++;
        }

        return resetCodeWithStack;
    }
}
