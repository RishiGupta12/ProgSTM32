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
        int curAddr = 0;
        int baseAddr = 0;
        int recordLength = 0;
        int recordType = 0;
        int checksum = 0;

        byte COLON = 0x3A;
        byte[] tmpbuf = new byte[4];
        ByteArrayOutputStream binbuf = new ByteArrayOutputStream();

        while (true) {

            if (hexbuf[x] != COLON) {
                x++;
                continue;
            }

            /* extract record length */
            recordLength = this.hexAsciiToIntValue(hexbuf, x + 1, 2);

            /* extract current address */
            tmpbuf[0] = hexbuf[x + 5];
            tmpbuf[1] = hexbuf[x + 6];
            tmpbuf[2] = hexbuf[x + 3];
            tmpbuf[3] = hexbuf[x + 4];
            curAddr = this.hexAsciiToIntValue(tmpbuf, 0, 4);

            /* extract record type */
            recordType = this.hexAsciiToIntValue(hexbuf, x + 7, 2);

            /* extract checksum */
            checksum = this.hexAsciiToIntValue(hexbuf, x + (2 * recordLength) + 8, 2);

            //TODO cksum verify
            switch (recordType) {

            case 0x00:
                /* data record */
                this.hexAsciiToByteArray(hexbuf, x + 8, recordLength, binbuf);
                
            case 0x01:
                /* end of file */
                return binbuf.toByteArray();
                
            case 0x02:
                /* extended segment address */
                
            case 0x03:
                /* start segment address */
                /* not applicable for ARM CPU */
                
            case 0x04:
                /* extended linear address */
                
            case 0x05:
                /* start linear address */
                /* not applicable for ARM CPU */
                
            default:
                throw new IllegalArgumentException(rb.getString("invalid.record.type"));
            }
        }
    }
}



































