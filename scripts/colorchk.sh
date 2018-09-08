#!/bin/bash
#
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
#

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

