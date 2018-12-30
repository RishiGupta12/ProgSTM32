ProgSTM32 : Flash firmware in stm32 microcontrollers
-----------------------------------

In STM32 (ARM Cortex-M based) microcontrollers, a default bootloader is programmed into system memory by ST Microelectronics. This bootloader can communicate to host computer through USART port using a well defined protocol.

**progstm32 sdk**: Java applications can implement functionality to upgrade firmware in their stm32 based product by using the APIs provided by this SDK. It implements complete protocol to communicate with factory bootloader in stm32 microcontroller. GUI based application can use this in their `'Help->Upgrade'` menu option, where if user selects this option new firmware gets flashed in the end user product. It saves time to market, application development cost and helps engineers to focus more on business use cases.

**progstm32 app**: It is a commandline application based on progstm32 SDK. It can be used as an independent flashing utility for stm32 microcontrollers. More information about options and usage can be obtained from manpage.

The progstm32 uses SerialPundit SDK for serial port communication, which is a separate project and can be [found here](https://github.com/RishiGupta12/SerialPundit).

## Features
- Erase, read and write firmware or arbitrary data in memory
- No restriction on addresses and address length
- Read bootloader ID, protocol version, ROM-programmed data, product ID
- Handle device specific quirks internally
- Enter/exit bootloader mode using DTR/RTS signals
- Robust error handling
- Enable/disable read/write protection of memory
- Resume communication with stm32 if required
- Convert intel hex firmware to plain binary
- Fully documented (javadocs,manpage) and tested
- Programmatically trigger system reset

## Build and Installation
To build and install progstm32 sdk and application run the following commands.

```sh
$ ./configure
$ make
$ sudo make install
```

In case you have custom installation of Java, set JAVA_HOME as shown below and then run ./configure script:
```sh
$ export JAVA_HOME=/home/a/packages/jdk1.8.0_31
```

If debug build is desired pass D=1 as option to make during build. Debug builds generates extra logs when communicating with stm32 microcontroller.
```sh
$ make D=1
```

If you want to build only sdk run command given below. Prebuilt sdk jar file is present in release folder.
```sh
$ make uartsdk
```

If you want to build javadocs run command:
```sh
$ make javadoc
```

## Documentation
- Manpage can be read using standard command for unix systems.
```sh
$ man progstm32
```
- Javadocs are found in documentation/uartsdk-javadocs folder.

## Testing and Bug reporting
- Automated and manual test cases are [here](tests)
- Support can be obtained through github issue tracker

## Help the project grow
- Suggest features and enhancements
- Report bugs and fixes
- Help with missing documentation or improvements
- Suggest more test cases and scenarios
- Publish an article on your blog to educate others about this project
- Provide peer support on mailing lists, forums or newsgroups

## Author, License and Copyright
- ProgSTM32 is designed, developed and maintained by Rishi Gupta. He does Linux device driver development, embedded systems design, firmware development, circuit designing, prototyping, board bring up and Linux middleware etc.     
  Linkdin profile : http://in.linkedin.com/pub/rishi-gupta/20/9b8/a10    
  
- ProgSTM32 is made available under the terms of the GNU Lesser General Public License (LGPL) v2.1. Please see LICENSE file in repository for full license text for LGPL v2.1.

- The ProgSTM32 uses [SerialPundit](https://github.com/RishiGupta12/SerialPundit) SDK for serial port communication, which is a separate project from the same author.
