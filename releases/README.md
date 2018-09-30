## Latest releases
Date: 30/July/2017

| Jar file       | Functions/Features                    | Comments     |
| :------------: |:-------------:                        | :--------:   |
| xx.jar         | UART based sdk                        |              |
| yy.jar         |                                       |              |
| zz.jar         | GUI application based on sdk          |              |

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

For Linux, change directory to where xxx.jar file is placed on your system and check as follows :
```
$ gpg --verify xx.jar.asc yy.jar
gpg: Signature made Friday 29 May 2015 11:28:11 AM IST using RSA key ID 2B942F12
gpg: Good signature from "rishigupta (xxxxxxxxxxx) <xxxx@gmail.com>"


## Reference documents

1. AN2606 Rev 33 Application note
   STM32 microcontroller system memory boot mode

2. AN3155 DocID17066 Rev 7 Application note
   USART protocol used in the STM32 bootloader
