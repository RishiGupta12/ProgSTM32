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

package uart_test_suite2;

import java.io.File;
import java.util.Locale;

import com.serialpundit.core.SerialComException;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

import flash.stm32.core.Device;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.DeviceManager.IFace;
import flash.stm32.core.FileType;
import flash.stm32.uart.UARTInterface;

public final class UARTtest2 {

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

			try {
				dev.readoutUnprotectMemoryRegion();
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
				System.out.println("Disabled read protection");

				dev.writeUnprotectMemoryRegion();
				System.out.println("Disabled write protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();

				dev.writeProtectMemoryRegion(0x08000000, 1);
				System.out.println("Enabled write protection");
				Thread.sleep(2);
				dev = uci.initAndIdentifyDevice();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			int[] devInfo = dev.getMCUInformation();
			System.out.println("PID = " + devInfo[0]);

			System.out.println("----------- Test 21 UNPROTECT WRITE -----------");
			try {
				dev.writeUnprotectMemoryRegion();
				System.out.println("Disabled write protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("----------- Test 22 FLASH FW FILE -----------");
			try {
				dev.writeMemory(FileType.BIN, new File("/home/a/exp/demo.bin"), 0x08000000, null);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("----------- Test 23 PROTECT WRITE -----------");
			try {
				dev.writeProtectMemoryRegion(0, 3);
				System.out.println("Enabled write protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("----------- Test 24 FLASH FW FILE -----------");
			try {
				dev.writeMemory(FileType.BIN, new File("/home/a/exp/demo.bin"), 0x08000000, null);
			} catch (Exception e) {
				// MUST fail as write protection is enabled
				e.printStackTrace();
			}

			System.out.println("----------- Test 25 UNPROTECT WRITE -----------");
			try {
				dev.writeUnprotectMemoryRegion();
				System.out.println("Disabled write protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
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
		UARTtest2 app = new UARTtest2();
		app.begin();
	}
}
