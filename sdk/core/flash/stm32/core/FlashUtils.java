/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

import java.util.ResourceBundle;
import java.io.ByteArrayOutputStream;

public final class FlashUtils {

    private final ResourceBundle rb;

    public FlashUtils(ResourceBundle rb) {
        this.rb = rb;
    }

    private int hexAsciiToIntValue(final byte[] data, int offset, int length) {

        int x;
        StringBuilder sBuilder = new StringBuilder();

        for (x = 0; x < length; x++) {
            sBuilder.append((char) data[offset]);
            offset++;
        }

        return Integer.parseInt(sBuilder.toString(), 16);
    }

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

        return 0;
    }

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

    public byte[] hexToBinFwFormat(byte[] hexbuf) {

        int x = 0;
        int y = 0;
        int fillerData = 0;

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
        byte[] tmpbuf = new byte[4];
        ByteArrayOutputStream binbuf = new ByteArrayOutputStream();

        while (true) {

            if (hexbuf[x] != COLON) {
                x++;
                continue;
            }

            /* extract current record length */
            curRecordLength = this.hexAsciiToIntValue(hexbuf, x + 1, 2);

            /* extract current record type */
            curRecordType = this.hexAsciiToIntValue(hexbuf, x + 7, 2);

            /* extract and verify checksum of current record */
            curChkSum = this.hexAsciiToIntValue(hexbuf, x + (2 * curRecordLength) + 8, 2);
            y = this.calCheckSum(hexbuf, x + 1, (2 * curRecordLength) + 8);
            if (y != curChkSum) {
                throw new IllegalArgumentException(rb.getString("invalid.checksum") + "at record" + x);
            }

            switch (curRecordType) {

            case 0x00:
                /* data record */
                tmpbuf[0] = hexbuf[x + 5];
                tmpbuf[1] = hexbuf[x + 6];
                tmpbuf[2] = hexbuf[x + 3];
                tmpbuf[3] = hexbuf[x + 4];

                curDataOffset = this.hexAsciiToIntValue(tmpbuf, 0, 4);
                curAbsAddr = curBaseAddr + curDataOffset;

                fillerData = curAbsAddr - prevAbsAddr + prevRecordLength;
                if (fillerData > 0) {
                    for (y = 0; y < fillerData; y++) {
                        binbuf.write(FILLER);
                    }
                }

                this.hexAsciiToByteArray(hexbuf, x + 8, curRecordLength, binbuf);

                prevAbsAddr = curAbsAddr;
                prevRecordLength = curRecordLength;
                x = x + 9 + (2 * curRecordLength);

            case 0x01:
                /* end of file */
                return binbuf.toByteArray();

            case 0x02:
                /* extended segment address */
                tmpbuf[0] = hexbuf[x + 11];
                tmpbuf[1] = hexbuf[x + 12];
                tmpbuf[2] = hexbuf[x + 9];
                tmpbuf[3] = hexbuf[x + 10];
                curBaseAddr = this.hexAsciiToIntValue(tmpbuf, 0, 4);
                curBaseAddr = curBaseAddr << 4;

                x = x + 14;

            case 0x03:
                /* start segment address */
                /*
                 * not applicable for ARM CPU, increment to go to next record and ignoring
                 * current record
                 */
                x++;

            case 0x04:
                /* extended linear address */
                tmpbuf[0] = hexbuf[x + 11];
                tmpbuf[1] = hexbuf[x + 12];
                tmpbuf[2] = hexbuf[x + 9];
                tmpbuf[3] = hexbuf[x + 10];
                curBaseAddr = this.hexAsciiToIntValue(tmpbuf, 0, 4);

                x = x + 14;

            case 0x05:
                /* start linear address */
                /*
                 * not applicable for ARM CPU, increment to go to next record and ignoring
                 * current record
                 */
                x++;

            default:
                throw new IllegalArgumentException(rb.getString("invalid.record.type"));
            }
        }
    }
}
