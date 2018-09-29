
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
