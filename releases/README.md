## Latest releases
File progstm32.zip was last updated on 11/August/2019. Its contents are given below.

| Jar file               | Functions/Features                    | Comments     |
| :------------:         |:-------------:                        | :--------:   |
| progstm32-uart.jar     | UART based sdk                        |              |
| progstm32-sp-core.jar  | library from serialpundit project     |              |
| progstm32-sp-tty.jar   | library from serialpundit project     |              |

- md5sum of progstm32.zip : d9f7177d8ab0003dd59df89253231017

## Platforms supported

| Operating System | Architecture  |  Versions            | Comments                                                  |
| :------------:   |:-------------:| :--------:           | :--------:                                                |
| Linux            | amd64         | 3.0 kernel or later  | eglibc 2.15 or later, libpthread.so.0, libudev.so.0       |
| Linux            | x86           | 3.0 kernel or later  | Intel Edision, eglibc 2.15, libpthread.so.0, libudev.so.0 |
| Windows          | amd86         | Windows 7 or later   | msvcr120.dll, setupapi.dll, advapi32.dll, kernel32.dll    |
| Windows          | x86           | Windows 7 or later   | msvcr120.dll, setupapi.dll, advapi32.dll, kernel32.dll    |
| Mac OS X         | amd64         | 10.4 or later        |                                                           |
| Mac OS X         | x86           | 10.4 kernel or later |                                                           |

Above dependencies are mainly for SerialPundit SDK.

## Signature verification

For Linux, change directory to where progstm32.zip file is downloaded and rum md5sum command to calculate checksum of file.
```
$ md5sum progstm32.zip
d9f7177d8ab0003dd59df89253231017  progstm32.zip
```

## Reference documents

1. AN2606 Rev 33 Application note
   STM32 microcontroller system memory boot mode

2. AN3155 DocID17066 Rev 7 Application note
   USART protocol used in the STM32 bootloader
