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

package uart_test_suite8;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import com.serialpundit.core.SerialComException;
import com.serialpundit.core.util.SerialComUtil;

import flash.stm32.core.internal.FlashUtils;
import flash.stm32.core.DeviceManager;
import flash.stm32.core.HexFirmware;

public final class UARTtest8 {

	private DeviceManager devMgr;

	protected void begin() throws SerialComException {

		devMgr = new DeviceManager(new Locale("English", "EN"));

        // use large files for testing
		System.out.println("---- Test 81 HEX PARSER a.hex started 109.8KiB -----------");
		try {
			// Read hex file and create byte array out of it
			File f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/a.hex");
			int lengthOfFileContents = (int) f.length();
			byte[] hexBuf = new byte[lengthOfFileContents];
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));
			int numBytesToRead = lengthOfFileContents;
			int x = 0;
			int offset = 0;
			int numBytesActuallyRead = 0;
			int totalBytesReadTillNow = 0;
			for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
				numBytesActuallyRead = inStream.read(hexBuf, offset, numBytesToRead);
				totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
				offset = totalBytesReadTillNow;
				numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
			}
			inStream.close();

			// data now contains hex file in array, parse it to generate bin format buffer
			FlashUtils fu = new FlashUtils(
					ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", new Locale("English", "EN")));
			HexFirmware hf = fu.hexToBinFwFormat(hexBuf);
			byte[] binBuf = hf.fwInBinFormat;
			SerialComUtil.byteArrayToHexString(binBuf, " ");
			System.out.println("---- Test 81 HEX PARSER a.hex ended-----------");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---- Test 82 HEX PARSER b.hex started 20.1 MiB -----------");
		try {
			// Read hex file and create byte array out of it
			File f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/b.hex");
			int lengthOfFileContents = (int) f.length();
			byte[] hexBuf = new byte[lengthOfFileContents];
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));
			int numBytesToRead = lengthOfFileContents;
			int x = 0;
			int offset = 0;
			int numBytesActuallyRead = 0;
			int totalBytesReadTillNow = 0;
			for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
				numBytesActuallyRead = inStream.read(hexBuf, offset, numBytesToRead);
				totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
				offset = totalBytesReadTillNow;
				numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
			}
			inStream.close();

			// data now contains hex file in array, parse it to generate bin format buffer
			FlashUtils fu = new FlashUtils(
					ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", new Locale("English", "EN")));
			HexFirmware hf = fu.hexToBinFwFormat(hexBuf);
			byte[] binBuf = hf.fwInBinFormat;
			SerialComUtil.byteArrayToHexString(binBuf, " ");
			System.out.println("---- Test 82 HEX PARSER b.hex ended-----------");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---- Test 83 HEX PARSER c.hex started 10.3 MiB -----------");
		try {
			// Read hex file and create byte array out of it
			File f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/c.hex");
			int lengthOfFileContents = (int) f.length();
			byte[] hexBuf = new byte[lengthOfFileContents];
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));
			int numBytesToRead = lengthOfFileContents;
			int x = 0;
			int offset = 0;
			int numBytesActuallyRead = 0;
			int totalBytesReadTillNow = 0;
			for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
				numBytesActuallyRead = inStream.read(hexBuf, offset, numBytesToRead);
				totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
				offset = totalBytesReadTillNow;
				numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
			}
			inStream.close();

			// data now contains hex file in array, parse it to generate bin format buffer
			FlashUtils fu = new FlashUtils(
					ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", new Locale("English", "EN")));
			HexFirmware hf = fu.hexToBinFwFormat(hexBuf);
			byte[] binBuf = hf.fwInBinFormat;
			SerialComUtil.byteArrayToHexString(binBuf, " ");
			System.out.println("---- Test 83 HEX PARSER c.hex ended-----------");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---- Test 84 HEX PARSER d.hex started 22.4 KiB -----------");
		try {
			// Read hex file and create byte array out of it
			File f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/d.hex");
			int lengthOfFileContents = (int) f.length();
			byte[] hexBuf = new byte[lengthOfFileContents];
			BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));
			int numBytesToRead = lengthOfFileContents;
			int x = 0;
			int offset = 0;
			int numBytesActuallyRead = 0;
			int totalBytesReadTillNow = 0;
			for (x = 0; x < lengthOfFileContents; x = totalBytesReadTillNow) {
				numBytesActuallyRead = inStream.read(hexBuf, offset, numBytesToRead);
				totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
				offset = totalBytesReadTillNow;
				numBytesToRead = lengthOfFileContents - totalBytesReadTillNow;
			}
			inStream.close();

			// data now contains hex file in array, parse it to generate bin format buffer
			FlashUtils fu = new FlashUtils(
					ResourceBundle.getBundle("flash.stm32.resources.MessagesBundle", new Locale("English", "EN")));
			HexFirmware hf = fu.hexToBinFwFormat(hexBuf);
			byte[] binBuf = hf.fwInBinFormat;
			SerialComUtil.byteArrayToHexString(binBuf, " ");
			System.out.println("---- Test 84 HEX PARSER d.hex ended-----------");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---- Test 85 DETECT HEX FILE started -----------");
		try {
			// give actual hex file and expect true in return
			File f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/c.hex");
			System.out.println("HEX DETECTED (c.hex) = " + devMgr.isFileInHexFormat(f));

			// give invalid file and expect false in return
			f = new File("/home/a/Ws-ProgSTM32/workspace/testhex/demo.bin");
			System.out.println("HEX DETECTED (demo.bin) = " + devMgr.isFileInHexFormat(f));

			System.out.println("---- Test 85 DETECT HEX FILE ended-----------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SerialComException {
		UARTtest8 app = new UARTtest8();
		app.begin();
	}
}
