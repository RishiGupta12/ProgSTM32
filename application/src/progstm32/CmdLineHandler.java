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
 * If the application is executing in commandline mode, it extracts arguments
 * and execute the user given command.
 * </p>
 * 
 * @author Rishi Gupta
 */
public final class CmdLineHandler {

    int ACT_WRITE = 0x01;
    int ACT_ERASE = 0x02;
    int ACT_MASS_ERASE = 0x03;
    int ACT_READ = 0x02;
    int ACT_SHOW_HELP = 0x01;

    public void process(String[] args) {

        int numArgs = args.length;
        int action = 0;
        int startPageNum = 0;
        int totalPageNum = 0;
        int baudrate = 0;
        String device = null;

        for (int i = 0; i < numArgs; i++) {

            switch (args[i]) {

            case "-w":
                action = ACT_WRITE;
                break;

            case "-e":
                i++;
                if (args[i].equals("m")) {
                    action = ACT_MASS_ERASE;
                } else {
                    action = ACT_ERASE;
                    startPageNum = Integer.parseInt(args[i]);
                    i++;
                    totalPageNum = Integer.parseInt(args[i]);
                }
                break;

            case "-r":
                break;

            case "-ih":
                break;

            case "-b":
                break;

            case "-br":
                i++;
                baudrate = Integer.parseInt(args[i]);
                break;

            case "-d":
                i++;
                device = args[i];
                break;

            case "-h":
                break;
            default:
                throw new IllegalArgumentException("Invalid option");
            }
        }

        System.out.println("Option : " + action + " " + startPageNum + " " + totalPageNum);
    }
}
