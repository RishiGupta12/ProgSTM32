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
package flash.stm32.core;

import java.util.ResourceBundle;

import flash.stm32.core.HexFirmware;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FlashUtils {

    private ResourceBundle rb;

    /**
     * <p>
     * Allocate an instance of FlashUtils using the given resource bundle. This does
     * not alter the resource bundle hence language.
     * </p>
     */
    public FlashUtils() {
    }

    /**
     * <p>
     * Allocate an instance of FlashUtils using the given resource bundle.
     * </p>
     * 
     * @param rb
     *            resource bundle mainly containing language specific items.
     */
    public FlashUtils(ResourceBundle rb) {
        this.rb = rb;
    }

    /**
     * <p>
     * Converts ASCII hex data in data buffer to its equivalent integer value
     * starting from the given offset for the given length of data.
     * </p>
     * 
     * @param data
     *            buffer containing ASCII hex values
     * @param offset
     *            starting location where from where conversion should start
     * @param length
     *            length of ASCII hex bytes to be converted
     * @return integer value calculated
     */
    private int hexAsciiToIntValue(final byte[] data, int offset, int length) {

        int x;
        StringBuilder sBuilder = new StringBuilder();

        for (x = 0; x < length; x++) {
            sBuilder.append((char) data[offset]);
            offset++;
        }

        return Integer.parseInt(sBuilder.toString(), 16);
    }

    /**
     * <p>
     * Converts ASCII hex data to its equivalent binary byte data and then
     * calculates checksum for the length of data given starting from the given
     * offset.
     * </p>
     * 
     * @param data
     *            buffer containing ASCII hex values
     * @param offset
     *            starting location where from where conversion should start
     * @param length
     *            length of ASCII hex bytes to be converted
     * @return checksum calculated
     */
    private int calCheckSum(final byte[] data, int offset, int length) {

        int x = 0;
        int val = 0;

        StringBuilder sBuilder = new StringBuilder();

        for (x = 0; x < length; x = x + 2) {
            sBuilder.append((char) data[offset]);
            offset++;
            sBuilder.append((char) data[offset]);
            offset++;
            val = val + Integer.parseInt(sBuilder.toString(), 16);
            sBuilder.setLength(0);
        }

        if (val > 0xFF) {
            sBuilder.setLength(0);

            /* convert to binary representation */
            String s1 = Integer.toBinaryString(val);

            /* replace 0's and 1's */
            int strLen = s1.length();
            for (x = 0; x < strLen; x++) {
                if (s1.charAt(x) == '0') {
                    sBuilder.append('1');
                } else {
                    sBuilder.append('0');
                }
            }
            String s2 = sBuilder.toString();

            /* add 1 to get final 2's complement */
            val = Integer.valueOf(s2, 2) + 1;

            /* drop everything except last byte */
            val = 0x000000FF & val;
        } else {
            val = 256 - val;
        }

        return val;
    }

    /**
     * <p>
     * Creates byte array out of ASCII hex data (2 hex character representing a
     * single byte) and write it to the given byte array output stream.
     * </p>
     * 
     * @param data
     *            buffer containing ASCII hex values
     * @param offset
     *            starting location where from where conversion should start
     * @param length
     *            length of ASCII hex bytes to be converted
     * @param outbuf
     *            stream to which converted data should be written
     * @return 0 on success
     */
    private int hexAsciiToByteArray(final byte[] data, int offset, int length, final ByteArrayOutputStream outbuf) {

        int x;
        byte val;
        StringBuilder sBuilder = new StringBuilder();

        for (x = 0; x < length; x = x + 2) {
            sBuilder.append((char) data[offset]);
            offset++;
            sBuilder.append((char) data[offset]);
            offset++;
            val = (byte) Integer.parseInt(sBuilder.toString(), 16);
            outbuf.write(val);
            sBuilder.setLength(0);
        }

        return 0;
    }

    /**
     * <p>
     * Converts data in given buffer to its equivalent data in binary format. It
     * extracts base address to which this firmware should be flashed.
     * </p>
     * 
     * @param hexbuf
     *            data in intel hex format
     * @return converted data and base address
     */
    public HexFirmware hexToBinFwFormat(byte[] hexbuf) {

        int x = 0;
        int y = 0;
        int fillerData = 0;
        int startFWaddress = 0;

        int curAbsAddr = 0;
        int curBaseAddr = 0;
        int curDataOffset = 0;
        int curRecordLength = 0;
        int curRecordType = 0;
        int curChkSum = 0;

        int prevAbsAddr = 0;
        int prevRecordLength = 0;

        int FILLER = 0xFF;
        byte COLON = 0x3A;
        ByteArrayOutputStream binbuf = new ByteArrayOutputStream();

        while (true) {

            if (hexbuf[x] != COLON) {
                /* bypass line feed and carriage return looking for a record starting with : */
                x++;
                continue;
            }

            /* extract current record length */
            curRecordLength = this.hexAsciiToIntValue(hexbuf, x + 1, 2);

            /* extract current record type */
            curRecordType = this.hexAsciiToIntValue(hexbuf, x + 7, 2);

            /* extract and verify checksum of current record */
            curChkSum = this.hexAsciiToIntValue(hexbuf, x + (2 * curRecordLength) + 9, 2);

            y = this.calCheckSum(hexbuf, x + 1, (2 * curRecordLength) + 8);
            if (y != curChkSum) {
                throw new IllegalArgumentException(rb.getString("inval.cksum") + " at record " + x);
            }

            switch (curRecordType) {

            case 0x00:
                /* data record */
                curDataOffset = this.hexAsciiToIntValue(hexbuf, x + 3, 4);

                curAbsAddr = curBaseAddr + curDataOffset;

                /*
                 * if the very 1st data record comes before very 1st address record which sets
                 * base address than padding must not be done. the exact start address is
                 * specified by caller in write command already.
                 */
                if ((prevAbsAddr != 0) && (prevRecordLength != 0)) {
                    fillerData = curAbsAddr - prevAbsAddr - prevRecordLength;
                    if (fillerData > 0) {
                        for (y = 0; y < fillerData; y++) {
                            binbuf.write(FILLER);
                        }
                    }
                } else {
                    startFWaddress = curAbsAddr;
                }

                this.hexAsciiToByteArray(hexbuf, x + 9, 2 * curRecordLength, binbuf);

                prevAbsAddr = curAbsAddr;
                prevRecordLength = curRecordLength;
                x = x + 9 + (2 * curRecordLength);
                break;

            case 0x01:
                /* end of file */
                HexFirmware hf = new HexFirmware(binbuf.toByteArray(), startFWaddress);
                return hf;

            case 0x02:
                /* extended segment address */
                curBaseAddr = this.hexAsciiToIntValue(hexbuf, 9, 4);
                curBaseAddr = curBaseAddr << 4;

                x = x + 14;
                break;

            case 0x03:
                /* start segment address */
                /*
                 * not applicable for ARM CPU, increment to go to next record and ignoring
                 * current record
                 */
                x++;
                break;

            case 0x04:
                /* extended linear address */
                curBaseAddr = this.hexAsciiToIntValue(hexbuf, (x + 9), 4);
                /* set upper 16 bits of base address */
                curBaseAddr = curBaseAddr << 16;

                x = x + 14;
                break;

            case 0x05:
                /* start linear address */
                /*
                 * not applicable for ARM CPU, increment to go to next record and ignoring
                 * current record
                 */
                x++;
                break;

            default:
                throw new IllegalArgumentException(rb.getString("inval.rcd.tp") + " " + curRecordType);
            }
        }
    }

    /**
     * <p>
     * Try to parse and check if the given file is in intel hex format or not.
     * </p>
     * 
     * @param file
     *            file which needs to be verified
     * @return true if file is in intel hex format otherwise false
     * @throws IOException
     *             if given file not found or unable to read it
     */
    public boolean isHexFormatFwFile(File file) throws IOException {

        int x;
        int y = 0;
        int offset = 0;
        int fileLength = 0;
        int numBytesActuallyRead = 0;
        int totalBytesReadTillNow = 0;
        BufferedInputStream inStream = null;
        byte[] data = null;

        try {
            fileLength = (int) file.length();
            int numBytesToRead = fileLength;

            data = new byte[fileLength];
            inStream = new BufferedInputStream(new FileInputStream(file));

            if (fileLength > 128) {
                numBytesToRead = 128;
                fileLength = 128;
            }
            for (x = 0; x < fileLength; x = totalBytesReadTillNow) {
                numBytesActuallyRead = inStream.read(data, offset, numBytesToRead);
                totalBytesReadTillNow = totalBytesReadTillNow + numBytesActuallyRead;
                offset = totalBytesReadTillNow;
                numBytesToRead = fileLength - totalBytesReadTillNow;
            }
            inStream.close();
        } catch (IOException e) {
            inStream.close();
            throw e;
        }

        try {
            for (x = 0; x < fileLength; x++) {
                if (y >= 4) {
                    return true;
                }
                if (data[x] != 0x3A) {
                    x++;
                    continue;
                }

                switch (this.hexAsciiToIntValue(data, x + 7, 2)) {
                case 0x00:
                case 0x01:
                case 0x02:
                case 0x03:
                case 0x04:
                case 0x05:
                    y++;
                    break;
                default:
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
