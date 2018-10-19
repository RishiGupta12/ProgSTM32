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

package uart_test_suite11;

import java.util.Locale;

import com.serialpundit.core.SerialComException;
import com.serialpundit.core.util.SerialComUtil;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;

import flash.stm32.core.Device;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.DeviceManager.IFace;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

public final class UARTtest11 {

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

			try {
				dev.extendedEraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// OPTION BYTES RANGE FOR STM32L476RG IS 0x1FFFF800 - 0x1FFFF800 and 0x1FFF7800
			// - 0x1FFF7810
			System.out.println("\n----------- Test 11.1 READ OPTION BYTE -----------");
			try {
				int numBytesRead = 0;
				byte[] readBuf = new byte[16];

				numBytesRead = dev.readMemory(readBuf, 0x1FFF7800, 16, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 11.2 READ OPTION BYTE -----------");
			try {
				int numBytesRead = 0;
				byte[] readBuf = new byte[16];

				numBytesRead = dev.readMemory(readBuf, 0x1FFFF800, 4, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 11.3 SYSTEM MEMORY -----------");
			try {
				int numBytesRead = 0;
				byte[] readBuf = new byte[16];

				numBytesRead = dev.readMemory(readBuf, 0x1FFF8000, 4, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 11.4 SYSTEM MEMORY BOOTLOADER ID -----------");
			try {
				int numBytesRead = 0;
				byte[] readBuf = new byte[16];

				// Read :4 31:00:92:00:00:00:00:00:00:00:00:00:00:00:00:00
				numBytesRead = dev.readMemory(readBuf, 0x1FFF6FFF - 0x03, 4, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 11.5 BOOTLOADER ID -----------");
			try {
				int blid = dev.getBootloaderID();
				System.out.println("BootloaderID :" + Integer.toHexString(blid));
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
		UARTtest11 app = new UARTtest11();
		app.begin();
	}

}
