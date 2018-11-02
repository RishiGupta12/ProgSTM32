#### Triggering system reset programatically
--------------------------------------------
ARM CPU provides a feature to trigger system reset programatically through its AIRCR (Application Interrupt and Reset Control Register). To trigger reset, we should write 0x5FA to the VECTKEY field (otherwise the processor ignores the write) and 1 to the SYSRESETREQ field. This feature combined with in-system programming (ISP) can be very useful. Listing 1.0 shows a hand crafted code in assembly to trigger system reset.

```assembly
Listing 1.0
------------------------------------
.global _start
_start:
LDR R1,=0xE000ED0C
LDR R2,=0x05FA0004
STR R2,[R1]
loopforever: b loopforever
```

#### Using above concept
------------------------
1. When ARM processor is powered on, CPU fetches main stack pointer value from 0x00000000 address and initialize R13 (SP) register with this value.

2. CPU then reads value at address 0x00000004 and loads program counter with that value. Program counter always holds the address of the instruction to be executed next by the CPU. In normal boot sequence, this value is typically address where reset handler is stored in memory.

3. The least significant bit of the address at 0x00000004 is always 1. This is because the CPU in ARM cortex-m processors always runs in thumb mode. Note that the absolute address at which reset handler is located does not have least significant bit set to 1. For example; if value at address 0x00000004 is 0x08000269, than reset handler should be located at 0x08000268 in memory.

4. The 'go' command always make CPU jump to the given address + 4 memory location.

Armed with the knowledge from points 1,2,3 and 4, we hand crafted a small code in assembly and write it in the RAM. Then we make cpu jump to the address which will point to the memory location where code shown in listing 1.0 is put in RAM.

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

