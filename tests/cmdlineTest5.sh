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

PORT=/dev/ttyUSB0

### Don't modify anything after this line, run this test from tests folder only ###
cd "$(dirname '$0')"/../build

# read unprotect
echo -e "\n---> enter/exit bootloader mode"
java -cp .:sp-tty.jar:sp-core.jar:progstm32uart.jar:progstm32app.jar progstm32.ProgSTM32 -d $PORT -er -dtr 0 0 -rts 1 0 -k -p -i -t 5 -ex -dtr 0 0 -rts 0 0
