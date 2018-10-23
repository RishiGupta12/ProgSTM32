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

package uart_test_suite1;

import java.io.File;
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
import flash.stm32.uart.UARTInterface;

public final class UARTtest1 {

	private DeviceManager devMgr;
	private UARTInterface uci;
	private boolean opened = false;
	
	private String PORT = "/dev/ttyACM0";

	protected void begin() throws SerialComException {

		try {
			System.out.println("----------- Test 1 DeviceManager -----------");
			devMgr = new DeviceManager(new Locale("English", "EN"));

			System.out.println("----------- Test 2 getCommunicationIface -----------");
			uci = (UARTInterface) devMgr.getCommunicationIface(IFace.UART, "proguartx3971");

			System.out.println("----------- Test 3 uart open -----------");
			uci.open(PORT, BAUDRATE.B115200, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_EVEN, FLOWCONTROL.NONE);
			opened = true;

			System.out.println("----------- Test 4 initAndIdentifyDevice -----------");
			// Identify device
			Device dev = uci.initAndIdentifyDevice();

			System.out.println("----------- Test 5 getMCUInformation -----------");
			// Get general information about device
			int[] devInfo = dev.getMCUInformation();
			
			System.out.println("PID = 0x" + SerialComUtil.intToHexString(devInfo[0]));

			System.out.println("----------- Test 6 -----------");
			// Get bootloader version
			System.out.println("Bootloader version = " + dev.getBootloaderVersion());

			System.out.println("----------- Test 7 -----------");
			// Get commands supported by bootloader
			System.out.println("Commands supported = " + dev.getAllowedCommands());

			System.out.println("----------- Test 8 -----------");
			// Get read protection status
			// Disable read protection (generates RESET after cmd has been executed)
			System.out.println("Read protection status = "
					+ SerialComUtil.byteArrayToHexString(dev.getReadProtectionStatus(), ":"));
			dev.readoutUnprotectMemoryRegion();
			System.out.println("Disabled readout protection");
			Thread.sleep(1);
			dev = uci.initAndIdentifyDevice();
			System.out.println("Read protection status = "
					+ SerialComUtil.byteArrayToHexString(dev.getReadProtectionStatus(), ":"));

			System.out.println("----------- Test 9 -----------");
			// Enable read protection (generates RESET after cmd has been executed)
			dev.readoutprotectMemoryRegion();
			System.out.println("Enabled readout protection");
			Thread.sleep(1);
			dev = uci.initAndIdentifyDevice();
			System.out.println("Read protection status after unprotect+protect = "
					+ SerialComUtil.byteArrayToHexString(dev.getReadProtectionStatus(), ":"));

			System.out.println("----------- Test 10 -----------");
			System.out.println("----------- Test 11 -----------");

			// // Disable and Enable write protection (generates RESET after cmd has been //
			// executed) try { dev.writeProtectMemoryRegion(0x08000000, 1);
			// System.out.println("Enabled write protection"); Thread.sleep(1); dev =
			// uci.connectAndIdentifyDevice(); dev.writeUnprotectMemoryRegion();
			// System.out.println("Disabled write protection"); Thread.sleep(1); dev =
			// uci.connectAndIdentifyDevice(); } catch (Exception e) { e.printStackTrace();
			// } try { // 1st disable read protection and then write protect as bootloader
			// sends NACK // if read protection is enabled
			// dev.readoutUnprotectMemoryRegion(); Thread.sleep(1); dev =
			// uci.connectAndIdentifyDevice(); dev.writeProtectMemoryRegion(1, 2);
			// System.out.println("Enabled write protection after disabling read protection"
			// ); Thread.sleep(1); dev = uci.connectAndIdentifyDevice();
			// dev.writeUnprotectMemoryRegion();
			// System.out.println("Disabled write protection"); Thread.sleep(1); dev =
			// uci.connectAndIdentifyDevice(); } catch (Exception e) { e.printStackTrace();
			// }

			System.out.println("----------- Test 12 -----------");
			try {
				// Read option bytes
				dev.readoutprotectMemoryRegion();
				System.out.println("Enabled readout protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
				byte[] dataReadBuf = new byte[8];
				int numBytesRead = dev.readMemory(dataReadBuf, 0x1FFF7800, 8, null);
				System.out.println(
						"Read bytes " + numBytesRead + " " + SerialComUtil.byteArrayToHexString(dataReadBuf, ":"));
			} catch (Exception e) {
				// Because read protection was enabled read must fail
				e.printStackTrace();
			}

			System.out.println("\n----------- Test 13 -----------");
			try {
				// Read option bytes after read protection is not enabled
				// Read bytes 16
				// AA:F8:EF:FF:55:07:10:00
				dev.readoutUnprotectMemoryRegion();
				System.out.println("Disabled readout protection");
				Thread.sleep(1);
				dev = uci.initAndIdentifyDevice();
				byte[] dataReadBuf = new byte[8];
				int numBytesRead = dev.readMemory(dataReadBuf, 0x1FFF7800, 8, null);
				System.out.println(
						"Read bytes " + numBytesRead + " " + SerialComUtil.byteArrayToHexString(dataReadBuf, ":"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("----------- Test 14 -----------");
			try {
				dev.writeMemory(FileType.BIN, new File("/home/a/Desktop/dw3/led/main.bin"), 0x08000000, null);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("----------- Test XXXXXXXXXXX -----------");
			uci.close();

		} catch (Exception e) {
			e.printStackTrace();
			if (opened == true) {
				uci.close();
			}
		}
	}

	public static void main(String[] args) throws SerialComException {
		UARTtest1 app = new UARTtest1();
		app.begin();
	}

}
