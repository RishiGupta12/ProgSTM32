When using stm32 default bootloader checklist given below can expedite the debugging process:

##Entering nto bootloader mode

1. The BOOT0 pin and nBOOT1 pins may be re-sampled when exiting from Standby mode. Consequently they must be kept in the required Boot mode configuration in Standby mode. Please refer reference manual for exact details.

2. To minimize bootloader detection time, consider keeping USB_VBUS signal pin low if only USART is used for communication between host and bootloader. PLease refer AN2606 to identify exact action items applicable for particular stm32 family.

# Memory size
1. JTAG/SWD and bootloader may not work together due to hardware/software limitations. Therefore when if we want to use bootloader we should first disconnect JTAG. Enabling and disabling memory protection may interfere with the way JTAG works.

# Others

2. Some of the memory areas are inaccessible during bootloader mode, so we should consult datasheets and reference manual of stm32 device in use in conjunction with AN2606 application note to find valid address ranges.

3. Go command requires setting proper stack setup. If the stack is not setup properly and a jump is made to location where an executable instruction is written it may not work as expected.

4. Different stm32 have different minimum size of pages that can be erased together.

5. There is no command in stm32 factory bootloader which helps in determining flash memory size of the given microcontroller.

6. All command-response operation must be atomic.

7. STM32 must be either in proper bootloader mode or out of it for consistent behaviour. 

8. Some products does not support mass erase. For such stm32 chips we can use page by page erase. Please consider reading the reference manual of the given stm32 chip to get information about flash memory size, page size and number of pages in it. Doing read protect followed by read unprotect results in mass erase but may interfere with JTAG or such debuggers as they may find it intrusive.

9. In stm32 chips, erase operation happens at the granularity of sectors. A sector may be of same size as a page or a sector may have more than one page. Please refer to reference manual of the stm32 chip in use.
