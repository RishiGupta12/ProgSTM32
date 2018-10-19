When using stm32 default bootloader checklist given below can expedite the debugging process:

1. JTAG/SWD and bootloader may not work together due to hardware/software limitations. Therefore when if we want ot use bootloader we should first disconnect JTAG. Enabling and disabling memory protection may interfere with the way JTAG works.

2. Some of the memory areas are inaccesible during bootloader mode, so we should consult datasheets and reference manual of stm32 device in use in conjuction with AN2606 application note to find valid address ranges.

3. Go command requires proper stack setup. If the stack is not setup properly and a jump is made to location where an executable instruction is written it may not work as expected.

4. Different stm32 have different minimum size of pages that can be erased together.
