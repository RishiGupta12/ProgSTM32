#!/bin/bash
# 
# Copyright (C) 2018, Rishi Gupta. All rights reserved.
# 
#########################################################################

# compile java source files
cd "$(dirname "$0")"
javac -cp ./sp-tty.jar:sp-core.jar ./Java/com/xmodemftp/XmodemFTPFileReceiver.java

# create application jar
cd Java
jar -cfe ../app.jar com.xmodemftp.XmodemFTPFileReceiver com/xmodemftp/XmodemFTPFileReceiver.class

# run jar
cd ..
java -cp .:sp-tty.jar:sp-core.jar:app.jar com.xmodemftp.XmodemFTPFileReceiver $1 $2
