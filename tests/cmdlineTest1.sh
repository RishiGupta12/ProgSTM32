#!/bin/bash
# 
# This file is part of progstm32.
# 
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
# 
# The progstm32 is free software; you can redistribute it and/or modify it 
# under the terms of the GNU Lesser General Public License as published 
# by the Free Software Foundation; either version 2.1 of the License, or 
# (at your option) any later version.
# 
# The progstm32 is distributed in the hope that it will be useful, but 
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
# License for more details.
# 
# You should have received a copy of the GNU Lesser General Public License 
# along with this library; if not, write to the Free Software Foundation,Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

PORT=/dev/ttyACM0
WRTBIN="$(dirname '$0')"/../../workspace/testhex/demo.bin
WRTHEX="$(dirname '$0')"/../../workspace/testhex/demo.hex
READFW="$(dirname '$0')"/../../workspace/testhex/rd.bin

### Don't modify anything after this line, run this test from tests folder only ###
cd "$(dirname '$0')"/../build

# read unprotect
echo -e "\n---> read unprotect"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -k

# write unprotect
echo -e "\n---> write unprotect"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -o

# get pid
echo -e "\n---> get pid"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -p

# get blid
echo -e "\n---> get blid"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -i

# mass erase
echo -e "\n---> mass erase"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -e m

# page by page erase
echo -e "\n---> page by page erase"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -e 2 5

# write bin firmware file
echo -e "\n---> write bin firmware file"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -w $WRTBIN -s 08000000 -bn

# verify + write bin firmware file
echo -e "\n---> verify + write bin firmware file"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -w $WRTBIN -s 08000000 -v -bn

# write hex firmware file
echo -e "\n---> write hex firmware file"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -w $WRTHEX -s 08000000 -ih

# verify + write hex firmware file
echo -e "\n---> verify + write hex firmware file"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -w $WRTHEX -s 08000000 -v -ih

# mass erase + verify + write hex firmware file
echo -e "\n---> mass erase + verify + write hex firmware file"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -e m -w $WRTHEX -s 08000000 -v -ih

# read memory to stdout
echo -e "\n---> read memory to stdout"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -r stdout -s 08000000 -l 1024

# read memory to file in file system
echo -e "\n---> read memory to file in file system"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -r $READFW -s 08000000 -l 1024

# write protect
echo -e "\n---> write protect"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -n 0 8

# read protect
echo -e "\n---> read protect"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -j

echo -e "\n---All Test Done---"
exit 0
