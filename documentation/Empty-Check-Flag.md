According to the RM0360 reference manual (STM32F030x4/x6/x8/xC and STM32F070x6/xB advanced ARM®-based 32-bit MCUs), the STM32F070x6 and STM32F030xC devices contains an empty check flag which decides the address from where CPU will start execution upon power on.

1. If this flag is set, system will boot from system memory i.e. bootloader beginning.
2. If this flag is cleared, system will boot from main flash memory beginning.

Once the stm32 device has been flashed with user application firmware there are two options to clear this empty check flag.

1. Update option byte and do power re-cycle.
2. Set OBL_LAUNCH bit in FLASH_CR register and do system reset.

So if you are using any of these devices do remember to follow any one of the above step so that user application executes instead of stm32 entering into bootloader mode.
