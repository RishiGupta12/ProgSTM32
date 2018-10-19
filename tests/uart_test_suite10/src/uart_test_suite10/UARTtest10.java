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

package uart_test_suite10;

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
import flash.stm32.core.ICmdProgressListener;
import flash.stm32.core.REGTYPE;
import flash.stm32.uart.UARTInterface;

public final class UARTtest10 implements ICmdProgressListener {

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

			System.out.println("\n----------- Test 101 erase and write hex fw file --------");
			try {
				System.out.println("Erasing flash");
				dev.extendedEraseMemoryRegion(REGTYPE.MAIN, 0, 4);
				System.out.println("Erased flash");
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Writing hex file");
				dev.writeMemory(FileType.HEX, new File("/home/a/exp/STM32CubeL4_Demo_STM32L476RG_Nucleo.hex"),
						0x08000000, this);
				System.out.println("Written hex file");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("----------- Test 101 erase and write hex fw file done -----");

			uci.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (opened == true) {
				uci.close();
			}
		}
	}

	public static void main(String[] args) throws SerialComException {
		UARTtest10 app = new UARTtest10();
		app.begin();
	}

	@Override
	public void onDataReadProgressUpdate(int arg0, int arg1) {
	}

	@Override
	public void onDataWriteProgressUpdate(long arg0, int arg1) {
		System.out.println("" + arg0 + " " + arg1);
	}

}
