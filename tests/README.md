Please feel free to suggest more tests, use cases, environments and settings etc. by raising an issue in github which we can use for more comprehensive testing.

### Java based
01. Sanity, manager, interface, pid, allowed commands, almost all.
02. Read unprotect, write unprotect, write protect.
03. Write unprotect, write fw in RAM, Go.
04. Write and read 8/16 byte array and verify, erase, write-read-verify buffer and file.
05. Allowed commands, page erase, mass erase, extended erase, bank1/2 erase.
06. Go flash and RAM with fw flashed.
07. Trigger system reset.
08. Hex parser 109Kib, 10MiB, 20MiB, 22KiB, Detect file format.
09. Erase and write in BIN format.
10. Erase and write demo nucleo factory fw in hex format.
11. Read option byte, system memory, bootloader ID.
12. Write data in given array in RAM.
13. Write, erase, read to compare page by page erase (nucleo LO53R8).
14. Write, erase, read to compare page by page erase (nucleo L476RG).
15. Read at page start boundaries (nucleo L476RG).
16. Write protect with different number of pages.

### Linux script based
01. cmdlineTest1.sh - Without installing app in host and without entry/exit sequence; read unprotect, write unprotect, get pid, get blid, get blversion, mass erase, page by page erase, flash bin file, flash and verify bin file, flash hex file, flash and verfiy hex file, read to stdout, read to file, write protect and read protect.
02. cmdlineTest2.sh - Trigger software system reset.
03. cmdlineTest3.sh - Jump (go command) to user specified address.
04. cmdlineTest4.sh - Read and write EEPROM area.
05. cmdlineTest5.sh - enter bootloader mode, disable read protection, get blid, get pid, exit bootloader mode
06. cmdlineTest6.sh - After installing app in host and with entry/exit sequence; read unprotect, write unprotect, get pid, get blid, get blversion, mass erase, page by page erase, flash bin file, flash and verify bin file, flash hex file, flash and verfiy hex file, read to stdout, read to file, write protect and read protect.
