#!/bin/bash

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

PORT=/dev/ttyACM0
WRTBIN="$(dirname '$0')"/../../workspace/testhex/demo.bin
WRTHEX="$(dirname '$0')"/../../workspace/testhex/demo.hex
READFW="$(dirname '$0')"/../../workspace/testhex/rd.bin

### Don't modify anything after this line, run this test from tests folder only ###
cd "$(dirname '$0')"/../build

# system reset through software
echo -e "\n---> system reset through software"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -R

echo -e "\n---All Test Done---"
exit 0
