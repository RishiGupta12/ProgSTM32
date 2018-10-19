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

package uart_test_suite4;

import java.io.File;
import java.io.FileOutputStream;
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
import flash.stm32.core.FileType;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

public final class UARTtest4 {

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

			System.out.println("\n----------- Test 41 write array 8 byte -----------");
			try {
				// write data
				byte[] wrtBuf = new byte[] { 1, 3, 5, 7, 2, 6, 4, 9 };
				dev.writeMemory(FileType.BIN, wrtBuf, 0x08000000, null);
				System.out.println("Written:" + SerialComUtil.byteArrayToHexString(wrtBuf, ":"));

				// read data
				byte[] readBuf = new byte[8];
				int numBytesRead = dev.readMemory(readBuf, 0x08000000, 8, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));

				// data read must be equal to data written
				int x = 0;
				for (x = 0; x < numBytesRead; x++) {
					if (wrtBuf[x] != readBuf[x]) {
						throw new Exception("Failed at x: " + x + " data " + wrtBuf[x] + " " + readBuf[x]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Thread.sleep(1);

			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 42 write array 16 byte -----------");
			try {
				// write data
				byte[] wrtBuf1 = new byte[] { 1, 6, 5, 8, 2, 5, 4, 9, 8, 7, 1, 7, 3, 0, 1, 4 };
				dev.writeMemory(FileType.BIN, wrtBuf1, 0x08000000, null);
				System.out.println("Written: " + SerialComUtil.byteArrayToHexString(wrtBuf1, ":"));

				// read data
				byte[] readBuf1 = new byte[16];
				int numBytesRead = dev.readMemory(readBuf1, 0x08000000, 16, null);
				System.out.println("Read: " + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf1, ":"));

				// data read must be equal to data written
				int x = 0;
				for (x = 0; x < 16; x++) {
					if (wrtBuf1[x] != readBuf1[x]) {
						throw new Exception("Failed at x: " + x + " data " + wrtBuf1[x] + " " + readBuf1[x]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Thread.sleep(1);

			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 43 write array 8 byte -----------");
			try {
				// write data
				byte[] wrtBuf = new byte[] { 1, 3, 5, 7, 2, 6, 4, 9 };
				dev.writeMemory(FileType.BIN, wrtBuf, 0x08000000, null);
				System.out.println("Written:" + SerialComUtil.byteArrayToHexString(wrtBuf, ":"));

				// read data
				byte[] readBuf = new byte[8];
				int numBytesRead = dev.readMemory(readBuf, 0x08000000, 8, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));

				// data read must be equal to data written
				int x = 0;
				for (x = 0; x < numBytesRead; x++) {
					if (wrtBuf[x] != readBuf[x]) {
						throw new Exception("Failed at x: " + x + " data " + wrtBuf[x] + " " + readBuf[x]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Thread.sleep(1);

			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 44 write array 8 byte -----------");
			try {
				byte[] wrtBuf = new byte[] { 0, 4, 6, 2, 3, 1, 5, 7 };
				dev.writeMemory(FileType.BIN, wrtBuf, 0x08000000, null);
				System.out.println("Written:" + SerialComUtil.byteArrayToHexString(wrtBuf, ":"));

				// read data
				byte[] readBuf = new byte[8];
				int numBytesRead = dev.readMemory(readBuf, 0x08000000, 8, null);
				System.out.println("Read :" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf, ":"));

				// data read must be equal to data written
				int x = 0;
				for (x = 0; x < numBytesRead; x++) {
					if (wrtBuf[x] != readBuf[x]) {
						throw new Exception("Failed at x: " + x + " data " + wrtBuf[x] + " " + readBuf[x]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Thread.sleep(1);

			try {
				dev.eraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 45 write file -----------");
			try {
				// read dummy file from host, write to stm32
				File f = new File("/home/a/Ws-ProgSTM32/workspace/uart_test_suite4/dummyfwrt1");
				long fsize = f.length();

				dev.writeMemory(FileType.BIN, f, 0x08000000, null);
				System.out.println("Written file " + fsize);

				// read from stm32 into a file
				byte[] data = new byte[(int) fsize];
				int r = dev.readMemory(data, 0x08000000, (int) fsize, null);
				System.out.println("Total read : " + r);

				FileOutputStream stream = new FileOutputStream(
						"/home/a/Ws-ProgSTM32/workspace/uart_test_suite4/dummyfred1");
				try {
					stream.write(data);
				} finally {
					stream.close();
				}

				// data read must be equal to data written
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
		UARTtest4 app = new UARTtest4();
		app.begin();
	}

}
