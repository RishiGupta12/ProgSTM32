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

package uart_test_suite13;

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
import flash.stm32.core.FileType;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

// Nucleo L053R8 write, erase and read, compare page by page erase
public final class UARTtest13 {

	private DeviceManager devMgr;
	private UARTInterface uci;
	private boolean opened = false;

	protected void begin() throws SerialComException {

		try {
			devMgr = new DeviceManager();
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

			System.out.println("\n----------- Test 13.1 write array 8 byte -----------");
			try {
				byte[] wrtBuf = new byte[512];
				int q = 0;

				for (q = 0; q < 512; q++) {
					wrtBuf[q] = 0x2B;
				}

				dev.writeMemory(FileType.BIN, wrtBuf, 0x08000000, null);
				System.out.println("Written:" + "wrtBuf.length : " + wrtBuf.length + " "
						+ SerialComUtil.byteArrayToHexString(wrtBuf, ":"));
				byte[] readBuf1 = new byte[8];
				int numBytesRead = dev.readMemory(readBuf1, 0x08000000, readBuf1.length, null);
				System.out.println("Read 1:" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf1, ":"));

				byte[] readBuf2 = new byte[128];
				numBytesRead = dev.readMemory(readBuf2, 0x08000000, readBuf2.length, null);
				System.out.println("Read 2:" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf2, ":"));

				byte[] readBuf3 = new byte[256];
				numBytesRead = dev.readMemory(readBuf3, 0x08000000, readBuf3.length, null);
				System.out.println("Read 3:" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf3, ":"));

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
		UARTtest13 app = new UARTtest13();
		app.begin();
	}

}
