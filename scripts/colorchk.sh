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

function chk_termcolor_support {
	command -v tput >/dev/null 2>&1
	if [ $? -eq 0 ]; then
		echo "define pry" >> $1
		echo "	@tput setaf 11" >> $1
		echo "	@echo \$1" >> $1
		echo "	@tput sgr0" >> $1
		echo "endef" >> $1
	else
		echo "define pry" >> $1
		echo "	@echo \$1" >> $1
		echo "endef" >> $1
	fi
}

