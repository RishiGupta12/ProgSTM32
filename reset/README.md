#### Triggering system reset programatically
--------------------------------------------
ARM CPU provides a facility to trigger system reset programatically through its AIRCR (Application Interrupt and Reset Control Register). To trigger reset, we should write 0x5FA to the VECTKEY field (otherwise the processor ignores the write) and 1 to the SYSRESETREQ field.

#### Using above concept
------------------------
Once the firmware has been flashed, we need to reset stm32 microcontroller. So we load a small program in RAM and execute it by using 'GO' command of bootloader. This program actually updates fields in AIRCR as explained above. The program is as follows:
```assembly
.global _start
_start:
LDR R1,=0xE000ED0C
LDR R2,=0x05FA0004
STR R2,[R1]
loopforever: b loopforever
```
#### Steps to build and integrate in Java code
----------------------------------------------
1. Create assembly source file reset.S with above code.
2. Generate reset.elf by running assebler and linker.
```assembly
arm-none-eabi-as -mthumb -o reset.o reset.S
arm-none-eabi-ld -Ttext 0x00 reset.o -o reset.elf
```
3. Generate .bin file from .elf file as stm32 reuires bin format.
```assembly
arm-none-eabi-objcopy -S -O binary reset.elf reset.bin
```
4. Generate programming style array to be integrated in Java code using xxd command.
```assembly
xxd -p -i reset.bin > reset.h
```
The reset.h file will contain something like this as a result of running above steps:
```c
unsigned char xxxxx_reset_reset_bin[] = {
  0x01, 0x49, 0x02, 0x4a, 0x0a, 0x60, 0xfe, 0xe7, 0x0c, 0xed, 0x00, 0xe0,
  0x04, 0x00, 0xfa, 0x05
};
unsigned int xxxxx_reset_reset_bin_len = 16;
```
5. Copy "0x01, 0x49, 0x02, 0x4a, 0x0a, 0x60, 0xfe, 0xe7, 0x0c, 0xed, 0x00, 0xe0,
  0x04, 0x00, 0xfa, 0x05" into flash.stm32.core.Reset.Java file.
  
  #### Understanding assembly a bit more
----------------------------------------
To see what opcodes are used by the above program and how this program will be placed in RAM, we can use objdump tool.
```assembly
arm-none-eabi-objdump -D -b binary -marm reset.bin -Mforce-thumb > output.s
```
The contents of output.s will be something like this which are more human friendly as compared to above assemebly code.
```assembly
reset.bin:     file format binary

Disassembly of section .data:

00000000 <.data>:
   0:	4901      	ldr	r1, [pc, #4]	; (0x8)
   2:	4a02      	ldr	r2, [pc, #8]	; (0xc)
   4:	600a      	str	r2, [r1, #0]
   6:	e7fe      	b.n	0x6
   8:	ed0c e000 	stc	0, cr14, [ip, #-0]
   c:	0004      	movs	r4, r0
   e:	05fa      	lsls	r2, r7, #23
```

