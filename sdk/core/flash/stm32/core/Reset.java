/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core;

public final class Reset {

    private final byte[] resetcode = { 0x01, 0x49, 0x02, 0x4a, 0x0a, 0x60, (byte) 0xfe, (byte) 0xe7, 0x0c, (byte) 0xed,
            0x00, (byte) 0xe0, 0x04, 0x00, (byte) 0xfa, 0x05 };

    public byte[] getResetCode() {
        return resetcode;
    }
}
