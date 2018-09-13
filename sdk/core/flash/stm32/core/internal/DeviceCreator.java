/* 
 * Copyright (C) 2018, Rishi Gupta. All rights reserved.
 */

package flash.stm32.core.internal;

import flash.stm32.core.Device;

/**
 * <p>
 * Handles concrete stm32 device specific information management.
 * </p>
 * 
 * @author Rishi Gupta
 */
public class DeviceCreator {

    public Device createDevFromPID(int pid, CommandExecutor cmdExtr) {

        switch (pid) {

        /* F3 series */
        case 0x422:
            return new DevF3x422();
        case 0x432:
            return new DevF3x432();
        case 0x438:
            return new DevF3x438();
        case 0x439:
            return new DevF3x439();
        case 0x446:
            return new DevF3x446();

        /* F4 series */
        case 0x413:
            return new DevF4x413();
        case 0x419:
            return new DevF4x419();
        case 0x421:
            return new DevF4x421();
        case 0x423:
            return new DevF4x423();
        case 0x431:
            return new DevF4x431();
        case 0x433:
            return new DevF4x433();
        case 0x441:
            return new DevF4x441();
        case 0x458:
            return new DevF4x458();

        /* F2 series */
        case 0x411:
            return new DevF2x411();

        /* F7 series */
        case 0x449:
            return new DevF7x449();
        case 0x451:
            return new DevF7x451();
        case 0x452:
            return new DevF7x452();

        /* F1 series */
        case 0x410:
            return new DevF1x410();
        case 0x412:
            return new DevF1x412();
        case 0x414:
            return new DevF1x414();
        case 0x420:
            return new DevF1x420();
        case 0x428:
            return new DevF1x428();

        /* H7 series */
        case 0x450:
            return new DevH7x450();

        /* L0 series */
        case 0x417:
            return new DevL0x417();
        case 0x425:
            return new DevL0x425();
        case 0x447:
            return new DevL0x447();
        case 0x457:
            return new DevL0x457();

        /* L1 series */
        case 0x416:
            return new DevL1x416();
        case 0x427:
            return new DevL1x427();
        case 0x429:
            return new DevL1x429();
        case 0x436:
            return new DevL1x436();
        case 0x437:
            return new DevL1x437();

        /* L4 series */
        case 0x415:
            return new DevL4x415();
        case 0x435:
            return new DevL4x435();
        case 0x4261:
            return new DevL4x461();
        case 0x462:
            return new DevL4x462();
        case 0x470:
            return new DevL4x470();

        /* F0 series */
        case 0x440:
            return new DevF0x440();
        case 0x442:
            return new DevF0x442();
        case 0x444:
            return new DevF0x444();
        case 0x445:
            return new DevF0x445();
        case 0x448:
            return new DevF0x448();

        }

        return new UnknownDevice(pid);
    }
}
