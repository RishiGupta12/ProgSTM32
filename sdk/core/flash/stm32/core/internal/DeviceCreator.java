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
            return new DevF3x422(cmdExtr);
        case 0x432:
            return new DevF3x432(cmdExtr);
        case 0x438:
            return new DevF3x438(cmdExtr);
        case 0x439:
            return new DevF3x439(cmdExtr);
        case 0x446:
            return new DevF3x446(cmdExtr);

        /* F4 series */
        case 0x413:
            return new DevF4x413(cmdExtr);
        case 0x419:
            return new DevF4x419(cmdExtr);
        case 0x421:
            return new DevF4x421(cmdExtr);
        case 0x423:
            return new DevF4x423(cmdExtr);
        case 0x431:
            return new DevF4x431(cmdExtr);
        case 0x433:
            return new DevF4x433(cmdExtr);
        case 0x441:
            return new DevF4x441(cmdExtr);
        case 0x458:
            return new DevF4x458(cmdExtr);

        /* F2 series */
        case 0x411:
            return new DevF2x411(cmdExtr);

        /* F7 series */
        case 0x449:
            return new DevF7x449(cmdExtr);
        case 0x451:
            return new DevF7x451(cmdExtr);
        case 0x452:
            return new DevF7x452(cmdExtr);

        /* F1 series */
        case 0x410:
            return new DevF1x410(cmdExtr);
        case 0x412:
            return new DevF1x412(cmdExtr);
        case 0x414:
            return new DevF1x414(cmdExtr);
        case 0x420:
            return new DevF1x420(cmdExtr);
        case 0x428:
            return new DevF1x428(cmdExtr);

        /* H7 series */
        case 0x450:
            return new DevH7x450(cmdExtr);

        /* L0 series */
        case 0x417:
            return new DevL0x417(cmdExtr);
        case 0x425:
            return new DevL0x425(cmdExtr);
        case 0x447:
            return new DevL0x447(cmdExtr);
        case 0x457:
            return new DevL0x457(cmdExtr);

        /* L1 series */
        case 0x416:
            return new DevL1x416(cmdExtr);
        case 0x427:
            return new DevL1x427(cmdExtr);
        case 0x429:
            return new DevL1x429(cmdExtr);
        case 0x436:
            return new DevL1x436(cmdExtr);
        case 0x437:
            return new DevL1x437(cmdExtr);

        /* L4 series */
        case 0x415:
            return new DevL4x415(cmdExtr);
        case 0x435:
            return new DevL4x435(cmdExtr);
        case 0x4261:
            return new DevL4x461(cmdExtr);
        case 0x462:
            return new DevL4x462(cmdExtr);
        case 0x470:
            return new DevL4x470(cmdExtr);

        /* F0 series */
        case 0x440:
            return new DevF0x440(cmdExtr);
        case 0x442:
            return new DevF0x442(cmdExtr);
        case 0x444:
            return new DevF0x444(cmdExtr);
        case 0x445:
            return new DevF0x445(cmdExtr);
        case 0x448:
            return new DevF0x448(cmdExtr);

        }

        return new UnknownDevice(pid);
    }
}
