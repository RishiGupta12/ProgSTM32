LDR R1,=0xE000ED0C
LDR R2,=0x05FA0004
STR R2,[R1]
loopforever: b loopforever
