ProgSTM32 : Flash firmware in stm32 microcontrollers
-----------------------------------

## Features
- No restriction on addresses and address length
- API to read bootloader ID and ROM-programmed data

In STM32 (ARM Cortex-M based) microcontrollers, a default bootloader is programmed into system memory by ST Microelectronics. This bootloader can communicate to host computer through USART port using a well defined protocol. Physical USART interface which is available for communication with bootloader differs from stm32 device to device.

## Building

To build and instal sdk and app run the following commands.

```sh
./configure
make
sudo make install
```

If debug build is desired pass D=1 as option to make during build. Debug builds generates verbose logs when communicating with stm32 microcontrollers.
```sh
make D=1
```

If you want to build only sdk run command given below. Prebuilt sdk jar file () is present in release folder.
```sh
make uartsdk
```

If you want to build javadocs run command:
```sh
make javadoc
```

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
