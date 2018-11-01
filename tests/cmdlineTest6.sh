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
WRTBIN="$(dirname '$0')"/../../workspace/testhex/demo.bin
WRTHEX="$(dirname '$0')"/../../workspace/testhex/demo.hex
READFW="$(dirname '$0')"/../../workspace/testhex/rd.bin
ENSEQ="-er -dtr 0 0 -rts 1 0"
EXSEQ="-ex -dtr 0 0 -rts 0 0"

### Don't modify anything after this line, run this test from tests folder only ###
cd "$(dirname '$0')"/../build

# read unprotect
echo -e "\n---> read unprotect"
progstm32 -d $PORT -k $ENSEQ $EXSEQ

# write unprotect
echo -e "\n---> write unprotect"
progstm32 -d $PORT -o $ENSEQ $EXSEQ

# get pid
echo -e "\n---> get pid"
progstm32 -d $PORT -p $ENSEQ $EXSEQ

# get blid
echo -e "\n---> get bootloader version"
progstm32 -d $PORT -i $ENSEQ $EXSEQ

# get bootloader version
echo -e "\n---> get protocol version"
progstm32 -d $PORT -z $ENSEQ $EXSEQ

# mass erase
echo -e "\n---> mass erase"
progstm32 -d $PORT -e m $ENSEQ $EXSEQ

# page by page erase
echo -e "\n---> page by page erase"
progstm32 -d $PORT -e 2 5 $ENSEQ $EXSEQ

# write bin firmware file
echo -e "\n---> write bin firmware file"
progstm32 -d $PORT -w $WRTBIN -s 08000000 -bn $ENSEQ $EXSEQ

# verify + write bin firmware file
echo -e "\n---> verify + write bin firmware file"
progstm32 -d $PORT -w $WRTBIN -s 08000000 -v -bn $ENSEQ $EXSEQ

# write hex firmware file
echo -e "\n---> write hex firmware file"
progstm32 -d $PORT -w $WRTHEX -s 08000000 -ih $ENSEQ $EXSEQ

# verify + write hex firmware file
echo -e "\n---> verify + write hex firmware file"
progstm32 -d $PORT -w $WRTHEX -s 08000000 -v -ih $ENSEQ $EXSEQ

# mass erase + verify + write hex firmware file
echo -e "\n---> mass erase + verify + write hex firmware file"
progstm32 -d $PORT -e m -w $WRTHEX -s 08000000 -v -ih $ENSEQ $EXSEQ

# read memory to stdout
echo -e "\n---> read memory to stdout"
progstm32 -d $PORT -r stdout -s 08000000 -l 1024 $ENSEQ $EXSEQ

# read memory to file in file system
echo -e "\n---> read memory to file in file system"
progstm32 -d $PORT -r $READFW -s 08000000 -l 1024 $ENSEQ $EXSEQ

# write protect
echo -e "\n---> write protect"
progstm32 -d $PORT -n 0 8 $ENSEQ $EXSEQ

# read protect
echo -e "\n---> read protect"
progstm32 -d $PORT -j $ENSEQ $EXSEQ

echo -e "\n---All Test Done---"
exit 0
