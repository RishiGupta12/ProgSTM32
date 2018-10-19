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

package uart_test_suite5;

import java.util.Locale;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

import flash.stm32.core.BLCMDS;
import flash.stm32.core.Device;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.DeviceManager.IFace;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

public final class UARTtest5 {

	private DeviceManager devMgr;
	private UARTInterface uci;
	private boolean opened = false;

	protected void begin() throws SerialComException {

		try {
			devMgr = new DeviceManager(new Locale("English", "EN"));
			uci = (UARTInterface) devMgr.getCommunicationIface(IFace.UART, "proguartx3971");
			uci.open("/dev/ttyACM0", BAUDRATE.B115200, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_EVEN, FLOWCONTROL.NONE);
			opened = true;

			Device dev = uci.initAndIdentifyDevice();
			int[] devInfo = dev.getMCUInformation();
			System.out.println("PID = " + devInfo[0]);

			int eraseType = dev.getAllowedCommands();
			if ((eraseType & BLCMDS.ERASE) == BLCMDS.ERASE) {
				System.out.println("Device supports ERASE TYPE");
			} else if ((eraseType & BLCMDS.EXTENDED_ERASE) == BLCMDS.EXTENDED_ERASE) {
				System.out.println("Device supports EXTENDED ERASE TYPE");
			} else {
				System.out.println("UNKNOWN ERASE SUPPORTED");
			}

			System.out.println("\n----------- Test 51 page erase -----------");
			// erase page 0 erase
			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 52 mass erase -----------");
			// mass erase
			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, -1, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 53 extended page 0 erase -----------");
			// extended page erase
			try {
				long start = System.currentTimeMillis();
				dev.extendedEraseMemoryRegion(REGTYPE.MAIN, 0, 2);
				System.out.println("----------- Test 53 extended page 0 erase took-----------"
						+ (System.currentTimeMillis() - start));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 54 extended mass erase -----------");
			// extended mass erase
			try {
				long start = System.currentTimeMillis();
				dev.extendedEraseMemoryRegion(REGTYPE.MAIN, -1, -1);
				System.out.println("----------- Test 54 extended mass erase took -----------"
						+ (System.currentTimeMillis() - start));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 55 extended bank1 erase -----------");
			// extended page erase
			try {
				long start = System.currentTimeMillis();
				dev.extendedEraseMemoryRegion(REGTYPE.BANK1, 0, 2);
				System.out.println("----------- Test 55 extended bank1 erase took-----------"
						+ (System.currentTimeMillis() - start));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 56 extended bank2 erase -----------");
			// extended page erase
			try {
				long start = System.currentTimeMillis();
				dev.extendedEraseMemoryRegion(REGTYPE.BANK2, 0, 2);
				System.out.println("----------- Test 56 extended bank2 erase took-----------"
						+ (System.currentTimeMillis() - start));
			} catch (Exception e) {
				e.printStackTrace();
			}

			uci.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (opened == true) {
				uci.close();
			}
		}
	}

	public static void main(String[] args) throws SerialComException {
		UARTtest5 app = new UARTtest5();
		app.begin();
	}

}
