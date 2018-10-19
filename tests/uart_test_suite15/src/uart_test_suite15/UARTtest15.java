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

package uart_test_suite15;

import java.io.File;

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

// Nucleo L476RG write, erase and read, compare page by page erase
public final class UARTtest15 {

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
			
//			dev.readoutUnprotectMemoryRegion();
//			Thread.sleep(2);
//			dev = uci.initAndIdentifyDevice();
//			
//			dev.writeUnprotectMemoryRegion();
//			Thread.sleep(2);
//			dev = uci.initAndIdentifyDevice();

			try {
				dev.extendedEraseMemoryRegion(REGTYPE.MAIN, 0, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 13.1 write array 8 byte -----------");
			try {				
				int q = 0;
				
				int page1 = 0x08000000;
				int page2 = 0x08000800;
				int page3 = 0x08001000;
				
				// page 2
				byte[] readBuf2 = new byte[2048];
				int numBytesRead2 = dev.readMemory(readBuf2, page2, readBuf2.length, null);
				System.out.println("Read 2:" + numBytesRead2 + " " + SerialComUtil.byteArrayToHexString(readBuf2, ":"));

//				// page 1
//				byte[] wrtBuf1 = new byte[2048];
//				for (q = 0; q < 2048; q++) {
//					wrtBuf1[q] = 0x2B;
//				}
//				dev.writeMemory(FileType.BIN, wrtBuf1, page1, null);
//				System.out.println("Writ :" + wrtBuf1.length + " " + SerialComUtil.byteArrayToHexString(wrtBuf1, ":"));
//				
//				// page 2
//				byte[] wrtBuf2 = new byte[2048];
//				for (q = 0; q < 2048; q++) {
//					wrtBuf2[q] = 0x3A;
//				}
//				dev.writeMemory(FileType.BIN, wrtBuf2, page2, null);
//				System.out.println("Writ :" + wrtBuf2.length + " " + SerialComUtil.byteArrayToHexString(wrtBuf2, ":"));
//				
//				// page 3
//				byte[] wrtBuf3 = new byte[2048];
//				for (q = 0; q < 2048; q++) {
//					wrtBuf3[q] = 0x11;
//				}
//				dev.writeMemory(FileType.BIN, wrtBuf3, page3, null);
//				System.out.println("Writ :" + wrtBuf3.length + " " + SerialComUtil.byteArrayToHexString(wrtBuf3, ":"));
//				
//				dev.writeMemory(FileType.BIN, new File("/home/a/Ws-ProgSTM32/workspace/testhex/k.hex"), 0x08000000, null);
//
//				// page 1
//				byte[] readBuf1 = new byte[2048];
//				int numBytesRead = dev.readMemory(readBuf1, page1, readBuf1.length, null);
//				System.out.println("Read 1:" + numBytesRead + " " + SerialComUtil.byteArrayToHexString(readBuf1, ":"));
//				
//				// page 2
//				byte[] readBuf2 = new byte[2048];
//				int numBytesRead2 = dev.readMemory(readBuf2, page2, readBuf2.length, null);
//				System.out.println("Read 2:" + numBytesRead2 + " " + SerialComUtil.byteArrayToHexString(readBuf2, ":"));
//				
//				// page 3
//				byte[] readBuf3 = new byte[2048];
//				int numBytesRead3 = dev.readMemory(readBuf3, page3, readBuf3.length, null);
//				System.out.println("Read 3:" + numBytesRead3 + " " + SerialComUtil.byteArrayToHexString(readBuf3, ":"));

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
		UARTtest15 app = new UARTtest15();
		app.begin();
	}

}
