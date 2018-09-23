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

    public byte[] hexToBinFwFormat(byte[] buf) {

        int x = 0;
        int curAddr = 0;
        int baseAddr = 0;
        int recordLength = 0;
        int recordType = 0;
        int checksum = 0;

        byte COLON = 0x3A;
        byte[] tmpbuf = new byte[4];
        ByteArrayOutputStream binfw = new ByteArrayOutputStream();

        while (true) {

            if (buf[x] != COLON) {
                x++;
                continue;
            }

            /* extract record length */
            recordLength = this.hexAsciiToIntValue(buf, x + 1, 2);

            /* extract current address */
            tmpbuf[0] = buf[x + 5];
            tmpbuf[1] = buf[x + 6];
            tmpbuf[2] = buf[x + 3];
            tmpbuf[3] = buf[x + 4];
            curAddr = this.hexAsciiToIntValue(tmpbuf, 0, 4);

            /* extract record type */
            recordType = this.hexAsciiToIntValue(buf, x + 7, 2);

            /* extract checksum */
            checksum = this.hexAsciiToIntValue(buf, x + (2 * recordLength) + 8, 2);

            switch (recordType) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            default:
                throw new IllegalArgumentException(rb.getString("invalid.record.type"));
            }

            break;
        }

        return null;
    }
}
