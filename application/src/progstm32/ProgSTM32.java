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

package progstm32;

/**
 * <p>
 * Entry class to the ProgSTM32 application.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class ProgSTM32 {

    private final static String AppVersion = "v1.0";

    /**
     * <p>
     * Entry point to the progstm32 application.
     * </p>
     * 
     * @param args
     *            user supplied arguments
     */
    public static void main(String[] args) {

        System.out.println("progstm32 " + AppVersion);

        CmdLineHandler cmdlhdlr = new CmdLineHandler();
        cmdlhdlr.process(args);
        return;
    }
}
